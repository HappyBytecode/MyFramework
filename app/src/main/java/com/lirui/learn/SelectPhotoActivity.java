package com.lirui.learn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lirui.learn.adapter.PhotoGridAdapter;
import com.lirui.learndemo.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.base.adapter.BaseQuickAdapter;
import com.lirui.lib_common.constant.IntentResultConstants;
import com.lirui.lib_common.image.photopicker.PhotoPagerActivity;
import com.lirui.lib_common.image.photopicker.PhotoPickerActivity;
import com.lirui.lib_common.image.photopicker.PhotoPickerConfig;
import com.lirui.lib_common.image.photopicker.bean.Photo;
import com.lirui.lib_common.view.ItemDivider;

import java.util.ArrayList;

public class SelectPhotoActivity extends AbsBaseActivity {

    private ArrayList<String> photoList = new ArrayList<>();
    private PhotoGridAdapter photoGridAdapter;
    private int MAX_COUNT = 9;//图片选择的最大数量

    private RecyclerView mRvPhoto;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_select_photo;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mRvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        mRvPhoto.setLayoutManager(new GridLayoutManager(this, 6));
        mRvPhoto.addItemDecoration(new ItemDivider(8, getResources().getColor(R.color.transparent)));
        freshPhotoAdapter();
    }

    /**
     * 刷新照片布局
     */
    private void freshPhotoAdapter() {
        if (photoGridAdapter == null) {
            photoGridAdapter = new PhotoGridAdapter(R.layout.item_photo, photoList, 9);
            photoGridAdapter.openLoadAnimation();
            mRvPhoto.setAdapter(photoGridAdapter);
            photoGridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    PhotoPagerActivity.startActivityForResult(SelectPhotoActivity.this, photoList, position);
                }
            });
            photoGridAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    if (view.getId() == R.id.iv_add) {
                        PhotoPickerActivity.start(SelectPhotoActivity.this, new PhotoPickerConfig().setMaxCount(MAX_COUNT - photoList.size()));
                    }
                }
            });
        } else {
            photoGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentResultConstants.PICKER_IMAGE_REQUEST_CODE
                && resultCode == RESULT_OK) {//由照片选择返回
            ArrayList<Photo> photos = data.getParcelableArrayListExtra(PhotoPagerActivity.PHOTOS);
            for (Photo photo : photos) {
                photoList.add(photo.getPath());
            }
            freshPhotoAdapter();
        }
        if (requestCode == IntentResultConstants.RESULT_PHOTO_PREVIEW_SURE) {//有照片预览返回
            ArrayList<Photo> photos = data.getParcelableArrayListExtra(PhotoPagerActivity.PHOTOS);
            photoList.clear();
            for (Photo photo : photos) {
                photoList.add(photo.getPath());
            }
            freshPhotoAdapter();
        }
    }
}
