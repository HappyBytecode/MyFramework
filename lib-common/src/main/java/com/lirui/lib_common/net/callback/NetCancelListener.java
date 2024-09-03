package com.lirui.lib_common.net.callback;

import com.lirui.lib_common.net.netError.ApiException;

import java.util.HashMap;

/**
 * 网络请求回调接口
 */
public interface NetCancelListener {
    //绑定后开始调用
    void onStart(String url, HashMap<String, Object> request);

    //请求成功
    void onSuccess(String url, HashMap<String, Object> request, Object t);

    //请求错误
    void onError(String url, HashMap<String, Object> request, ApiException e);

    //请求完成
    void onCompleted(String url, HashMap<String, Object> request);
}
