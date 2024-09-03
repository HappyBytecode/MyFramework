package com.lirui.lib_common.image.photopicker;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lirui.lib_common.R;
import com.lirui.lib_common.image.photopicker.bean.Photo;
import com.lirui.lib_common.view.IntensifyImage.IntensifyImage;
import com.lirui.lib_common.view.IntensifyImage.IntensifyImageView;

import java.util.List;

/**
 * 照片大图预览
 */

public abstract class PhotoPagerAdapter extends PagerAdapter {

    private final List<Photo> photoList;
    private final LayoutInflater inflater;

    public PhotoPagerAdapter(Activity activity, List<Photo> photoList) {
        super();
        this.photoList = photoList;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return photoList == null ? 0 : photoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.picker_item_photo_preview, container, false);
        final IntensifyImageView iiv = (IntensifyImageView) view.findViewById(R.id.iiv);
        iiv.setImage(photoList.get(position).getPath());
        //LoaderManager.getLoader().loadPath(iiv, photoList.get(position).getPath());
        iiv.setOnSingleTapListener(new IntensifyImage.OnSingleTapListener() {
            @Override
            public void onSingleTap(boolean inside) {
                onItemClick(position);
            }
        });
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    abstract void onItemClick(int position);

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}