package com.lirui.lib_common.net.converterFactory;


import com.google.gson.TypeAdapter;
import com.lirui.lib_common.net.bean.BaseBean;
import com.lirui.lib_common.net.netError.ErrorCode;
import com.lirui.lib_common.net.netError.ServerException;
import com.lirui.lib_common.net.netError.TokenInvalidException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 作者：李蕊(1735613836@qq.com)
 * 2016/11/7 0007
 * 功能：通用错误处理
 */

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, Object> {

    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Object convert(ResponseBody value) throws IOException {
        try {
            BaseBean apiModel = (BaseBean) adapter.fromJson(value.charStream());
            if (apiModel.getMsgCode().startsWith("E")) {//错误码
                int errorCode = Integer.parseInt(apiModel.getMsgCode().substring(1, apiModel.getMsgCode().length()));
                if (errorCode == ErrorCode.TOKEN_INVALID) {
                    throw new TokenInvalidException();
                }else {
                    throw new ServerException(errorCode, apiModel.getMsg());
                }
            } else {
                return apiModel.getData();
            }
        }catch (Exception e){
            throw new ServerException(-1,"服务器数据异常");
        }
        finally {
            value.close();
        }
    }
}