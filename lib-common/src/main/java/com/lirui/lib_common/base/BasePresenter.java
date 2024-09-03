package com.lirui.lib_common.base;

import com.lirui.lib_common.net.callback.NetCancelListener;
import com.lirui.lib_common.net.netError.ApiException;

import java.util.HashMap;

/**
 * MVP-P
 */
public class BasePresenter implements NetCancelListener {
    public BaseView view;

    public BasePresenter(BaseView view) {
        this.view = view;
    }

    //绑定后开始调用
    @Override
    public void onStart(String url, HashMap<String, Object> request) {
        view.onStart(url, request);
    }

    //请求成功
    @Override
    public void onSuccess(String url, HashMap<String, Object> request, Object t) {
        view.onSuccess(url, request, t);
    }

    //请求错误
    @Override
    public void onError(String url, HashMap<String, Object> request, ApiException e) {
        view.onError(url, request, e);
    }

    //请求完成
    @Override
    public void onCompleted(String url, HashMap<String, Object> request) {
        view.onCompleted(url, request);
    }
}
