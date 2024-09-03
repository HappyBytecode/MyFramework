package com.lirui.lib_common.image.photopicker;

import com.lirui.lib_common.image.photopicker.bean.Photo;
import com.lirui.lib_common.image.photopicker.bean.PhotoDirectory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donglua on 16/6/25.
 * Builder class to ease Intent setup.
 */
public class PhotoPickerConfig implements Serializable {

    public final static int DEFAULT_MAX_COUNT = 9;
    public final static int DEFAULT_COLUMN_NUMBER = 3;

    public int maxCount = DEFAULT_MAX_COUNT;//选择的图片最大数量
    public boolean is_show_camera = true;//是否显示拍照
    public boolean is_show_gif = true;//是否可选gif图片
    public int column = DEFAULT_COLUMN_NUMBER;//显示图片的的默认列数
    //public List<Photo> original_photos = new ArrayList<>();//传入已选择的图片

    public PhotoPickerConfig setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public PhotoPickerConfig isShowCamera(boolean is_show_camera) {
        this.is_show_camera = is_show_camera;
        return this;
    }

    public PhotoPickerConfig isShowGif(boolean is_show_gif) {
        this.is_show_gif = is_show_gif;
        return this;
    }

    public PhotoPickerConfig setColumn(int column) {
        this.column = column;
        return this;
    }

   /* public PhotoPickerConfig setOriginalPhotos(List<Photo> original_photos) {
        this.original_photos = original_photos;
        return this;
    }*/
}
