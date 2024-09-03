package com.lirui.lib_common.net.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 下载进度监听
 */

public class DownloadInterceptor implements Interceptor {
    private final String url;

    public DownloadInterceptor(String url) {
        this.url = url;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Response orginalResponse = chain.proceed(chain.request());

        return orginalResponse.newBuilder()
                .body(new ProgressResponseBody(url,orginalResponse.body()))
                .build();
    }
}