package com.lirui.learn.face;

import com.lirui.learn.face.net.API;
import com.lirui.learn.face.net.ApiService;
import com.lirui.lib_common.base.BasePresenter;
import com.lirui.lib_common.base.BaseView;
import com.lirui.lib_common.net.callback.NetSubscriber;
import com.lirui.lib_common.net.netError.ApiException;

import java.util.HashMap;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/09/07
 *      desc    :
 *      version : 1.0
 *  </pre>
 */

public class FaceDetectPresenter extends BasePresenter {
    public FaceDetectPresenter(BaseView view) {
        super(view);
    }

    //上传图片
    public void uploadImage(String path) {
        ApiService.getAppService().uploadImage(path).
                subscribe(new NetSubscriber<FaceBean>(API.updateFile, this));
    }

    public void testweb() {
        ApiService.getAppService().testWeb().
                subscribe(new NetSubscriber<FaceBean>(API.updateFile, this));
    }

    @Override
    public void onStart(String url, HashMap<String, Object> request) {
        view.onStart(url, request);
    }

    @Override
    public void onSuccess(String url, HashMap<String, Object> request, Object t) {
        view.onSuccess(url, request, t);
    }

    @Override
    public void onError(String url, HashMap<String, Object> request, ApiException e) {
        view.onError(url, request, e);
    }
}
