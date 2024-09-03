package com.lirui.lib_common.net.upload.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.lirui.lib_common.net.upload.UploadTask;

/**
 * 文件上传进度
 */
public class UploadFileEvent implements Parcelable {
    //本次读取文件大小，用于任务判断总体下载进度
    public long byteCount = 0;
    //当前文件上传进度
    public int percent = 0;
    //当前文件名称
    public String fileName = "";
    //当前文件所属任务
    public UploadTask task;

    public UploadFileEvent(UploadTask task,String fileName, int percent, long byteCount) {
        this.fileName = fileName;
        this.percent = percent;
        this.byteCount = byteCount;
        this.task = task;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.byteCount);
        dest.writeInt(this.percent);
        dest.writeString(this.fileName);
        dest.writeSerializable(this.task);
    }

    protected UploadFileEvent(Parcel in) {
        this.byteCount = in.readLong();
        this.percent = in.readInt();
        this.fileName = in.readString();
        this.task = (UploadTask) in.readSerializable();
    }

    public static final Creator<UploadFileEvent> CREATOR = new Creator<UploadFileEvent>() {
        @Override
        public UploadFileEvent createFromParcel(Parcel source) {
            return new UploadFileEvent(source);
        }

        @Override
        public UploadFileEvent[] newArray(int size) {
            return new UploadFileEvent[size];
        }
    };
}
