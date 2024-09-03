package com.lirui.lib_common.image.photopicker;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lirui.lib_common.R;
import com.lirui.lib_common.base.adapter.BaseQuickAdapter;
import com.lirui.lib_common.base.adapter.BaseViewHolder;
import com.lirui.lib_common.image.loader.LoaderManager;
import com.lirui.lib_common.image.photopicker.bean.Photo;
import com.lirui.lib_common.image.photopicker.bean.PhotoDirectory;
import com.lirui.lib_common.util.ToastUtils;
import com.lirui.lib_common.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册布局
 */

public class PhotoGridAdapter extends BaseQuickAdapter<Photo, BaseViewHolder> {

    public static final int DEFAULT_VIEW_TYPE = 0x00000000;
    public static final int CAMERA_VIEW_TYPE = 0x00000001;

    private final PhotoPickerActivity.IPhotoPicker iPhotoPicker;
    private final PhotoPickerConfig config;
    private String currentSelectDirectory;
    private List<PhotoDirectory> photoDirectoryList;

    public PhotoGridAdapter(@LayoutRes int layoutResId, @Nullable List<Photo> data, List<PhotoDirectory> photoDirectoryList, PhotoPickerConfig config, PhotoPickerActivity.IPhotoPicker iPhotoPicker) {
        super(layoutResId, data);
        this.photoDirectoryList = photoDirectoryList;
        this.currentSelectDirectory = MediaStoreHelper.PARENT_PATH;
        this.iPhotoPicker = iPhotoPicker;
        this.config = config;
    }

    @Override
    protected int getDataCount() {
        int size = mData == null ? 0 : mData.size();
        if (config.is_show_camera) {
            size++;
        }
        return size;
    }

    @Override
    protected int getDefItemViewType(int position) {
        if (config.is_show_camera && position == 0) {
            return CAMERA_VIEW_TYPE;
        } else {
            return DEFAULT_VIEW_TYPE;
        }
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, getLayoutId(viewType));
    }

    private int getLayoutId(int viewType) {
        if (viewType == DEFAULT_VIEW_TYPE) {
            return R.layout.picker_item_photo;
        } else {
            return R.layout.picker_item_camera_photo;
        }
    }

    @Override
    protected void convert(final int position, final BaseViewHolder helper) {
        if (getDefItemViewType(position) == DEFAULT_VIEW_TYPE) {
            Photo tempItem;
            if (config.is_show_camera) {
                tempItem = mData.get(position - 1);
            } else {
                tempItem = mData.get(position);
            }
            final Photo item = tempItem;
            helper.getView(R.id.iv_selected).setSelected(item.isSelected());
            if (item.isSelected()) {
                helper.getView(R.id.v_shadow).setVisibility(View.VISIBLE);
            } else {
                helper.getView(R.id.v_shadow).setVisibility(View.GONE);
            }
            LoaderManager.getLoader().loadPath((ImageView) helper.getView(R.id.iv_photo), item.getPath());
            helper.getView(R.id.iv_selected).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isSelected()) {
                        item.setSelected(false);
                        if (currentSelectDirectory.equals(MediaStoreHelper.PARENT_PATH)) {
                            //当前为全部图片目录
                            setPhotoFromOtherDirectorySelect(item.getId(), item.getFilePath(), false);
                        } else {
                            setPhotoFromAllDirectorySelect(item.getId(), false);
                        }
                    } else {
                        if (photoDirectoryList.get(0).getSelectedPhotosSize() < config.maxCount) {
                            item.setSelected(true);
                            if (currentSelectDirectory.equals(MediaStoreHelper.PARENT_PATH)) {
                                //当前为全部图片目录
                                setPhotoFromOtherDirectorySelect(item.getId(), item.getFilePath(), true);
                            } else {
                                setPhotoFromAllDirectorySelect(item.getId(), true);
                            }
                        } else {
                            ToastUtils.info(Utils.getContext(), Utils.getContext().getString(R.string.picker_over_max_count_tips, config.maxCount)).show();
                        }
                    }
                    iPhotoPicker.photoPickerChange(photoDirectoryList.get(0).getSelectedPhotosSize());
                    notifyItemChanged(position);
                }
            });
        } else {
            helper.addOnClickListener(R.id.iv_camera);
        }
    }

    /**
     * 设置全部相册中的选择或取消选中
     */
    private void setPhotoFromAllDirectorySelect(int id, boolean isSelect) {
        List<Photo> photoDirectory = photoDirectoryList.get(0).getPhotos();
        for (int index = 0; index < photoDirectory.size(); index++) {
            if (photoDirectory.get(index).getId() == id) {
                photoDirectory.get(index).setSelected(isSelect);
                break;
            }
        }
    }

    private void setPhotoFromOtherDirectorySelect(int id, String filePath, boolean isSelect) {

        List<Photo> photoDirectory = new ArrayList<>();
        for (int index = 1; index < photoDirectoryList.size(); index++) {
            if (photoDirectoryList.get(index).getCoverPath().equals(filePath)) {
                photoDirectory = photoDirectoryList.get(index).getPhotos();
                break;
            }
        }
        for (int index = 0; index < photoDirectory.size(); index++) {
            if (photoDirectory.get(index).getId() == id) {
                photoDirectory.get(index).setSelected(isSelect);
                break;
            }
        }
    }

    public void notifyDataSetChanged(String coverPath) {
        this.currentSelectDirectory = coverPath;
        notifyDataSetChanged();
    }
}
