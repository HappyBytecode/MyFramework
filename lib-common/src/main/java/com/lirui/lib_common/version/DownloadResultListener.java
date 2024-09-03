package com.lirui.lib_common.version;

/**
 * 版本检测，进度更新
 */

public interface DownloadResultListener {
    //不需要进行版本升级
    void noNeedUpdate();

    //暂不进行进行版本升级
    void noUpdate();

    //版本升级成功
    void updateSuccess();

    //版本升级失败
    void updateFail();

    //取消版本下载
    void cancelUpdate();
}
