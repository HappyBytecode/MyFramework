package com.lirui.lib_common.version.bean;

import android.content.ContentValues;

/**
 * 保存版本号及下载路径
 */

public class VersionBean {
    private int versionCode;//版本号
    private String filePath;//版本保存路径
    private String downloadTime;//下载时间
    private int isMustUpdate;//是否必须要更新
    private String updateMsg;//更新内容

    public VersionBean(int versionCode, String updateMsg, boolean isMustUpdate, String filePath, String downloadTime) {
        this.versionCode = versionCode;
        this.updateMsg = updateMsg;
        this.filePath = filePath;
        this.downloadTime = downloadTime;
        this.isMustUpdate = isMustUpdate ? 1 : 0;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    public boolean isMustUpdate() {
        return isMustUpdate == 1;
    }

    public void setMustUpdate(boolean mustUpdate) {
        isMustUpdate = mustUpdate ? 1 : 0;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(String downloadTime) {
        this.downloadTime = downloadTime;
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put("versionCode", versionCode);
        values.put("filePath", filePath);
        values.put("updateMsg", updateMsg);
        values.put("time", downloadTime);
        values.put("isMustUpdate", isMustUpdate);
        return values;
    }
}
