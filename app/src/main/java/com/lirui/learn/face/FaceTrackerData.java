package com.lirui.learn.face;

import com.lirui.lib_common.util.LogUtils;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/09/05
 *      desc    :摇头，眨眼
 *      version : 1.0
 *  </pre>
 */

public class FaceTrackerData {
    private int faceId = -1;

    private boolean node;
    private long lastNodeTime;
    private float minNode = 0;
    private float maxNode = 0;

    private boolean cameraStatus;

    private boolean rightBlink = true;
    private long lastRightBlinkTime;
    private boolean lastRightBlink;

    private boolean leftBlink = true;
    private long lastLeftBlinkTime;
    private boolean lastLeftBlink;

    private FaceData faceData;

    private long lastTrackTime = 0;

    private int timeInterval = 3000;
    public static final int ANGLE = 16;

    public FaceTrackerData(int faceId) {
        this.faceId = faceId;
    }

    public void setLastNode(float lastNode) {
        if (lastNode > maxNode) {
            maxNode = lastNode;
        }
        if (lastNode < minNode) {
            minNode = lastNode;
        }

        if (lastTrackTime - lastNodeTime < timeInterval) {
            if (maxNode > ANGLE && minNode < -ANGLE) {
                node = true;
            }
        } else {
            maxNode = 0;
            minNode = 0;
        }
        lastNodeTime = lastTrackTime;
        LogUtils.i(faceId + "," + lastNode + "," + maxNode + "," + minNode);
    }

    public void setLastRightBlink(boolean lastRightBlink) {
        if (lastTrackTime - lastRightBlinkTime <= timeInterval) {
            if (this.lastRightBlink != lastRightBlink) {
                rightBlink = true;
            }
        }
        this.lastRightBlink = lastRightBlink;
        lastRightBlinkTime = lastTrackTime;
    }

    public void setLastLeftBlink(boolean lastLeftBlink) {
        if (lastTrackTime - lastLeftBlinkTime <= timeInterval) {
            if (this.lastLeftBlink != lastLeftBlink) {
                leftBlink = true;
            }
        }
        this.lastLeftBlink = lastLeftBlink;
        lastLeftBlinkTime = lastTrackTime;
    }

    public void setFaceId(int faceId) {
        if (System.currentTimeMillis() - lastTrackTime < 1000) {
            this.faceId = faceId;
        }
        if (faceId != this.faceId) {
            LogUtils.e("faceid", faceId);
            clearData();
        }
        this.faceId = faceId;
    }

    /**
     * 是否可以直接进行拍照
     */
    public boolean isCamera() {
        return node && (leftBlink || rightBlink);
    }

    public boolean isNode() {
        return node;
    }

    public boolean isBlink() {
        return rightBlink || leftBlink;
    }

    /**
     * 获得提示信息
     */
    public String getTip() {
        if (cameraStatus) {
            return "正在进行拍照";
        }
        if (faceId == -1) {
            return "未检测到人脸";
        }
        if (!node) {
            return "请左右摇头";
        }
        if (!(leftBlink || rightBlink)) {
            return "请眨眨眼睛";
        }
        return "请面向摄像头,睁开眼睛,准备拍照";
    }

    public void clearData() {
        node = false;
        rightBlink = false;
        leftBlink = false;
        lastNodeTime = 0;
        lastRightBlinkTime = 0;
        lastLeftBlinkTime = 0;
        minNode = 0;
        maxNode = 0;
    }

    public void setFaceData(FaceData faceData) {
        lastTrackTime = System.currentTimeMillis();
        this.faceData = faceData;
        if (!isNode()) {
            setLastNode(faceData.getEulerZ());
            return;
        }
        if (!isBlink()) {
            setLastLeftBlink(faceData.isLeftEyeOpen());
            setLastRightBlink(faceData.isRightEyeOpen());
            return;
        }
    }

    public boolean isCameraStatus() {
        return cameraStatus;
    }

    public void setCameraStatus(boolean cameraStatus) {
        this.cameraStatus = cameraStatus;
    }


    public boolean isReady() {
        return (faceData.getEulerY() > -FaceTrackerData.ANGLE || faceData.getEulerY() < FaceTrackerData.ANGLE)
                && faceData.isLeftEyeOpen() && faceData.isRightEyeOpen();
    }

    public int getFaceId() {
        return faceId;
    }

    public FaceData getFaceData() {
        return faceData;
    }

    public long getLastTrackTime() {
        return lastTrackTime;
    }
}
