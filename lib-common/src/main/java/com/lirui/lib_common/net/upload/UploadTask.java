package com.lirui.lib_common.net.upload;

import android.app.Notification;

import com.lirui.lib_common.net.HttpHelper;
import com.lirui.lib_common.net.upload.event.UploadFinishEvent;
import com.lirui.lib_common.util.FileTypeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 上传任务监听
 */
public class UploadTask implements Serializable{
    public int id;
    public String mUrl;
    public Disposable mDisposable;
    public Notification mNotification;
    public HashMap<String, File> mFileMap;
    public HashMap<String, String> mParamMap;
    public long total = 0;
    public long progress = 0;
    public int current_percent = 0;

    public UploadTask(int id, String mUrl,
                      HashMap<String, File> map, HashMap<String, String> paramMap) {
        this.id = id;
        this.mUrl = mUrl;
        this.mFileMap = map;
        this.mParamMap = paramMap;
    }

    public void start() {
        Map<String, RequestBody> files = new HashMap<>();

        Iterator fileIterator = mFileMap.entrySet().iterator();
        while (fileIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) fileIterator.next();
            String key = (String) entry.getKey();
            File file = (File) entry.getValue();
            String fileName = file.getName();//FileUtils.getUrlFileName(file.getAbsolutePath());
            RequestBody fileBody = RequestBody.create(MediaType.parse(getContentType(file)), file);
            files.put("" + key + "\"; filename=\"" + fileName + "",
                    new UploadRequestBody(fileBody, fileName, this));
        }

        Iterator paramIterator = mParamMap.entrySet().iterator();
        while (paramIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) paramIterator.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();

            files.put(key, RequestBody.create(MediaType.parse("text/plain"), val));
        }


        HttpHelper.getInstance().getProxy(UploadApi.class)
                .uploadFile(mUrl, files)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(ResponseBody body) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new UploadFinishEvent(UploadTask.this, false));
                    }

                    @Override
                    public void onComplete() {
                        EventBus.getDefault().post(new UploadFinishEvent(UploadTask.this, true));
                    }
                });
    }

    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    //获取文件的上传类型，图片格式为image/png,image/jpg等。非图片为application/octet-stream
    private String getContentType(File f) {

        if (!FileTypeUtil.isImageFile(f.getAbsolutePath())) {
            return "application/octet-stream";
        } else {
            return "image/" + FileTypeUtil.getFileType(f.getAbsolutePath());
        }
    }
}
