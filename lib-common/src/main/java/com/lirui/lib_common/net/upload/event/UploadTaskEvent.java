package com.lirui.lib_common.net.upload.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.lirui.lib_common.net.upload.UploadTask;

/**
 * 任务上传进度，一个任务可能包含一个或多个文件上传
 */
public class UploadTaskEvent implements Parcelable {
    //当前文件上传进度
    public int percent = 0;
    public UploadTask task;

    public UploadTaskEvent(UploadTask task,int percent) {
        this.percent = percent;
        this.task = task;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.percent);
        dest.writeSerializable(this.task);
    }

    protected UploadTaskEvent(Parcel in) {
        this.percent = in.readInt();
        this.task = (UploadTask) in.readSerializable();
    }

    public static final Creator<UploadTaskEvent> CREATOR = new Creator<UploadTaskEvent>() {
        @Override
        public UploadTaskEvent createFromParcel(Parcel source) {
            return new UploadTaskEvent(source);
        }

        @Override
        public UploadTaskEvent[] newArray(int size) {
            return new UploadTaskEvent[size];
        }
    };
}
