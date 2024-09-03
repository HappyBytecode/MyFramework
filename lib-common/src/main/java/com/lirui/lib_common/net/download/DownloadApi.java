package com.lirui.lib_common.net.download;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载API
 */
public interface DownloadApi {
    String end_point = "https://www.baidu.com/";

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);
}
