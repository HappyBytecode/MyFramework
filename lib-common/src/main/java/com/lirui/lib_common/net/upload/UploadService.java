package com.lirui.lib_common.net.upload;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lirui.lib_common.R;
import com.lirui.lib_common.net.download.DownloadService;
import com.lirui.lib_common.net.upload.event.UploadFileEvent;
import com.lirui.lib_common.net.upload.event.UploadFinishEvent;
import com.lirui.lib_common.net.upload.event.UploadTaskEvent;
import com.lirui.lib_common.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * 文件上传服务
 */
public class UploadService extends Service {
    public static String UPLOAD_URL = "UPLOAD_URL";
    public static String UPLOAD_FILES = "UPLOAD_FILES";
    public static String UPLOAD_PARAMS = "UPLOAD_PARAMS";
    private static String ACTION = "UPLOAD_ACTION";
    private static String UPLOAD_CANCEL_ID = "UPLOAD_CANCEL_ID";
    private static int ACTION_CANCEL = 1;
    private HashMap<String, UploadTask> mTaskMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    public static void startService(final Activity activity, final String url, final ArrayList<UploadParam> fileList, final ArrayList<UploadParam> paramList) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            Intent intent = new Intent(activity, UploadService.class);
                            intent.putExtra(UPLOAD_URL, url);
                            intent.putExtra(UPLOAD_FILES, fileList);
                            intent.putExtra(UPLOAD_PARAMS, paramList);
                            activity.startService(intent);
                        } else {
                            ToastUtils.warning(activity, activity.getResources().getString(R.string.permissionsError, "读写权限")).show();
                        }
                    }
                });
    }

    public static void cancelUpload(Context context, String url) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(ACTION, ACTION_CANCEL);
        intent.putExtra(UPLOAD_URL, url);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(ACTION, 0);

        if (action == ACTION_CANCEL) {
            String url = intent.getStringExtra(UPLOAD_URL);
            UploadTask task = mTaskMap.get(url);
            if (task != null) {
                task.cancel();
            }
        } else {
            String url = intent.getStringExtra(UPLOAD_URL);
            List<UploadParam> fileList = intent.getParcelableArrayListExtra(UPLOAD_FILES);
            List<UploadParam> paramList = intent.getParcelableArrayListExtra(UPLOAD_PARAMS);

            if (!TextUtils.isEmpty(url) && fileList != null && fileList.size() > 0) {
                long total = 0;

                HashMap<String, File> fileMap = new HashMap<>();
                for (UploadParam param : fileList) {
                    File file = new File(param.value);
                    total += file.length();
                    fileMap.put(param.key, file);
                }

                HashMap<String, String> paramMap = new HashMap<>();
                if (paramList != null) {
                    for (UploadParam param : paramList) {
                        paramMap.put(param.key, param.value);
                    }
                }

                UploadTask task = new UploadTask(mTaskMap.size(), url, fileMap, paramMap);
                task.total = total;
                mTaskMap.put(url, task);
                task.start();
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

    //上传进度更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadFileEvent uploadEvent) {
        uploadEvent.task.progress += uploadEvent.byteCount;
        int percent = (int) (uploadEvent.task.progress / uploadEvent.task.total * 100);
        if (uploadEvent.task.current_percent != percent) {
            uploadEvent.task.current_percent = percent;
            //更新任务进度
            EventBus.getDefault().post(new UploadTaskEvent(uploadEvent.task, percent));
            /*if (percent == 100) {
                EventBus.getDefault().post(new UploadFinishEvent(uploadEvent.task, true));
            }*/
        }
    }

    //上传完成
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UploadFinishEvent event) {
        mTaskMap.remove(event.task.id);
    }

    //清楚所有下载任务
    private void cancelAll() {
        Iterator iterator = mTaskMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            UploadTask task = (UploadTask) entry.getValue();
            task.cancel();
        }
    }
}
