package com.lirui.lib_common.base;

import android.app.Application;

import com.lirui.lib_common.BuildConfig;
import com.lirui.lib_common.util.CleanUtils;
import com.lirui.lib_common.util.CrashUtils;
import com.lirui.lib_common.util.FileUtils;
import com.lirui.lib_common.util.LogUtils;
import com.lirui.lib_common.util.Utils;

import java.io.File;

/**
 * 基类，
 */

public abstract class BaseApplication extends Application {

    private static BaseApplication application;

    //文件根路径
    public String parentFilePath;
    //缓存根路径
    public String parentCacheFilePath;

    //1.下载目录
    public String downloadPath;
    //2.文件目录
    public String filePath;
    //3.拍照图片路径
    public String imagePath;
    //4.Cache路径
    public String cachePath;
    //4.Error路径
    public String crashPath;

    public static BaseApplication getInstance() {
        return application;
    }

    public void onCreate() {
        super.onCreate();
        application = this;
        Utils.init(this);
        initFilePath();
        //初始化App崩溃捕捉，并保存Log到文件
        CrashUtils.init(crashPath);
        //初始化Log打印相关信息
        initLog();
    }

    /**
     * 初始化文件路径
     */
    private void initFilePath() {
        //删除缓存照片，cachePath
        CleanUtils.cleanInternalCache();
        //文件根路径
        parentFilePath = FileUtils.getFilesDir();
        //缓存根路径
        parentCacheFilePath = FileUtils.getCacheDir();

        //1.下载目录
        downloadPath = parentFilePath + "download" + File.separator;
        FileUtils.createOrExistsDir(downloadPath);
        //2.文件目录
        filePath = parentFilePath + "file" + File.separator;
        FileUtils.createOrExistsDir(filePath);
        //3.拍照图片路径
        imagePath = parentFilePath + "image" + File.separator;
        FileUtils.createOrExistsDir(imagePath);
        //4.Cache路径
        cachePath = parentCacheFilePath + "cache" + File.separator;
        FileUtils.createOrExistsDir(cachePath);
        //4.Error路径
        crashPath = parentFilePath + "crash" + File.separator;
        FileUtils.createOrExistsDir(crashPath);

    }

    /**
     * 用户相关
     */
    public abstract String getUserName();

    public abstract String getPassword();

    public abstract String getToken();

    public abstract void setToken(String token);

    /**
     * 文件相关
     */
    public void initLog() {
        LogUtils.Builder builder = new LogUtils.Builder()
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLogHeadSwitch(true)// 设置log头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                // .setDir(parentCacheFilePath + "log" + File.separator)// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(LogUtils.V)// log的控制台过滤器，和logcat过滤器同理，默认Verbose
                .setFileFilter(LogUtils.V);// log文件过滤器，和logcat过滤器同理，默认Verbose
        LogUtils.d(builder.toString());
    }

}
