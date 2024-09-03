package com.lirui.lib_common.net.download;


import android.os.Parcel;
import android.os.Parcelable;

import com.lirui.lib_common.constant.DownloadStatusEnum;

public class DownloadEvent implements Parcelable {
    public String filePath;
    public DownloadStatusEnum status;
    public String url;
    public int progress;

    public DownloadEvent(String url, int progress, DownloadStatusEnum status, String filePath) {
        this.url = url;
        this.progress = progress;
        this.status=status;
        this.filePath=filePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
        dest.writeString(this.url);
        dest.writeInt(this.progress);
    }

    protected DownloadEvent(Parcel in) {
        int tmpStatus = in.readInt();
        this.status = tmpStatus == -1 ? null : DownloadStatusEnum.values()[tmpStatus];
        this.url = in.readString();
        this.progress = in.readInt();
    }

    public static final Creator<DownloadEvent> CREATOR = new Creator<DownloadEvent>() {
        @Override
        public DownloadEvent createFromParcel(Parcel source) {
            return new DownloadEvent(source);
        }

        @Override
        public DownloadEvent[] newArray(int size) {
            return new DownloadEvent[size];
        }
    };
}
