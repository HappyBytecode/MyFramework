package com.lirui.lib_common.view.web;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;

import com.lirui.lib_common.R;

public class Notify {
    private static final int FLAG = Notification.FLAG_INSISTENT;
    int requestCode = (int) SystemClock.uptimeMillis();
    private int NOTIFICATION_ID;
    private NotificationManager nm;
    private Notification notification;
    private NotificationCompat.Builder cBuilder;
    private Notification.Builder nBuilder;
    private Context mContext;

    public Notify(Context context, int ID) {
        this.NOTIFICATION_ID = ID;
        mContext = context;
        // 获取系统服务来初始化对象
        nm = (NotificationManager) mContext
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        cBuilder = new NotificationCompat.Builder(mContext);
    }

    /**
     * 设置在顶部通知栏中的各种信息
     */
    public void notify_progress(PendingIntent pendingIntent, String title, String content, PendingIntent pendingIntentCancel) {
        cBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        cBuilder.setSmallIcon(R.mipmap.ic_launcher);// 设置顶部状态栏的小图标

        cBuilder.setContentTitle(title);// 设置通知中心的标题
        cBuilder.setContentText(content);// 设置通知中心中的内容
        cBuilder.setWhen(System.currentTimeMillis());
        /*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
		 * 不设置的话点击消息后也不清除，但可以滑动删除
		 */
        cBuilder.setAutoCancel(true);
        cBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        cBuilder.setDeleteIntent(pendingIntentCancel);
    }

    public void setProgress(int maxProgress, int currentProgress, boolean exc) {
        cBuilder.setProgress(maxProgress, currentProgress, exc);
        sent();
    }

    public void setContentText(String text) {
        cBuilder.setContentText(text);
    }

    public void setContentIntent(PendingIntent pendingIntent) {
        cBuilder.setContentIntent(pendingIntent);
    }

    /**
     * 发送通知
     */
    void sent() {
        notification = cBuilder.build();
        nm.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 根据id清除通知
     */
    public void clear() {
        // 取消通知
        nm.cancelAll();
    }

    public void cancel(int id) {
        nm.cancel(id);
    }
}
