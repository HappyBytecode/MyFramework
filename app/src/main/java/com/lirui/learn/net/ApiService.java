package com.lirui.learn.net;

import com.lirui.learn.bean.InTheaters;
import com.lirui.lib_common.net.HttpHelper;
import com.lirui.lib_common.net.converterFactory.Transformer;

import io.reactivex.Observable;

/**
 * 作者：李蕊(1735613836@qq.com)
 * 2016/10/18 0018
 * 功能：网络请求
 */

public class ApiService {
    /**
     * 正在上映
     */
    public static Observable<InTheaters> inTheaters() {
        return HttpHelper.getInstance().getProxy(ApiInterface.class).inTheaters().compose(Transformer.<InTheaters>switchSchedulers());
    }
}
