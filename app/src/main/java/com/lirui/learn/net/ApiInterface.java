package com.lirui.learn.net;

import com.lirui.learn.bean.InTheaters;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 作者：李蕊(1735613836@qq.com)
 * 2016/10/18 0018
 * 功能：网络请求接口
 */

public interface ApiInterface {
    String end_point = "https://api.douban.com/";

    /**
     * 正在上映
     */
    @GET(API.in_theaters)
    Observable<InTheaters> inTheaters();
}
