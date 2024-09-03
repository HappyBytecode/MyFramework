package com.lirui.lib_common.view.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;

import com.lirui.lib_common.R;
import com.lirui.lib_common.constant.IntentResultConstants;
import com.lirui.lib_common.constant.Permissions;
import com.lirui.lib_common.util.ToastUtils;
import com.lirui.lib_common.view.web.listener.IWebLoadListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/25
 *      desc    :
 *      version : 1.0
 *  </pre>
 */

public class SmartWebChromeClient extends WebChromeClient {

    private SafeManager safeManager;
    private Activity activity;
    private AlertDialog promptDialog;
    private JsPromptResult pJsResult;
    private AlertDialog confirmDialog;
    private JsResult cJsResult;

    private AlertDialog mAlertDialog;

    private IWebLoadListener onWebLoadListener;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private ValueCallback uploadMessage;

    public SmartWebChromeClient(Activity activity, SafeManager safeManager) {
        this.activity = activity;
        this.safeManager = safeManager;
    }

    @Override
    public final void onProgressChanged(WebView view, int newProgress) {
        safeManager.injectJavascriptInterfaces();
        if (onWebLoadListener != null) {
            onWebLoadListener.loadProgress(newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public final void onReceivedTitle(WebView view, String title) {
        if (onWebLoadListener != null) {
            onWebLoadListener.loadTile(title);
        }
        safeManager.injectJavascriptInterfaces();
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {

        if (onWebLoadListener != null) {
            onWebLoadListener.loadIcom(icon);
        }
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        //Video
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        //Video
        super.onHideCustomView();
    }

    //弹出警告框
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        ToastUtils.warning(activity, message).show();
        return true;
    }

    //弹出确认框
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        showJsConfirm(message, result);
        return true;
    }

    //弹出输入框
    @Override
    public final boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (safeManager.handleJsInterface(view, url, message, defaultValue, result)) {
            return true;
        }
        showJsPrompt(message, result, defaultValue);
        return true;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        new RxPermissions(activity).request(Permissions.LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            callback.invoke(origin, true, false);
                        } else {
                            callback.invoke(origin, false, false);
                            ToastUtils.warning(activity, activity.getString(R.string.permissionsError, "位置权限")).show();
                        }
                    }
                });
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        return super.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        super.getVisitedHistory(callback);
    }

    private void showJsConfirm(String message, final JsResult result) {

        if (confirmDialog == null)
            confirmDialog = new AlertDialog.Builder(activity)//
                    .setMessage(message)//
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmDialog.cancel();
                            cJsResult.cancel();
                        }
                    })//
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            confirmDialog.cancel();
                            cJsResult.confirm();

                        }
                    }).create();
        this.cJsResult = result;
        confirmDialog.show();

    }

    private void showJsPrompt(String message, final JsPromptResult js, String defaultstr) {
        if (promptDialog == null) {
            final EditText et = new EditText(activity);
            et.setText(defaultstr);
            promptDialog = new AlertDialog.Builder(activity)//
                    .setView(et)//
                    .setTitle(message)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            promptDialog.cancel();
                            pJsResult.cancel();
                        }
                    })//
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            promptDialog.cancel();
                            pJsResult.confirm(et.getText().toString());
                        }
                    }).create();
        }
        this.pJsResult = js;
        promptDialog.show();
    }

    public void setOnWebLoadListener(IWebLoadListener onWebLoadListener) {
        this.onWebLoadListener = onWebLoadListener;
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        uploadMessage = valueCallback;
        openImageChooserActivity();
    }

    // For Android  >= 3.0
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        uploadMessage = valueCallback;
        openImageChooserActivity();
    }

    //For Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        uploadMessage = valueCallback;
        openImageChooserActivity();
    }

    // For Android >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        uploadMessageAboveL = filePathCallback;
        openImageChooserActivity();
        return true;
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(i, "Image Chooser"), IntentResultConstants.REQUEST_FILE_CHOOSER);
    }

    public void notifyWebFileSelect(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentResultConstants.REQUEST_FILE_CHOOSER) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != IntentResultConstants.REQUEST_FILE_CHOOSER || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }
}
