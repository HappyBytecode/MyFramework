package com.lirui.lib_common.net.upload;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Url;


/**
 * 上传API
 */
public interface UploadApi {
    String end_point = "https://www.baidu.com/";

    @Multipart
    @POST
    Observable<ResponseBody> uploadFile(@Url String url, @PartMap Map<String, RequestBody> params);
}
