package com.lirui.lib_common.net.converterFactory;

import android.text.TextUtils;

import com.lirui.lib_common.base.BaseApplication;
import com.lirui.lib_common.net.HttpHelper;
import com.lirui.lib_common.net.bean.TokenApi;
import com.lirui.lib_common.net.bean.TokenBean;
import com.lirui.lib_common.net.netError.TokenInvalidException;
import com.lirui.lib_common.util.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import retrofit2.http.Query;

/**
 * 代理模式，用于刷新Token
 */

public class ProxyHandler implements InvocationHandler {

    //token字段Key值
    public final static String TOKEN = "token";

    //token更新失败
    private Throwable mRefreshTokenError = null;
    //是否更新API中的token
    private boolean mIsTokenNeedRefresh;

    private Object mProxyObject;
    private BaseApplication application = BaseApplication.getInstance();

    public ProxyHandler(Object proxyObject) {
        mProxyObject = proxyObject;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return Observable.just(1).flatMap(new Function<Object, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Object o) throws Exception {
                try {
                    //需要刷新Token
                    if (mIsTokenNeedRefresh) {
                        updateMethodToken(method, args);
                    }
                    return (Observable<?>) method.invoke(mProxyObject, args);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).retryWhen(new Function<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
                return observable.flatMap(new Function<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> apply(Throwable throwable) throws Exception {
                        if (throwable instanceof TokenInvalidException) {
                            return refreshTokenWhenTokenInvalid();
                        }
                        return Observable.error(throwable);
                    }
                });
            }
        });
    }

    /**
     * 重新获取Token
     */
    private Observable<?> refreshTokenWhenTokenInvalid() {
        synchronized (ProxyHandler.class) {
            // call the refresh token api.
            HttpHelper.getInstance().getProxy(TokenApi.class)
                    .refreshToken(application.getUserName(), application.getPassword())
                    .subscribe(new Observer<TokenBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mRefreshTokenError = null;
                        }

                        @Override
                        public void onNext(TokenBean model) {
                            if (model != null) {
                                mIsTokenNeedRefresh = true;
                                application.setToken(model.getToken());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mRefreshTokenError = e;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            if (mRefreshTokenError != null) {
                return Observable.error(mRefreshTokenError);
            } else {
                return Observable.just(true);
            }
        }
    }

    /**
     * 更新API文档的Token值
     */
    private void updateMethodToken(Method method, Object[] args) {
        if (mIsTokenNeedRefresh && !TextUtils.isEmpty(application.getToken())) {
            Annotation[][] annotationsArray = method.getParameterAnnotations();
            Annotation[] annotations;
            if (annotationsArray != null && annotationsArray.length > 0) {
                for (int i = 0; i < annotationsArray.length; i++) {
                    annotations = annotationsArray[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Query) {
                            if (TOKEN.equals(((Query) annotation).value())) {
                                args[i] = application.getToken();
                            }
                        }
                    }
                }
            }
            mIsTokenNeedRefresh = false;
        }
    }
}