package com.lirui.lib_common.image.photopicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lirui.lib_common.R;
import com.lirui.lib_common.image.loader.LoaderManager;
import com.lirui.lib_common.image.photopicker.bean.PhotoDirectory;

import java.util.ArrayList;
import java.util.List;


/**
 * 相册适配器
 */
public class PopupDirectoryListAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private List<PhotoDirectory> directories = new ArrayList<>();

    public PopupDirectoryListAdapter(Context context, List<PhotoDirectory> directories) {
        mLayoutInflater = LayoutInflater.from(context);
        this.directories = directories;
    }


    @Override
    public int getCount() {
        return directories == null ? 0 : directories.size();
    }


    @Override
    public PhotoDirectory getItem(int position) {
        return directories.get(position);
    }


    @Override
    public long getItemId(int position) {
        return directories.get(position).hashCode();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.picker_item_directory, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(directories.get(position));

        return convertView;
    }

    private class ViewHolder {

        public ImageView ivCover;
        public TextView tvName;
        public TextView tvCount;
        private ImageView mIvSelected;

        public ViewHolder(View rootView) {
            ivCover = (ImageView) rootView.findViewById(R.id.iv_dir_cover);
            tvName = (TextView) rootView.findViewById(R.id.tv_dir_name);
            tvCount = (TextView) rootView.findViewById(R.id.tv_dir_count);
            mIvSelected = (ImageView) rootView.findViewById(R.id.iv_selected);
        }

        public void bindData(PhotoDirectory directory) {
            LoaderManager.getLoader().loadPath(ivCover, directory.getImagePath());
            tvName.setText(directory.getName());
            tvCount.setText(tvCount.getContext().getString(R.string.picker_image_count, directory.getPhotos().size()));
            if (directory.isSelected()) {
                mIvSelected.setVisibility(View.VISIBLE);
            } else {
                mIvSelected.setVisibility(View.INVISIBLE);
            }
        }
    }

}
