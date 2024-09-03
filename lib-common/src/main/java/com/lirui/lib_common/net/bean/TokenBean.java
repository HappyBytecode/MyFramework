package com.lirui.lib_common.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 获取返回的Token
 */

public class TokenBean implements Parcelable {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
    }

    public TokenBean() {
    }

    protected TokenBean(Parcel in) {
        this.token = in.readString();
    }

    public static final Creator<TokenBean> CREATOR = new Creator<TokenBean>() {
        @Override
        public TokenBean createFromParcel(Parcel source) {
            return new TokenBean(source);
        }

        @Override
        public TokenBean[] newArray(int size) {
            return new TokenBean[size];
        }
    };
}
