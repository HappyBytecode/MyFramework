package com.lirui.lib_common.image.photopicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lirui.lib_common.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.base.BaseApplication;
import com.lirui.lib_common.base.adapter.BaseQuickAdapter;
import com.lirui.lib_common.constant.IntentResultConstants;
import com.lirui.lib_common.constant.Permissions;
import com.lirui.lib_common.image.photopicker.bean.Photo;
import com.lirui.lib_common.image.photopicker.bean.PhotoDirectory;
import com.lirui.lib_common.util.FileUtils;
import com.lirui.lib_common.util.IntentUtils;
import com.lirui.lib_common.util.PopupWindowUtils;
import com.lirui.lib_common.util.ToastUtils;
import com.lirui.lib_common.util.Utils;
import com.lirui.lib_common.view.ItemDivider;
import com.lirui.lib_common.view.ToolBarHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 照片选择
 */
public class PhotoPickerActivity extends AbsBaseActivity implements View.OnClickListener {


    public static final String EXTRA_SHOW_GIF = "SHOW_GIF";
    private static final String BUILDER = "builder";
    private static final int COUNT_MAX = 4;//最多显示4个相册

    private PhotoPickerConfig config;//配置相关

    private TextView mTvSure;
    private RecyclerView mRcvContent;
    private LinearLayout mLlSelectDir;
    private TextView mtvDirName;
    private TextView mTvPreview;
    private ToolBarHelper toolbarHelper;
    private ListPopupWindow listPopupWindow;

    private String filePath = MediaStoreHelper.PARENT_PATH;
    private String cameraFilePath = "";//拍照图片保存路径

    //获取到的相册
    private List<PhotoDirectory> photoDirectoryList;
    //正在显示的图片
    private ArrayList<Photo> photoList = new ArrayList<>();
    //图片展示adapter
    private PhotoGridAdapter photoGridAdapter;
    //相册展示adapter
    private PopupDirectoryListAdapter photoDirectoryAdapter;
    //选中的相册position
    private int selection = 0;

