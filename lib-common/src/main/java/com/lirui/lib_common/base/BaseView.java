package com.lirui.lib_common.base;

import com.lirui.lib_common.net.netError.ApiException;

import java.util.HashMap;

/**
 * MVP-V
 */

public interface BaseView {
    //请求开始
    void onStart(String url, HashMap<String, Object> request);

    //请求成功
    void onSuccess(String url, HashMap<String, Object> request, Object t);

    //请求完成
    void onCompleted(String url, HashMap<String, Object> request);

    //请求错误
    void onError(String url, HashMap<String, Object> request, ApiException e);
}
