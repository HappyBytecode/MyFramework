package com.lirui.lib_common.view.web;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.lirui.lib_common.constant.DownloadStatusEnum;
import com.lirui.lib_common.net.download.DownloadEvent;
import com.lirui.lib_common.net.download.DownloadService;
import com.lirui.lib_common.util.FileUtils;
import com.lirui.lib_common.util.IntentUtils;
import com.lirui.lib_common.util.ToastUtils;
import com.lirui.lib_common.view.web.listener.IWebLoadListener;

import java.util.HashMap;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/25
 *      desc    : 智能WebView
 *                  1.上传
 *                  2.下载
 *                  3.网页加载进度条
 *      version : 1.0
 *  </pre>
 */

public class SmartWebView extends WebView {
    private static final int CANCEL = 101;
    private static final int SUCCESS = 102;
    private static final String SMART_DOWNLOAD = "smart_download";
    private static final String URL = "url";
    private static final String STATUS = "status";
    private static final String FILEPATH = "filePath";
    private final Activity activity;
    private SmartWebChromeClient webChromeClient;
    private SmartWebViewClient webViewClient;
    private SafeManager safeManager;
    private Handler mHandler;

    HashMap<String, Notify> notifyList = new HashMap<>();
    private NotificationBroadcastReceiver notificationBroadcastReceiver;

    public SmartWebView(Activity activity) {
        super(activity);
        this.activity = activity;
        init();
    }

    /**
     * WebView初始化
     */
    private void init() {
        mHandler = new Handler(Looper.getMainLooper());
        safeManager = new SafeManager(this);
        webChromeClient = new SmartWebChromeClient(activity, safeManager);
        webViewClient = new SmartWebViewClient(activity, safeManager);

        new SettingManager(this).settings();
        safeManager.removeUnSafeJavascriptImpl();
        setWebChromeClient(webChromeClient);
        setWebViewClient(webViewClient);
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadService.startService(activity, url);
            }
        });
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object obj, String interfaceName) {
        safeManager.addJavascriptInterface(obj, interfaceName);
    }

    @SuppressLint("NewApi")
    @Override
    public void removeJavascriptInterface(String interfaceName) {
        safeManager.removeJavascriptInterface(interfaceName);
    }

    private void safeLoadUrl(final String url) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadUrl(url);
            }
        });
    }

    private void safeReload() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                reload();
            }
        });
    }

    @Override
    public void loadUrl(String url) {
        if (!WebUtils.isUIThread()) {
            safeLoadUrl(url);
            return;
        }
        super.loadUrl(url);
    }

    @Override
    public void reload() {
        if (!WebUtils.isUIThread()) {
            safeReload();
            return;
        }
        super.reload();
    }

    @Override
    public void loadData(final String data, final String mimeType, final String encoding) {
        if (!WebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadData(data, mimeType, encoding);
                }
            });
            return;
        }
        super.loadData(data, mimeType, encoding);

    }

    @Override
    public void stopLoading() {
        if (!WebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopLoading();
                }
            });
            return;
        }
        super.stopLoading();
    }

    @Override
    public void loadDataWithBaseURL(final String baseUrl, final String data, final String mimeType, final String encoding, final String historyUrl) {
        if (!WebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
                }
            });
            return;
        }
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);

    }

    @Override
    public void postUrl(final String url, final byte[] postData) {
        if (!WebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    postUrl(url, postData);
                }
            });
            return;
        }
        super.postUrl(url, postData);
    }

    public void setOnWebLoadListener(IWebLoadListener onWebLoadListener) {
        if (onWebLoadListener != null) {
            webChromeClient.setOnWebLoadListener(onWebLoadListener);
            webViewClient.setOnWebLoadListener(onWebLoadListener);
        }
    }

    public void notifyWebFileSelect(int requestCode, int resultCode, Intent data) {
        webChromeClient.notifyWebFileSelect(requestCode, resultCode, data);
    }

    public void notifyWebDownloadFileProgress(DownloadEvent event) {
        Notify notify = notifyList.get(event.url);
        if (event.status == DownloadStatusEnum.START) {
            if (notify == null) {
                Intent intentClick = new Intent(SMART_DOWNLOAD);
                intentClick.putExtra(URL, event.url);
                intentClick.putExtra(STATUS, DownloadStatusEnum.START);
                PendingIntent cancelIntent = PendingIntent.getBroadcast(activity, CANCEL, intentClick, PendingIntent.FLAG_ONE_SHOT);
                notify = new Notify(activity, event.url.hashCode());
                notify.notify_progress(null, FileUtils.getUrlFileName(event.url), "", cancelIntent);
                notifyList.put(event.url, notify);
                notify.setContentText("当前进度:" + 0 + "%");
                notify.setProgress(100, 0, false);
                notify.sent();
            }
        } else if (event.status == DownloadStatusEnum.CANCEL) {
            if (notify != null) {
                notify.cancel(event.url.hashCode());
                notifyList.remove(event.url);
            }
        } else if (event.status == DownloadStatusEnum.ERROR) {
            if (notify != null) {
                notify.setContentText("下载失败");
                notifyList.remove(event.url);
                notify.sent();
            }
        } else if (event.status == DownloadStatusEnum.SUCCESS) {
            if (notify != null) {
                Intent intentClick = new Intent(SMART_DOWNLOAD);
                intentClick.putExtra(STATUS, DownloadStatusEnum.SUCCESS);
                intentClick.putExtra(FILEPATH, event.filePath);
                PendingIntent okIntent = PendingIntent.getBroadcast(activity, SUCCESS, intentClick, PendingIntent.FLAG_ONE_SHOT);
                notify.setContentIntent(okIntent);
                notify.setContentText("下载成功");
                notify.setProgress(100, 100, false);
                notifyList.remove(event.url);
                notify.sent();
            }
        } else {
            notify.setContentText("当前进度:" + event.progress + "%");
            notify.setProgress(100, event.progress, false);
            notify.sent();
        }
    }

    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= 11)
            super.onResume();
        registerReceiver();
        super.resumeTimers();
    }

    @Override
    public void onPause() {
        super.pauseTimers();
        unregisterReceiver();
        if (Build.VERSION.SDK_INT >= 11)
            super.onPause();
    }

    public void onDestroy() {
        WebUtils.clearWebView(this);
    }

    /**
     * 注册广播监听Notification操作
     */
    private void registerReceiver() {
        if (notificationBroadcastReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(SMART_DOWNLOAD);
            notificationBroadcastReceiver = new NotificationBroadcastReceiver();
            activity.registerReceiver(notificationBroadcastReceiver, filter);
        }
    }

    /**
     * 注册广播取消监听Notification操作
     */
    private void unregisterReceiver() {
        if (notificationBroadcastReceiver != null) {
            activity.unregisterReceiver(notificationBroadcastReceiver);
            notificationBroadcastReceiver = null;
        }
    }

    /**
     * 接受通知,取消下载
     */
    public class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SMART_DOWNLOAD)) {
                DownloadStatusEnum status = intent.getParcelableExtra(STATUS);
                //处理点击事件
                if (status == DownloadStatusEnum.SUCCESS) {
                    String filePath = intent.getStringExtra(FILEPATH);
                    IntentUtils.getOpenFileIntent(filePath);
                } else {
                    String url = intent.getStringExtra(URL);
                    DownloadService.cancelDownload(activity, url);
                    ToastUtils.info(activity, "文件" + FileUtils.getUrlFileName(url) + "下载取消").show();
                }
            }
        }
    }
}
