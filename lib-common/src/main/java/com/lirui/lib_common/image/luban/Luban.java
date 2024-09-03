package com.lirui.lib_common.image.luban;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.UiThread;

import com.lirui.lib_common.base.BaseApplication;
import com.lirui.lib_common.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Luban implements Handler.Callback {
    private static final String TAG = "Luban";

    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private HashMap<String, String> compressFilePath = new HashMap<>();
    private OnCompressListener onCompressListener;

    private Handler mHandler;

    private Luban(Builder builder) {
        for (String filePath : builder.filePaths) {
            compressFilePath.put(filePath, "");
        }
        this.onCompressListener = builder.onCompressListener;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with() {
        return new Builder();
    }

    /**
     * Returns a file with a cache audio name in the private cache directory.
     * 修改缓存路径
     */
    private File getImageCacheFile() {
        return new File(BaseApplication.getInstance().cachePath + "compress_" + FileUtils.getFileName() + ".jpg");
    }


    /**
     * start asynchronous compress thread
     */
    @UiThread
    private void launch() {
        if (compressFilePath.size() <= 0 && onCompressListener != null) {
            onCompressListener.onError(new NullPointerException("image file cannot be null"));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
                    for (String path : compressFilePath.keySet()) {
                        File result = new Engine(new File(path), getImageCacheFile()).compress();
                        compressFilePath.put(path, result.getAbsolutePath());
                    }
                    Collection<String> compressFilePaths = compressFilePath.values();
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, new ArrayList<>(compressFilePaths)));
                } catch (IOException e) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
                }
            }
        }).start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (onCompressListener == null) return false;

        switch (msg.what) {
            case MSG_COMPRESS_START:
                onCompressListener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                onCompressListener.onSuccess((ArrayList<String>) msg.obj);
                break;
            case MSG_COMPRESS_ERROR:
                onCompressListener.onError((Throwable) msg.obj);
                break;
        }
        return false;
    }

    public static class Builder {
        private ArrayList<String> filePaths = new ArrayList<>();
        private OnCompressListener onCompressListener;

        Builder() {

        }

        private Luban build() {
            return new Luban(this);
        }

        public Builder load(ArrayList<String> filePaths) {
            this.filePaths = filePaths;
            return this;
        }

        public Builder load(String filePath) {
            filePaths.add(filePath);
            return this;
        }

        public Builder setCompressListener(OnCompressListener listener) {
            this.onCompressListener = listener;
            return this;
        }

        public void launch() {
            build().launch();
        }

    }
}