package com.lirui.lib_common.net.callback;

import com.lirui.lib_common.net.netError.ApiException;

import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 用来集成网络请求，仅支持主线程操作（其他线程，代码中自主操作）
 * 网络请求的前期处理这里进行，App端网络请求的Subscriber采用或继承NetSubscriber
 */

public class NetSubscriber<T> implements Observer<T> {

    private String url;//用来分辨是哪个网络请求的返回
    private NetCancelListener netCancelListener;//网络请求回调
    private HashMap<String, Object> request = new HashMap();//发送的网络请求

    public NetSubscriber(String url, NetCancelListener netCancelListener) {
        super();
        this.url = url;
        this.netCancelListener = netCancelListener;
    }

    public NetSubscriber(String url, HashMap<String, Object> request, NetCancelListener netCancelListener) {
        super();
        this.url = url;
        this.netCancelListener = netCancelListener;
        this.request = request;
    }

    @Override
    public void onSubscribe(Disposable d) {
        netCancelListener.onStart(url, request);
    }

    @Override
    public void onNext(T t) {
        netCancelListener.onSuccess(url, request, t);
    }

    @Override
    public void onError(Throwable e) {
        netCancelListener.onError(url, request, (ApiException) e);
        netCancelListener.onCompleted(url, request);
    }

    @Override
    public void onComplete() {
        netCancelListener.onCompleted(url, request);
    }

}
