package com.lirui.lib_common.net.download;

/**
 * <pre>
 *      author  :lirui
 *      QQ      :1735613836
 *      time    :2017/08/21
 *      desc    : 下载进度
 *      version :1.0
 *  </pre>
 */

public interface DownloadListener {
    void onDownloadStart();

    void onDownloadLoading();

    void onDownloadError();

    void onDownloadSuccess();

    void onDownloadCancel();
}
