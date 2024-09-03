package com.lirui.lib_common.net.download;

import com.lirui.lib_common.base.BaseApplication;
import com.lirui.lib_common.constant.DownloadStatusEnum;
import com.lirui.lib_common.net.HttpHelper;
import com.lirui.lib_common.net.config.HttpConfig;
import com.lirui.lib_common.util.CloseUtils;
import com.lirui.lib_common.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 下载任务
 */
public class DownloadTask implements Serializable {
    public String mUrl;
    public Disposable mDisposable;

    public DownloadTask(String mUrl) {
        this.mUrl = mUrl;
    }

    public void start() {
        HttpHelper.getInstance().getProxy(DownloadApi.class, new HttpConfig.Builder().isDownloadListener(true).url(mUrl).builder())
                .downloadFile(mUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String filePath = "";
                        boolean result = writeResponseBodyToDisk(responseBody, FileUtils.getUrlFileName(mUrl));
                        if (result) {
                            filePath = BaseApplication.getInstance().downloadPath + FileUtils.getUrlFileName(mUrl);
                        }
                        EventBus.getDefault().post(new DownloadEvent(mUrl, 100, result ? DownloadStatusEnum.SUCCESS : DownloadStatusEnum.ERROR, filePath));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new DownloadEvent(mUrl, -1, DownloadStatusEnum.ERROR, ""));
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            EventBus.getDefault().post(new DownloadEvent(mUrl, -1, DownloadStatusEnum.CANCEL, ""));
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {

        String store_path = BaseApplication.getInstance().downloadPath;
        if (!FileUtils.createOrExistsFile(store_path + fileName)) {
            return false;
        }
        File futureStudioIconFile = new File(store_path + fileName);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[4096];

            inputStream = body.byteStream();
            outputStream = new FileOutputStream(futureStudioIconFile);

            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);

            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            CloseUtils.closeIO(inputStream);
            CloseUtils.closeIO(outputStream);
        }
    }
}
