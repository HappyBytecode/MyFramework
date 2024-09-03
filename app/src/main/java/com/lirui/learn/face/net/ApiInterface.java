package com.lirui.learn.face.net;

import com.lirui.learn.bean.InTheaters;
import com.lirui.learn.face.FaceBean;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 作者：李蕊(1735613836@qq.com)
 * 2016/10/18 0018
 * 功能：网络请求接口
 */

public interface ApiInterface {
    String end_point = "http://192.168.1.100:8080/";

    /**
     * 上传图片
     */
    @Multipart
    @POST(API.updateFile)
    Observable<FaceBean> updateFiles(@Part List<MultipartBody.Part> parts);

    /**
     * 上传图片
     */
    @POST(API.testweb)
    Observable<FaceBean> testweb(@Body HashMap<String, Object> body);
}
