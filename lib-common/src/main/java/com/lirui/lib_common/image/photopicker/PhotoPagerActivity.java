package com.lirui.lib_common.image.photopicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lirui.lib_common.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.constant.IntentResultConstants;
import com.lirui.lib_common.image.photopicker.bean.Photo;
import com.lirui.lib_common.util.LogUtils;
import com.lirui.lib_common.util.ToastUtils;
import com.lirui.lib_common.view.ToolBarHelper;

import java.util.ArrayList;

/**
 * 展示相册图片
 */

public class PhotoPagerActivity extends AbsBaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static String PHOTOS = "photos";
    private static String SELECT_PHOTO = "select_photo";
    private static String CURRENT_COUNT = "current_count";
    private static String MAX_COUNT = "max_count";
    private static String CURRENT_POSITION = "current_position";

    private ToolBarHelper toolbarHelper;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private ImageView mToolbarRightImage;
    private TextView mToolbarRightText;

    private ViewPager mVpPager;
    private RelativeLayout mRlBottom;
    private TextView mTvEdit;
    private LinearLayout mLlSelect;
    private ImageView mIvSelected;

    private PhotoPagerAdapter mAdapter;

    //正在显示的图片
    private int currentCount;
    private int maxCount;
    private int currentPosition = 0;
    private ArrayList<Photo> photoList = new ArrayList<>();
    private boolean is_select_photo;

    /**
     * 打开当前Activity
     *
     * @param activity  打开当前Activity的上下文
     * @param photoList 需要显示的List
     *                  isSelectPhoto 是否是显示选择图片，
     *                  true:由选择图片进入，可以进行图片选择；
     *                  false:由选择完成的界面展示，可以进行图片的修改
     */
    public static void startActivityForResult(Activity activity, ArrayList<Photo> photoList, int currentCount, int maxCount, int currentPosition) {
        Intent intent = new Intent(activity, PhotoPagerActivity.class);
        intent.putParcelableArrayListExtra(PHOTOS, photoList);
        intent.putExtra(SELECT_PHOTO, true);
        intent.putExtra(CURRENT_COUNT, currentCount);
        intent.putExtra(MAX_COUNT, maxCount);
        intent.putExtra(CURRENT_POSITION, currentPosition);
        activity.startActivityForResult(intent, IntentResultConstants.RESULT_PHOTO_PREVIEW_SURE);
    }

    /**
     * 选择完照片进行展示时使用
     */
    public static void startActivityForResult(Activity activity, ArrayList<String> photoList, int currentPosition) {
        Intent intent = new Intent(activity, PhotoPagerActivity.class);
        intent.putParcelableArrayListExtra(PHOTOS, path2Photo(photoList));
        intent.putExtra(SELECT_PHOTO, false);
        intent.putExtra(CURRENT_POSITION, currentPosition);
        intent.putExtra(CURRENT_COUNT, photoList == null ? 0 : photoList.size());
        activity.startActivityForResult(intent, IntentResultConstants.RESULT_PHOTO_PREVIEW_SURE);
    }

    /**
     * 将String转为Photo
     */
    private static ArrayList<Photo> path2Photo(ArrayList<String> photoList) {
        ArrayList<Photo> list = new ArrayList<>();
        for (String path : photoList) {
            list.add(new Photo(path));
        }
        return list;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_photo_pager;
    }

    @Override
    protected void getExtra(Intent intent) {
        photoList = intent.getParcelableArrayListExtra(PHOTOS);
        is_select_photo = intent.getBooleanExtra(SELECT_PHOTO, false);
        currentPosition = intent.getIntExtra(CURRENT_POSITION, 0);
        currentCount = intent.getIntExtra(CURRENT_COUNT, 0);
        if (is_select_photo) {
            maxCount = intent.getIntExtra(MAX_COUNT, 9);
        }
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initView();
        initDate();
    }

    private void initView() {
        toolbarHelper = new ToolBarHelper.Builder(this).setLeftIcon(this).setTitle(getString(R.string.picker_image_current, 1, photoList.size())).builder();
        mToolbar = (Toolbar) toolbarHelper.getViewById(R.id.toolbar);
        mToolbarTitle = (TextView) toolbarHelper.getViewById(R.id.tv_title);
        mToolbarRightImage = (ImageView) toolbarHelper.getViewById(R.id.iv_right_2);
        mToolbarRightText = (TextView) toolbarHelper.getViewById(R.id.tv_right_2);
        mVpPager = (ViewPager) findViewById(R.id.vp_pager);
        mRlBottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        mTvEdit = (TextView) findViewById(R.id.tv_edit);
        mLlSelect = (LinearLayout) findViewById(R.id.ll_select);
        mIvSelected = (ImageView) findViewById(R.id.iv_selected);

        Toolbar.LayoutParams lp = (Toolbar.LayoutParams) mToolbarTitle.getLayoutParams();
        lp.gravity = Gravity.LEFT;
        mToolbarTitle.setLayoutParams(lp);

        if (is_select_photo) {
            mToolbarRightText.setVisibility(View.VISIBLE);
            mToolbarRightText.setText(getString(R.string.picker_done_with_count, currentCount, maxCount));
            mRlBottom.setVisibility(View.VISIBLE);
            mToolbarRightText.setOnClickListener(this);

            mIvSelected.setSelected(photoList.get(currentPosition).isSelected());
        } else {
            mToolbarRightImage.setImageResource(R.mipmap.ic_photo_delete);
            mToolbarRightImage.setVisibility(View.VISIBLE);
            mRlBottom.setVisibility(View.GONE);
            mToolbarRightImage.setOnClickListener(this);
        }

        mTvEdit.setOnClickListener(this);
        mLlSelect.setOnClickListener(this);
    }

    private void initDate() {
        freshAdapter();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.iv_left) {
            setResultCancel();
            finish();
            return;
        }
        if (viewId == R.id.iv_right_2) {
            //提示是否删除此图片
            //删除已选择的图片
            photoList.remove(currentPosition);
            freshAdapter();
            if (currentPosition > photoList.size() - 1) {
                currentPosition = photoList.size() - 1;
            }
            currentCount--;
            if (currentCount == 0) {
                setResultCancel();
                finish();
            }
            mToolbarTitle.setText(getString(R.string.picker_image_current, currentPosition + 1, photoList.size()));
            return;
        }
        if (viewId == R.id.tv_right_2) {
            //完成已选择的图片
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(PHOTOS, photoList);
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        if (viewId == R.id.tv_edit) {
            //编辑
            ToastUtils.info(this, "功能正在研发中").show();
            return;
        }
        if (viewId == R.id.ll_select) {
            if (!photoList.get(currentPosition).isSelected()) {
                //更改选择
                if (currentCount >= maxCount) {
                    ToastUtils.info(this, getString(R.string.picker_over_max_count_tips, maxCount)).show();
                    return;
                }
                currentCount++;
                photoList.get(currentPosition).setSelected(true);
            } else {
                currentCount--;
                photoList.get(currentPosition).setSelected(false);
            }
            mIvSelected.setSelected(photoList.get(currentPosition).isSelected());
            mToolbarRightText.setText(getString(R.string.picker_done_with_count, currentCount, maxCount));
            return;
        }

    }

    /**
     * 刷新适配器布局
     */
    private void freshAdapter() {
        if (mAdapter == null) {
            mAdapter = new PhotoPagerAdapter(this, photoList) {
                @Override
                void onItemClick(int position) {
                    if (mToolbar.getVisibility() == View.VISIBLE) {
                        mToolbar.setVisibility(View.GONE);
                        mRlBottom.setVisibility(View.GONE);
                    } else {
                        mToolbar.setVisibility(View.VISIBLE);
                        mRlBottom.setVisibility(View.VISIBLE);
                    }
                }
            };
            mVpPager.setAdapter(mAdapter);
            mVpPager.addOnPageChangeListener(this);
            mVpPager.setCurrentItem(currentPosition);
            mVpPager.setOffscreenPageLimit(5);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        mToolbarTitle.setText(getString(R.string.picker_image_current, currentPosition + 1, photoList.size()));
        if (is_select_photo) {
            mIvSelected.setSelected(photoList.get(position).isSelected());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setResultCancel() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PHOTOS, photoList);
        setResult(RESULT_CANCELED, intent);
    }

    //按返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            setResultCancel();
            finish();
            return true;//返回true，把事件消费掉，不会继续调用onBackPressed
        }
        return super.dispatchKeyEvent(event);
    }
}
