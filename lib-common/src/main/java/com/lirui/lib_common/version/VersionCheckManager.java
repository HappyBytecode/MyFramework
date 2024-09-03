package com.lirui.lib_common.version;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.lirui.lib_common.R;
import com.lirui.lib_common.exception.PermissionsException;
import com.lirui.lib_common.constant.DownloadStatusEnum;
import com.lirui.lib_common.db.VersionDAO;
import com.lirui.lib_common.net.download.DownloadEvent;
import com.lirui.lib_common.net.download.DownloadService;
import com.lirui.lib_common.util.AppUtils;
import com.lirui.lib_common.util.FileUtils;
import com.lirui.lib_common.util.LogUtils;
import com.lirui.lib_common.version.bean.VersionBean;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.functions.Consumer;

/**
 * 版本检测
 * lirui 2017.08.03
 * 1.判定是否需要升级
 * 2.判定需要升级的版本是否已下载
 * 3.下载新版本，显示Notification
 */

public class VersionCheckManager {

    private static final String ACTION_DOWNLOAD = "download";

    private final Activity activity;
    private String downUrl;//版本下载路径
    private DownloadResultListener downloadListener;//下载更新结果
    private int versionCode;//下载版本
    private boolean isMustUpdate;//是否必须更新
    private String updateMsg;//更新内容
    private DownloadStatusEnum status;

    private RemoteViews views;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager notificationManager;
    private NotificationBroadcastReceiver notificationBroadcastReceiver;

    /**
     * 版本升级
     *
     * @param versionCode  升级的版本号
     * @param isMustUpdate 是否必须更新
     * @param updateMsg    更新内容
     * @param downUrl      下载路径
     */
    public void versionUpdate(int versionCode, boolean isMustUpdate,
                              String updateMsg, String downUrl) {

        this.downUrl = downUrl;
        this.versionCode = versionCode;
        this.isMustUpdate = isMustUpdate;
        this.updateMsg = updateMsg;
        //检测版本是否需要更新
        if (checkUpdate()) {//是
            //检测本地是否已下载最新版本
            VersionBean versionBean = VersionDAO.getInstance().query(versionCode);
            if (versionBean != null && FileUtils.isFileExists(versionBean.getFilePath())) {//已经下载，并且路径有效
                //弹出更新的Dialog
                showUploadDialog(versionBean);
            } else {
                //弹出下载的Dialog
                showDownloadDialog();
            }
        } else {
            //否
            //不需要更新
            if (downloadListener != null) {
                downloadListener.noNeedUpdate();
            }
        }

    }

