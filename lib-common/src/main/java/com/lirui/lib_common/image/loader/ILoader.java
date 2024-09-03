package com.lirui.lib_common.image.loader;

import android.widget.ImageView;

import com.lirui.lib_common.R;
import com.lirui.lib_common.view.IntensifyImage.IntensifyImageView;

import java.io.File;

/**
 * 图片加载接口
 */
public interface ILoader {

    void loadPath(ImageView target, String url);

    void loadPath(IntensifyImageView target, String url);

    void loadResource(ImageView target, int resId);

    void loadAssets(ImageView target, String assetName);

    void loadFile(ImageView target, File file);

    void loadPath(ImageView target, String url, Options options);

    void loadPath(IntensifyImageView target, String url, Options options);

    void loadResource(ImageView target, int resId, Options options);

    void loadAssets(ImageView target, String assetName, Options options);

    void loadFile(ImageView target, File file, Options options);

    void clearMemoryCache();

    void clearDiskCache();

    void clear();

    enum TYPE {
        fitCenter,
        centerCrop,
        circleCrop,
        centerInside
    }

    class Options {

        private Options options = this;

        public static final int RES_NONE = -1;
        public int loadingResId = R.mipmap.ic_loading;//加载中的资源id
        public int loadErrorResId = R.mipmap.ic_load_error;//加载失败的资源id
        public int loadNullResId = R.mipmap.ic_load_empty;//加载资源为空时显示
        public OnProgressListener downLoadProgressListener;//下载进度监听
        public TYPE scaleType = TYPE.fitCenter;


        public static Options defaultOptions() {
            return new Options();
        }

        public Options(int loadingResId, int loadErrorResId, int loadNullResId) {
            this.loadingResId = loadingResId;
            this.loadErrorResId = loadErrorResId;
            this.loadNullResId = loadNullResId;
        }

        public Options() {
        }

        public Options loadingResId(int loadingResId) {
            this.loadingResId = loadingResId;
            return options;
        }

        public Options loadErrorResId(int loadErrorResId) {
            this.loadErrorResId = loadErrorResId;
            return options;
        }

        public Options loadNullResId(int loadNullResId) {
            this.loadNullResId = loadNullResId;
            return options;
        }

        public Options OnProgressListener(OnProgressListener downLoadProgressListener) {
            this.downLoadProgressListener = downLoadProgressListener;
            return options;
        }

        public Options scaleType(TYPE scaleType) {
            this.scaleType = scaleType;
            return options;
        }
    }
}
