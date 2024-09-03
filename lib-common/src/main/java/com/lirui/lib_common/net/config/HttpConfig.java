package com.lirui.lib_common.net.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Converter;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Http配置文件
 */

public class HttpConfig {
    private Map<String, String> queryParamsMap = new HashMap<>(); //公共参数，作用于url
    private Map<String, String> paramsMap = new HashMap<>(); //公共参数，作用于post实体中
    private Map<String, String> headerParamsMap = new HashMap<>(); //全局公共请求头，key value形式
    private List<String> headerLinesList = new ArrayList<>(); //全局公共请求头 key:value形式
    private int connectTimeOut;           //连接超时时间
    private int writeReadTimeOut;         //读写超时时间
    private boolean retryOnConnectionFailure;               //是否错误重连
    private SSLUtils.SSLParams sSLParams;   //证书
    private boolean isDownloadListener = false;
    private String url;
    private Converter.Factory gsonConverterFactory = GsonConverterFactory.create();

    /*private CacheMode mCacheMode;          //全局缓存模式
    private long mCacheTime;               //全局缓存过期时间,默认永不过期
    private long mCacheLength;             //全局缓存大小*/

    private HttpConfig(Builder builder) {
        this.queryParamsMap = builder.queryParamsMap; //公共参数，作用于url
        this.paramsMap = builder.paramsMap; //公共参数，作用于post实体中
        this.headerParamsMap = builder.headerParamsMap; //全局公共请求头，key value形式
        this.headerLinesList = builder.headerLinesList; //全局公共请求头 key:value形式
        this.connectTimeOut = builder.connectTimeOut; //连接超时时间
        this.writeReadTimeOut = builder.writeReadTimeOut;//读写超时时间
        this.retryOnConnectionFailure = builder.retryOnConnectionFailure;//是否错误重连
        this.sSLParams = builder.sSLParams; //证书
        this.isDownloadListener = builder.isDownloadListener;
        this.url = builder.url;
        this.gsonConverterFactory=builder.gsonConverterFactory;
    }

    public Map<String, String> getQueryParamsMap() {
        return queryParamsMap;
    }

    public Map<String, String> getParamsMap() {
        return paramsMap;
    }

    public Map<String, String> getHeaderParamsMap() {
        return headerParamsMap;
    }

    public List<String> getHeaderLinesList() {
        return headerLinesList;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public int getWriteReadTimeOut() {
        return writeReadTimeOut;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public SSLUtils.SSLParams getSSLParams() {
        return sSLParams;
    }

    public boolean isDownloadListener() {
        return isDownloadListener;
    }

    public String getUrl() {
        return url;
    }

    public Converter.Factory getGsonConverterFactory() {
        return gsonConverterFactory;
    }

    public static class Builder {

        private Map<String, String> queryParamsMap = new HashMap<>(); //公共参数，作用于url
        private Map<String, String> paramsMap = new HashMap<>(); //公共参数，作用于post实体中
        private Map<String, String> headerParamsMap = new HashMap<>(); //全局公共请求头，key value形式
        private List<String> headerLinesList = new ArrayList<>(); //全局公共请求头 key:value形式
        private int connectTimeOut = 30;           //连接超时时间
        private int writeReadTimeOut = 30;         //读写超时时间
        private boolean retryOnConnectionFailure = false;               //是否错误重连
        private SSLUtils.SSLParams sSLParams = SSLUtils.getSslSocketFactory();   //证书
        private boolean isDownloadListener = false;
        private Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
        private String url;

        public Builder() {
        }

        public Builder addQueryParamsMap(Map<String, String> queryParamsMap) {
            this.queryParamsMap = queryParamsMap;
            return this;
        }

        public Builder addParamsMap(Map<String, String> paramsMap) {
            this.paramsMap = paramsMap;
            return this;
        }

        public Builder addHeaderParamsMap(Map<String, String> headerParamsMap) {
            this.headerParamsMap = headerParamsMap;
            return this;
        }

        public Builder addHeaderLinesList(List<String> headerLinesList) {
            this.headerLinesList = headerLinesList;
            return this;
        }

        public Builder connectTimeOut(int connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
            return this;
        }

        public Builder writeReadTimeOut(int writeReadTimeOut) {
            this.writeReadTimeOut = writeReadTimeOut;
            return this;
        }

        public Builder isRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
            this.retryOnConnectionFailure = retryOnConnectionFailure;
            return this;
        }

        public Builder sSLParams(SSLUtils.SSLParams sSLParams) {
            this.sSLParams = sSLParams;
            return this;
        }

        public Builder isDownloadListener(boolean isDownloadListener) {
            this.isDownloadListener = isDownloadListener;
            return this;
        }

        public Builder gsonConverterFactory(Converter.Factory gsonConverterFactory) {
            this.gsonConverterFactory = gsonConverterFactory;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public HttpConfig builder() {
            return new HttpConfig(this);
        }

    }

}
