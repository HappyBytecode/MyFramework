package com.lirui.lib_common.image.loader;

import com.lirui.lib_common.constant.DownloadStatusEnum;

/**
 * 更新下载进度
 */

public interface OnProgressListener {
    void onProgressUpdate(String url, int progress, DownloadStatusEnum status);
}
