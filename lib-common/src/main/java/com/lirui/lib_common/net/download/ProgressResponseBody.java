package com.lirui.lib_common.net.download;

import android.support.annotation.Nullable;

import com.lirui.lib_common.constant.DownloadStatusEnum;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 下载进度更新
 */

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final String url;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(String url, ResponseBody responseBody) {
        this.responseBody = responseBody;
        this.url = url;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (null == bufferedSource) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;
            int lastProgress = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                int progress = (int) (totalBytesRead * 100 / responseBody.contentLength());
                if (progress != lastProgress) {//jindu
                    EventBus.getDefault().post(new DownloadEvent(url, progress, DownloadStatusEnum.LOADING, ""));
                    lastProgress = progress;
                }
                return bytesRead;
            }
        };
    }
}
