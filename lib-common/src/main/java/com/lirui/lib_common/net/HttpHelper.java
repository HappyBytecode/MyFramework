package com.lirui.lib_common.net;

import com.lirui.lib_common.BuildConfig;
import com.lirui.lib_common.net.config.HttpConfig;
import com.lirui.lib_common.net.converterFactory.HttpLoggingInterceptor;
import com.lirui.lib_common.net.converterFactory.ProxyHandler;
import com.lirui.lib_common.net.download.DownloadInterceptor;
import com.lirui.lib_common.net.interceptor.HeaderParameterInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Http 请求处理类
 * 1.添加代理，所有的网络请求均被代理，用来处理Token失效问题
 * 2.添加拦截器，用来拦截发出的网络请求，添加统一的头部及参数
 * 3.添加转换工厂，重写GsonConverterFactory,GsonResponseBodyConverter用来处理参数的统一，并进行异常封装
 * 4.使用Transformer来处理Rxjava的线程问题，并进行所有异常封装
 */

public class HttpHelper {
    //ApiService 列表
    private HashMap<String, Object> mServiceMap;
    //默认配置文件
    private HttpConfig mDefaultConfig;

    private static HttpHelper mInstance;

    private HttpHelper() {
        mServiceMap = new HashMap<>();
        mDefaultConfig = new HttpConfig.Builder().builder();
    }

    public static HttpHelper getInstance() {
        if (mInstance == null) {
            synchronized (HttpHelper.class) {
                if (mInstance == null) {
                    mInstance = new HttpHelper();
                }
            }
        }
        return mInstance;
    }

    public <T> T getApi(Class<T> serviceClass) {
        if (mServiceMap.containsKey(serviceClass.getName())) {
            return (T) mServiceMap.get(serviceClass.getName());
        } else {
            Object obj = createApi(serviceClass);
            mServiceMap.put(serviceClass.getName(), obj);
            return (T) obj;
        }
    }

    public <T> T getApi(Class<T> serviceClass, HttpConfig config) {
        if (mServiceMap.containsKey(serviceClass.getName())) {
            return (T) mServiceMap.get(serviceClass.getName());
        } else {
            Object obj = createApi(serviceClass, config);
            mServiceMap.put(serviceClass.getName(), obj);
            return (T) obj;
        }
    }

    private <T> T createApi(Class<T> serviceClass) {
        return createApi(serviceClass, mDefaultConfig);
    }

    private <T> T createApi(Class<T> serviceClass, HttpConfig builder) {
        String end_point = "";
        try {
            Field field = serviceClass.getField("end_point");
            end_point = (String) field.get(serviceClass);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.getMessage();
            e.printStackTrace();
        }

        OkHttpClient client = buildOkHttp(builder);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(end_point)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(builder.getGsonConverterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(serviceClass);
    }

    private OkHttpClient buildOkHttp(HttpConfig config) {
        HeaderParameterInterceptor basicParamsInterceptor =
                new HeaderParameterInterceptor.Builder()
                        .addHeaderParamsMap(config.getHeaderParamsMap())
                        .addHeaderLinesList(config.getHeaderLinesList())
                        .addParamsMap(config.getParamsMap())
                        .addQueryParamsMap(config.getQueryParamsMap())
                        .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor).build();
        }
        //设置超时
        builder.connectTimeout(config.getConnectTimeOut(), TimeUnit.SECONDS);
        builder.readTimeout(config.getWriteReadTimeOut(), TimeUnit.SECONDS);
        builder.writeTimeout(config.getWriteReadTimeOut(), TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(config.isRetryOnConnectionFailure());
        //证书
        builder.sslSocketFactory(config.getSSLParams().sSLSocketFactory, config.getSSLParams().trustManager);
        builder.hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        //添加拦截器，用于处理统一Head及参数
        builder.addInterceptor(basicParamsInterceptor);
        //添加进度拦截器
        if (config.isDownloadListener()) {
            builder.addInterceptor(new DownloadInterceptor(config.getUrl()));
        }
        /*//cache
        File httpCacheDirectory = new File(mContext.getCacheDir(), "OkHttpCache");
        httpClient.cache(new Cache(httpCacheDirectory, 50 * 1024 * 1024));
        //Interceptor
        httpClient.addNetworkInterceptor(new LogInterceptor());
        httpClient.addInterceptor(new CacheControlInterceptor());*/
        return builder.build();
    }

    /**
     * 通过代理来调用接口，代理处理Token失效问题
     */
    public <T> T getProxy(Class<T> tClass) {
        T t = getApi(tClass);
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, new ProxyHandler(t));
    }

    /**
     * 通过代理来调用接口，代理处理Token失效问题
     */
    public <T> T getProxy(Class<T> tClass, HttpConfig config) {
        T t = getApi(tClass, config);
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[]{tClass}, new ProxyHandler(t));
    }
}
