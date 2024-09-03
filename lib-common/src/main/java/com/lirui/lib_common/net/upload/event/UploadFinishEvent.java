package com.lirui.lib_common.net.upload.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.lirui.lib_common.net.upload.UploadTask;

/**
 * 任务上传完成事件，成功，失败
 */
public class UploadFinishEvent implements Parcelable {
    public boolean isSuccess;
    public UploadTask task;

    public UploadFinishEvent(UploadTask task, boolean isSuccess) {
        this.task = task;
        this.isSuccess = isSuccess;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSuccess ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.task);
    }

    protected UploadFinishEvent(Parcel in) {
        this.isSuccess = in.readByte() != 0;
        this.task = (UploadTask) in.readSerializable();
    }

    public static final Creator<UploadFinishEvent> CREATOR = new Creator<UploadFinishEvent>() {
        @Override
        public UploadFinishEvent createFromParcel(Parcel source) {
            return new UploadFinishEvent(source);
        }

        @Override
        public UploadFinishEvent[] newArray(int size) {
            return new UploadFinishEvent[size];
        }
    };
}