    public static void start(final Activity activity, final PhotoPickerConfig config) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Permissions.STORAGE_CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            PhotoPickerConfig defaultConfig = config;
                            if (defaultConfig == null) {
                                defaultConfig = new PhotoPickerConfig();
                            }
                            Intent intent = new Intent(activity, PhotoPickerActivity.class);
                            intent.putExtra(BUILDER, defaultConfig);
                            activity.startActivityForResult(intent, IntentResultConstants.PICKER_IMAGE_REQUEST_CODE);
                        } else {
                            ToastUtils.warning(activity, activity.getString(R.string.permissionsError,"读写权限，拍照权限")).show();
                        }
                    }
                });
    }

    public static void start(final Fragment fragment, final PhotoPickerConfig config) {
        RxPermissions rxPermissions = new RxPermissions(fragment.getActivity());
        rxPermissions.request(Permissions.STORAGE_CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            PhotoPickerConfig defaultConfig = config;
                            if (defaultConfig == null) {
                                defaultConfig = new PhotoPickerConfig();
                            }
                            Intent intent = new Intent(fragment.getActivity(), PhotoPickerActivity.class);
                            intent.putExtra(BUILDER, defaultConfig);
                            fragment.startActivityForResult(intent, IntentResultConstants.PICKER_IMAGE_REQUEST_CODE);
                        } else {
                            ToastUtils.warning(fragment.getActivity(), fragment.getString(R.string.permissionsError,"读写权限，拍照权限")).show();
                        }
                    }
                });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_photo_picker;
    }

    @Override
    protected void getExtra(Intent intent) {
        config = (PhotoPickerConfig) intent.getSerializableExtra(BUILDER);
        if (config == null) {
            config = new PhotoPickerConfig();
        }
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initView();
        initListener();
        initData();
    }

    private void initView() {
        toolbarHelper = new ToolBarHelper.Builder(this).setTitle("图片").setRightText2("完成", this).builder();
        mTvSure = (TextView) toolbarHelper.getViewById(R.id.tv_right_2);
        mTvSure.setTextColor(getResources().getColor(R.color.select_preview));

        mRcvContent = (RecyclerView) findViewById(R.id.rcv_content);
        mLlSelectDir = (LinearLayout) findViewById(R.id.ll_select_dir);
        mtvDirName = (TextView) findViewById(R.id.tv_dir_name);
        mTvPreview = (TextView) findViewById(R.id.tv_preview);

        mRcvContent.setLayoutManager(new GridLayoutManager(this, config.column));
        mRcvContent.addItemDecoration(new ItemDivider(2, getResources().getColor(R.color.colorAccent)));

        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAnimationStyle(R.style.popwindow);
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setAnchorView(mLlSelectDir);
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
    }

    private void initListener() {
        //选择相册
        mLlSelectDir.setOnClickListener(this);
        //预览
        mTvPreview.setOnClickListener(this);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selection = position;
                //1.更改PopupWindow布局
                for (PhotoDirectory photoDirectory : photoDirectoryList) {
                    photoDirectory.setSelected(false);
                }
                photoDirectoryList.get(position).setSelected(true);
                freshDirectoryAdapter();
                //2.更改相册
                photoList.clear();
                photoList.addAll(photoDirectoryList.get(position).getPhotos());
                filePath = photoDirectoryList.get(position).getCoverPath();
                freshPhotoAdapter(filePath);

                listPopupWindow.dismiss();

                mtvDirName.setText(photoDirectoryList.get(position).getName());
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowUtils.clearDim(mRcvContent);
            }
        });
    }

    private void initData() {
        Bundle mediaStoreArgs = new Bundle();
        mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, config.is_show_gif);
        MediaStoreHelper.getPhotoDirs(this, mediaStoreArgs,
                new MediaStoreHelper.PhotosResultCallback() {
                    @Override
                    public void onResultCallback(List<PhotoDirectory> dirs) {
                        photoDirectoryList = dirs;
                        if (photoDirectoryList != null && photoDirectoryList.size() > 1) {
                            mLlSelectDir.setEnabled(true);
                            //initSelectPhoto(config.original_photos);
                            photoList.clear();
                            photoList.addAll(photoDirectoryList.get(0).getPhotos());
                            freshPhotoAdapter(MediaStoreHelper.PARENT_PATH);
                            freshDirectoryAdapter();
                        } else if (!config.is_show_camera) {
                            ToastUtils.error(Utils.getContext(), "不存在相册").show();
                            finish();
                        }
                    }
                });
    }

    /**
     * 刷新照片布局
     *
     * @param coverPath
     */
    private void freshPhotoAdapter(String coverPath) {
        if (photoGridAdapter == null) {
            photoGridAdapter = new PhotoGridAdapter(R.layout.picker_item_photo, photoList, photoDirectoryList, config, new IPhotoPicker() {
                @Override
                public void photoPickerChange(int currentCount) {
                    freshLocalView(currentCount);
                }
            });
            photoGridAdapter.openLoadAnimation();
            mRcvContent.setAdapter(photoGridAdapter);
            photoGridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    PhotoPagerActivity.startActivityForResult(PhotoPickerActivity.this, photoList, photoDirectoryList.get(0).getSelectedPhotosSize(), config.maxCount, position);
                }
            });
            photoGridAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    if (view.getId() == R.id.iv_camera) {
                        if (photoDirectoryList.get(0).getSelectedPhotosSize() >= config.maxCount) {
                            ToastUtils.info(Utils.getContext(), Utils.getContext().getString(R.string.picker_over_max_count_tips, config.maxCount)).show();
                            return;
                        }
                        Intent intent = dispatchTakePictureIntent();
                        startActivityForResult(intent, IntentResultConstants.REQUEST_TAKE_PHOTO);
                    }
                }
            });
        } else {
            photoGridAdapter.notifyDataSetChanged(coverPath);
        }
    }

    /**
     * 选中数量发生改变时，更改当前布局显示
     */
    private void freshLocalView(int currentCount) {
        if (currentCount > 0) {
            mTvPreview.setEnabled(true);
            mTvSure.setEnabled(true);
            mTvPreview.setText(getString(R.string.picker_show_with_count, currentCount));
            mTvSure.setText(getString(R.string.picker_done_with_count, currentCount, config.maxCount));
        } else {
            mTvPreview.setEnabled(false);
            mTvSure.setEnabled(false);
            mTvPreview.setText("预览");
            mTvSure.setText("完成");
        }
    }

    /**
     * 刷新相册布局
     */
    private void freshDirectoryAdapter() {
        if (photoDirectoryAdapter == null) {
            photoDirectoryAdapter = new PopupDirectoryListAdapter(this, photoDirectoryList);
            listPopupWindow.setAdapter(photoDirectoryAdapter);
        } else {
            photoDirectoryAdapter.notifyDataSetChanged();
        }
        adjustHeight();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_right_2) {//完成
            setOk();
            return;
        }
        if (id == R.id.ll_select_dir) {//更改相册
            if (listPopupWindow.isShowing()) {
                listPopupWindow.dismiss();
            } else {
                adjustHeight();
                listPopupWindow.show();
                listPopupWindow.setSelection(selection);
                PopupWindowUtils.applyDim(mRcvContent);
            }
            return;
        }
        if (id == R.id.tv_preview) {//预览
            PhotoPagerActivity.startActivityForResult(PhotoPickerActivity.this, photoDirectoryList.get(0).getSelectedPhotos(), photoDirectoryList.get(0).getSelectedPhotosSize(), config.maxCount, 0);
        }
    }

    /**
     * 调整布局
     */
    public void adjustHeight() {
        if (photoDirectoryAdapter == null) return;
        int count = photoDirectoryAdapter.getCount();
        count = count < COUNT_MAX ? count : COUNT_MAX;
        if (listPopupWindow != null) {
            listPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.item_with_image));
        }
    }

    interface IPhotoPicker {
        //photo选择更改
        void photoPickerChange(int count);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentResultConstants.RESULT_PHOTO_PREVIEW_SURE
                && resultCode == RESULT_CANCELED) {
            //直接按返回键返回
            ArrayList<Photo> photoList = data.getParcelableArrayListExtra(PhotoPagerActivity.PHOTOS);
            if (photoList == null) {
                return;
            }
            initSelectPhoto(photoList);
            freshPhotoAdapter(filePath);
            freshLocalView(photoDirectoryList.get(0).getSelectedPhotosSize());
            return;
        }
        if (requestCode == IntentResultConstants.RESULT_PHOTO_PREVIEW_SURE
                && resultCode == RESULT_OK) {
            //按确定键返回
            ArrayList<Photo> photoList = data.getParcelableArrayListExtra(PhotoPagerActivity.PHOTOS);
            if (photoList == null) {
                return;
            }
            for (Photo photo : photoList) {
                //全部种是否含有此图片，存在设置为先择
                List<Photo> setSelectedByList = photoDirectoryList.get(0).getPhotos();
                setSelectedByList(photo, setSelectedByList);
            }
            setOk();
            return;
        }
        if (requestCode == IntentResultConstants.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            ArrayList<Photo> myPhotos = photoDirectoryList.get(0).getSelectedPhotos();
            myPhotos.add(new Photo(-1, cameraFilePath));
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(PhotoPagerActivity.PHOTOS, myPhotos);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
    }

    /**
     * 设置图片选择完成
     */
    private void setOk() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PhotoPagerActivity.PHOTOS, photoDirectoryList.get(0).getSelectedPhotos());
        setResult(RESULT_OK, intent);
        finish();
    }

    //初始化已选中的图片
    private void initSelectPhoto(List<Photo> photoList) {
        for (Photo photo : photoList) {
            //全部种是否含有此图片，存在设置为先择
            List<Photo> setSelectedByList = photoDirectoryList.get(0).getPhotos();
            setSelectedByList(photo, setSelectedByList);
            //查找该相册属于哪个相册
            for (PhotoDirectory photoDirectory : photoDirectoryList) {
                if (photo.getFilePath().equals(photoDirectory.getCoverPath())) {
                    setSelectedByList(photo, photoDirectory.getPhotos());
                    break;
                }
            }
        }
    }

    //查找相片列表中的位置，并设置为选择
    private void setSelectedByList(Photo photo, List<Photo> photoList) {
        for (Photo photoOther : photoList) {
            if (photoOther.getPath().equals(photo.getPath())) {
                photoOther.setSelected(photo.isSelected());
                break;
            }
        }
    }

    /**
     * 拍照Intent
     */
    private Intent dispatchTakePictureIntent() {
        cameraFilePath = BaseApplication.getInstance().imagePath + FileUtils.getFileName() + ".jpg";
        return IntentUtils.getCaptureIntent(FileUtils.getFileUri(new File(cameraFilePath)));
    }
}
