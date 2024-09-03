package com.lirui.lib_common.net.bean;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenApi {
    String end_point = "";

    @FormUrlEncoded
    @POST
    Observable<TokenBean> refreshToken(@Field("username") String username, @Field("password") String password);
}