    public VersionCheckManager(Activity activity) {
        this.activity = activity;
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (!granted) {
                            throw new PermissionsException("WRITE_EXTERNAL_STORAGE", "您没有授权读写权限,请在设置中打开授权");
                        }
                    }
                });
    }

    /**
     * 检测版本是否需要更新
     */

    private boolean checkUpdate() {
        return versionCode > AppUtils.getAppVersionCode();
    }

    /**
     * 显示下载界面
     */
    private void showDownloadDialog() {
        //检测是否为必须下载
        //弹出选择下载的Dialog
        if (isMustUpdate) {//是
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog);
            builder.setTitle("发现新版本");
            builder.setMessage(updateMsg);
            builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //暂不更新
                    if (downloadListener != null) {
                        downloadListener.noUpdate();
                    }
                }
            });
            builder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //下载
                    DownloadService.startService(activity, downUrl);
                }
            });
            builder.setCancelable(false);
            builder.show();
        } else {//否
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog);
            builder.setTitle("发现新版本");
            builder.setMessage(updateMsg);
            builder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //下载
                    DownloadService.startService(activity, downUrl);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    /**
     * 显示更新Dialog
     */
    private void showUploadDialog(final VersionBean versionBean) {
        //检测是否为必须更新
        //弹出选择更新的Dialog
        if (versionBean.isMustUpdate()) {//是
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog);
            builder.setTitle("发现新版本");
            builder.setMessage(versionBean.getUpdateMsg());
            builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (downloadListener != null) {
                        downloadListener.noUpdate();
                    }
                }
            });
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //更新
                    AppUtils.installApp(versionBean.getFilePath(), AppUtils.getAppPackageName() + ".fileprovider");
                }
            });
            builder.setCancelable(false);
            builder.show();
        } else {//否
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog);
            builder.setTitle("发现新版本");
            builder.setMessage(versionBean.getUpdateMsg());
            builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //更新
                    AppUtils.installApp(versionBean.getFilePath(), AppUtils.getAppPackageName() + ".fileprovider");
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    /**
     * 显示通知
     */
    private void showNotification(int progress, boolean isSuccess) {
        //创建通知详细信息
        if (mNotifyBuilder == null) {
            Intent intentClick = new Intent(ACTION_DOWNLOAD);
            //FLAG_ONE_SHOT PendingIntent 只执行一次
            PendingIntent clickIntent = PendingIntent.getBroadcast(activity, 1, intentClick, PendingIntent.FLAG_ONE_SHOT);//FLAG_CANCEL_CURRENT
            views = new RemoteViews(AppUtils.getAppPackageName(), R.layout.update_notify);
            mNotifyBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(activity)
                    .setSmallIcon(R.mipmap.ic_launcher)  //系统状态栏显示的小图标
                    .setContentTitle("正在下载...")            //通知栏标题
                    .setAutoCancel(false)       //不可点击通知栏的删除按钮删除
                    .setWhen(System.currentTimeMillis()) //通知的时间
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setContent(views);
            views.setOnClickPendingIntent(R.id.iv_cancel, clickIntent);
        }

        if (!isSuccess) {
            views.setTextViewText(R.id.tv_progress, "下载失败");
        } else {
            if (progress == 100) {
                views.setImageViewResource(R.id.iv_cancel, R.mipmap.ic_install_transparent_24dp);
                views.setTextViewText(R.id.tv_progress, "等待安装...");
                views.setProgressBar(R.id.pb_progress, 100, progress, false);
            } else {
                views.setTextViewText(R.id.tv_progress, "已下载" + progress + "%");
                views.setProgressBar(R.id.pb_progress, 100, progress, false);
            }
        }
        //显示通知
        notificationManager.notify(0, mNotifyBuilder.build());
    }

    /**
     * 下载进度更新
     */
    public void updateDownloadProgress(DownloadEvent event) {
        if (event.url.equals(downUrl)) {
            LogUtils.v("Version", event.progress);
            if (event.status == DownloadStatusEnum.START) {//开始下载
                status = DownloadStatusEnum.START;
                //创建广播
                registerReceiver();
                //创建通知
                notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                showNotification(event.progress, true);
                return;
            }
            if (event.status == DownloadStatusEnum.LOADING) {//下载中
                status = DownloadStatusEnum.LOADING;
                showNotification(event.progress, true);
                return;
            }
            if (event.status == DownloadStatusEnum.SUCCESS) {//下载成功
                status = DownloadStatusEnum.SUCCESS;
                showNotification(event.progress, true);
                //更新数据库信息
                VersionDAO.getInstance().insert(new VersionBean(versionCode, updateMsg, isMustUpdate,
                        event.filePath, new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date())));
                //安装
                AppUtils.installApp(event.filePath, AppUtils.getAppPackageName() + ".fileprovider");
                if (downloadListener != null) {
                    downloadListener.updateSuccess();
                }
                return;
            }
            if (event.status == DownloadStatusEnum.ERROR) {//下载失败
                status = DownloadStatusEnum.ERROR;
                showNotification(event.progress, false);
                if (downloadListener != null) {
                    downloadListener.updateFail();
                }
                return;
            }
            if (event.status == DownloadStatusEnum.CANCEL) {//取消下载
                status = DownloadStatusEnum.CANCEL;
                if (downloadListener != null) {
                    downloadListener.cancelUpdate();
                }
                return;
            }
        }
    }

    /**
     * 设置下载监听
     */
    public void setDownloadListener(DownloadResultListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    /**
     * 注册广播监听Notification操作
     */
    private void registerReceiver() {
        if (notificationBroadcastReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_DOWNLOAD);
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
            if (intent.getAction().equals(ACTION_DOWNLOAD)) {
                LogUtils.v("NotificationBroadcastReceiver", "NotificationBroadcastReceiver");
                //处理点击事件
                if (status == DownloadStatusEnum.SUCCESS) {
                    VersionBean versionBean = VersionDAO.getInstance().query(versionCode);
                    AppUtils.installApp(versionBean.getFilePath(), AppUtils.getAppPackageName() + ".fileprovider");
                } else {
                    DownloadService.cancelDownload(activity, downUrl);
                }
                unregisterReceiver();
                notificationManager.cancelAll();
            }
        }
    }
}
