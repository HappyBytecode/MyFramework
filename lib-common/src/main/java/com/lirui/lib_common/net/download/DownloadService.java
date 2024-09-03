package com.lirui.lib_common.net.download;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.lirui.lib_common.R;
import com.lirui.lib_common.constant.DownloadStatusEnum;
import com.lirui.lib_common.util.LogUtils;
import com.lirui.lib_common.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * 文件下载服务
 */
public class DownloadService extends Service {
    public static String DOWNLOAD_URL = "DOWNLOAD_URL";
    private static String DOWNLOAD_ACTION = "DOWNLOAD_ACTION";
    public final static int ACTION_CANCEL = 1;
    private HashMap<String, DownloadTask> mTaskMap = new HashMap<>();

    public static void startService(final Activity activity, final String url) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            Intent intent = new Intent(activity, DownloadService.class);
                            intent.putExtra(DOWNLOAD_URL, url);
                            activity.startService(intent);
                        } else {
                            ToastUtils.warning(activity,activity.getResources().getString(R.string.permissionsError,"读写权限")).show();
                        }
                    }
                });
    }

    public static void cancelDownload(Context context, String url) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DOWNLOAD_URL, url);
        intent.putExtra(DOWNLOAD_ACTION, ACTION_CANCEL);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(DOWNLOAD_ACTION, 0);

        if (action == ACTION_CANCEL) {
            String url = intent.getStringExtra(DOWNLOAD_URL);
            DownloadTask task = mTaskMap.get(url);
            if (task != null) {
                task.cancel();
                mTaskMap.remove(url);
            }
        } else {
            String url = intent.getStringExtra(DOWNLOAD_URL);
            if (!TextUtils.isEmpty(url)) {
                if (mTaskMap.containsKey(url)) {
                    Toast.makeText(getApplicationContext(), "正在下载中...", Toast.LENGTH_SHORT).show();
                } else {
                    LogUtils.v("DownloadService", url);
                    EventBus.getDefault().post(new DownloadEvent(url, 0, DownloadStatusEnum.START, ""));
                    DownloadTask task = new DownloadTask(url);
                    mTaskMap.put(url, task);
                    task.start();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        cancelAll();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void cancelAll() {
        Iterator iterator = mTaskMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            DownloadTask task = (DownloadTask) entry.getValue();
            task.cancel();
        }
    }

    //上传完成
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadEvent event) {
        if (event.status == DownloadStatusEnum.ERROR || event.status == DownloadStatusEnum.SUCCESS) {
            mTaskMap.remove(event.url);
        }
    }
}
