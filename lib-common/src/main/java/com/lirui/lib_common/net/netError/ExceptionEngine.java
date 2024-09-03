package com.lirui.lib_common.net.netError;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 处理异常的驱动器，所有异常统一转换为ApiException，封装统一处理
 */
public class ExceptionEngine {

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {             //HTTP错误
            ex = new ApiException(e, ErrorCode.HTTP_ERROR);
            ex.setMessage("没有可用的网络，请检查网络设置");//均视为网络错误
            return ex;
        }else if(e instanceof UnknownHostException){
            ex = new ApiException(e, ErrorCode.NO_NET_ERROR);
            ex.setMessage("没有可用的网络，请检查网络设置");
            return ex;
        }
        else if (e instanceof ServerException) {    //服务器返回的错误
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, resultException.code);
            ex.setMessage(resultException.getMessage());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof MalformedJsonException) {
            ex = new ApiException(e, ErrorCode.PARSE_ERROR);
            ex.setMessage("数据异常");//均视为解析错误
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ErrorCode.NETWORD_ERROR);
            ex.setMessage("服务器异常");//均视为网络错误
            return ex;
        }else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ErrorCode.SSL_ERROR);
            ex.setMessage("安全策略异常");//均视为网络错误
            return ex;
        } else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(e, ErrorCode.TIMEOUT_ERROR);
            ex.setMessage("连接超时");//均视为网络错误
            return ex;
        }
        else {
            ex = new ApiException(e, ErrorCode.UNKNOWN);
            ex.setMessage("未知错误");//未知错误
            return ex;
        }
    }
}
