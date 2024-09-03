package com.lirui.lib_common.image.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lirui.lib_common.util.FileUtils;

/**
 * 图片
 */
public class Photo implements Parcelable {

    private int id;//-1：不是从相册中取得的
    private String path;
    private String filePath;
    private boolean isSelected;

    public Photo(int id, String path) {
        this.id = id;
        this.path = path;
        this.filePath = FileUtils.getUrlPath(path);
    }

    public Photo(String path) {
        this.id = -1;
        this.path = path;
        this.filePath = FileUtils.getUrlPath(path);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeString(this.filePath);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected Photo(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.filePath = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
