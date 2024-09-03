package com.lirui.lib_common.image.loader;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.lirui.lib_common.constant.DownloadStatusEnum;
import com.lirui.lib_common.util.FormatTools;
import com.lirui.lib_common.util.Utils;
import com.lirui.lib_common.view.IntensifyImage.IntensifyImageView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 使用Glide框架加载图片
 */
class GlideLoader implements ILoader {

    /**
     * 下载事件监听列表
     */
    private static Map<String, ArrayList<WeakReference<OnProgressListener>>> listeners
            = Collections.synchronizedMap(new HashMap<String, ArrayList<WeakReference<OnProgressListener>>>());

    /**
     * 初始化okhttp
     */
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    return response.newBuilder()
                            .body(new ProgressResponseBody(request.url().toString(), response.body(), LISTENER))
                            .build();
                }
            })
            .build();

    @Override
    public void loadPath(ImageView target, String url) {
        load(Glide.with(target.getContext()).load(url), url, target, null);
    }

    @Override
    public void loadPath(ImageView target, String url, Options options) {
        load(Glide.with(target.getContext()).load(url), url, target, options);
    }

    @Override
    public void loadPath(IntensifyImageView target, String url) {
        load(Glide.with(target.getContext()).asBitmap().load(url), url, target, null);
    }

    @Override
    public void loadPath(IntensifyImageView target, String url, Options options) {
        load(Glide.with(target.getContext()).asBitmap().load(url), url, target, options);
    }

    @Override
    public void loadResource(ImageView target, int resId) {
        load(Glide.with(target.getContext()).load(resId), target, null);
    }

    @Override
    public void loadResource(ImageView target, int resId, Options options) {
        load(Glide.with(target.getContext()).load(resId), target, options);
    }

    @Override
    public void loadAssets(ImageView target, String assetName) {
        load(Glide.with(target.getContext()).load("file:///android_asset/" + assetName), target, null);
    }

    @Override
    public void loadAssets(ImageView target, String assetName, Options options) {
        load(Glide.with(target.getContext()).load("file:///android_asset/" + assetName), target, options);
    }

    @Override
    public void loadFile(ImageView target, File file) {
        load(Glide.with(target.getContext()).load(file), target, null);
    }

    @Override
    public void loadFile(ImageView target, File file, Options options) {
        load(Glide.with(target.getContext()).load(file), target, options);
    }

    @Override
    public void clearMemoryCache() {
        Glide.get(Utils.getContext()).clearMemory();
    }

    @Override
    public void clearDiskCache() {
        Glide.get(Utils.getContext()).clearDiskCache();
    }

    @Override
    public void clear() {
        Glide.get(Utils.getContext()).clearMemory();
        Glide.get(Utils.getContext()).clearDiskCache();
    }

    /**
     * 除网络之外的其他路径加载图片
     */
    private void load(RequestBuilder request, final ImageView imageView, Options options) {
        initRequestBuilder(request, options);
        request.into(imageView);
    }

    /**
     * 网络加载图片
     */
    private void load(RequestBuilder request, final String url, final ImageView imageView, Options options) {
        initRequestBuilder(request, options);
        if (options != null) {
            setDownLoadProgressListener(request, url, options.downLoadProgressListener);
        }
        request.into(imageView);
    }

    /**
     * 网络加载图片
     */
    private void load(RequestBuilder request, final String url, final IntensifyImageView imageView, Options options) {
        initRequestBuilder(request, options);
        if (options != null) {
            setDownLoadProgressListener(request, url, options.downLoadProgressListener);
        }
        request.into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition transition) {
                imageView.setImage(FormatTools.Bitmap2InputStream(resource));
            }
        });
    }

    /**
     * 设置下载进度监听
     */
    private void setDownLoadProgressListener(RequestBuilder request, final String url, final OnProgressListener downLoadProgressListener) {
        //下载进度监听
        if (downLoadProgressListener != null) {
            addProgressListener(url, downLoadProgressListener);

            request.listener(new RequestListener() {

                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    //资源下载失败
                    downLoadProgressListener.onProgressUpdate(url, -1, DownloadStatusEnum.ERROR);
                    removeProgressListener(url, downLoadProgressListener);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    //资源下载成功
                    downLoadProgressListener.onProgressUpdate(url, 100, DownloadStatusEnum.SUCCESS);
                    removeProgressListener(url, downLoadProgressListener);
                    return false;
                }
            });
            //资源开始下载
            downLoadProgressListener.onProgressUpdate(url, 0, DownloadStatusEnum.START);
        }
    }

    /**
     * 对RequestBuilder进行初始化
     */
    private void initRequestBuilder(RequestBuilder request, Options options) {
        if (options == null) options = Options.defaultOptions();
        RequestOptions requestOptions = new RequestOptions();
        if (options.loadingResId != Options.RES_NONE) {
            requestOptions.placeholder(options.loadingResId);
        }
        if (options.loadErrorResId != Options.RES_NONE) {
            requestOptions.error(options.loadErrorResId);
        }
        if (options.loadNullResId != Options.RES_NONE) {
            requestOptions.fallback(options.loadNullResId);
        }
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        //图片缩放模式
        if (options.scaleType == TYPE.fitCenter) {
            requestOptions.fitCenter();
        } else if (options.scaleType == TYPE.centerCrop) {
            requestOptions.centerCrop();
        } else if (options.scaleType == TYPE.centerInside) {
            requestOptions.centerInside();
        } else if (options.scaleType == TYPE.circleCrop) {
            requestOptions.circleCrop();
        }

        request.apply(requestOptions);
        request.thumbnail(0.1f);
    }

    /**
     * 进度更新回调
     */
    private static final OnProgressListener LISTENER = new OnProgressListener() {
        @Override
        public void onProgressUpdate(String url, int progress, DownloadStatusEnum status) {
            if (listeners == null || listeners.size() == 0) return;
            ArrayList<WeakReference<OnProgressListener>> listener = listeners.get(url);
            if (listener == null || listener.size() == 0) return;
            for (WeakReference<OnProgressListener> tmpListener : listener) {
                tmpListener.get().onProgressUpdate(url, progress, DownloadStatusEnum.LOADING);
            }
        }
    };

    /**
     * 添加监听器到列表
     */
    public static void addProgressListener(String url, OnProgressListener progressListener) {
        if (progressListener == null) return;


        ArrayList<WeakReference<OnProgressListener>> progressListeners;
        if (listeners.get(url) == null) {
            progressListeners = new ArrayList<>();
        } else {
            progressListeners = listeners.get(url);
        }
        if (findProgressListener(progressListeners, progressListener) == null) {
            progressListeners.add(new WeakReference(progressListener));
        }
        listeners.put(url, progressListeners);
    }

    /**
     * 列表删除监听器
     */
    public static void removeProgressListener(String url, OnProgressListener progressListener) {
        if (progressListener == null) return;
        if (listeners.get(url) != null) {
            WeakReference<OnProgressListener> listener = findProgressListener(listeners.get(url), progressListener);
            if (listener != null) {
                listeners.get(url).remove(listener);
            }
        }
    }

    /**
     * 列表查找监听器并返回
     */
    private static WeakReference<OnProgressListener> findProgressListener(ArrayList<WeakReference<OnProgressListener>> progressListeners, OnProgressListener listener) {
        if (listener == null) return null;
        if (progressListeners == null || progressListeners.size() == 0) return null;

        for (int i = 0; i < progressListeners.size(); i++) {
            WeakReference<OnProgressListener> progressListener = progressListeners.get(i);
            if (progressListener.get() == listener) {
                return progressListener;
            }
        }
        return null;
    }

    /**
     * 获取okhttp
     */
    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

}
