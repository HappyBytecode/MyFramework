package com.lirui.lib_common.view.web;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lirui.lib_common.view.web.listener.IWebLoadListener;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/25
 *      desc    :
 *      version : 1.0
 *  </pre>
 */

public class SmartWebViewClient extends WebViewClient {

    public static final String INTENT_SCHEME = "intent://";
    public static final String WEBCHAT_PAY_SCHEME = "weixin://wap/pay?";

    private SafeManager safeManager;
    private Context context;
    private IWebLoadListener onWebLoadListener;

    public SmartWebViewClient(Context context, SafeManager safeManager) {
        this.safeManager = safeManager;
        this.context = context;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        safeManager.injectJavascriptInterfaces();
        super.onLoadResource(view, url);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        safeManager.injectJavascriptInterfaces();
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        safeManager.injectJavascriptInterfaces();
        if(onWebLoadListener!=null){
            onWebLoadListener.loadStart();
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        safeManager.injectJavascriptInterfaces();
        if(onWebLoadListener!=null){
            onWebLoadListener.loadFinish();
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if(onWebLoadListener!=null){
            onWebLoadListener.loadError();
        }
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        if(onWebLoadListener!=null){
            onWebLoadListener.loadError();
        }
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if(onWebLoadListener!=null){
            onWebLoadListener.loadError();
        }
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (handleNormalLinked(request.getUrl() + "")) {
            return true;
        }

        if (request.getUrl().toString().startsWith(INTENT_SCHEME)) { //
            handleIntentUrl(request.getUrl() + "");
            return true;
        }

        if (request.getUrl().toString().startsWith(WEBCHAT_PAY_SCHEME)) {
            startActivity(request.getUrl().toString());
            return true;
        }

        return super.shouldOverrideUrlLoading(view, request);
    }

    private boolean handleNormalLinked(String url) {
        if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return true;
        }
        return false;
    }

    private void handleIntentUrl(String intentUrl) {
        try {
            Intent intent = null;
            if (TextUtils.isEmpty(intentUrl) || !intentUrl.startsWith(INTENT_SCHEME))
                return;

            PackageManager packageManager = context.getPackageManager();
            intent = new Intent().parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (info != null) {  //跳到该应用
                context.startActivity(intent);
                return;
            }
            /*intent=new Intent().setData(Uri.parse("market://details?id=" + intent.getPackage()));
            info=packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i(TAG,"resolveInfo:"+info);
            if (info != null) {  //跳到应用市场
                mActivity.startActivity(intent);
                return;
            }

            intent=new Intent().setData(Uri.parse("https://play.google.com/store/apps/details?id=" + intent.getPackage()));
            info=packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i(TAG,"resolveInfo:"+info);
            if (info != null) {  //跳到浏览器
                mActivity.startActivity(intent);
                return;
            }*/
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void startActivity(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public void setOnWebLoadListener(IWebLoadListener onWebLoadListener) {
        this.onWebLoadListener=onWebLoadListener;
    }
}
