/*
 * Copyright (c) 2017 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish, 
 * distribute, sublicense, create a derivative work, and/or sell copies of the 
 * Software in any work that is designed, intended, or marketed for pedagogical or 
 * instructional purposes related to programming, coding, application development, 
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works, 
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.lirui.learn.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.SparseArray;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.lirui.lib_common.net.upload.UploadParam;
import com.lirui.lib_common.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 跟踪在相机的图像中检测到的面孔，并收集其位置和地标。
 */
class FaceTracker extends Tracker<Face> {

    private static final String TAG = "FaceTracker";
    private final IFaceListener faceListener;
    private FaceTrackerData trackerData;
    private FaceData mFaceData;

    private boolean mPreviousIsLeftEyeOpen = true;
    private boolean mPreviousIsRightEyeOpen = true;

    private Map<Integer, PointF> mPreviousLandmarkPositions = new HashMap<>();

    public FaceTracker(FaceTrackerData trackerData, IFaceListener faceListener) {
        this.trackerData = trackerData;
        this.faceListener = faceListener;
        mFaceData = new FaceData();
    }

    // 1

    /**
     * 当检测到新的面部并且其跟踪开始时调用。
     * 您正在使用它创建一个新的FaceGraphic实例，这是有道理的：当检测到一个新的面孔时，您要创建新的AR图像来绘制它。
     */
    @Override
    public void onNewItem(int id, Face face) {
        trackerData.setFaceId(id);
    }

    // 2

    /**
     * 当跟踪的脸部的某些属性（位置，角度或状态）发生变化时调用
     * 您正在使用它将FaceGraphic实例添加到GraphicOverlay，然后调用FaceGraphic的更新方法，该方法沿着跟踪的面部数据传递。
     */
    public void onUpdate(FaceDetector.Detections detectionResults, Face face) {
        updatePreviousLandmarkPositions(face);
        updateFaceDate(face);
        detect();
    }

    /**
     * 检测摇头及微笑
     */
    private void detect() {
        if (faceListener == null||trackerData.isCameraStatus()) {
            return;
        }
        trackerData.setFaceData(mFaceData);
        faceListener.setTip(trackerData.getTip());
        if (trackerData.isCamera() && trackerData.isReady()) {
            trackerData.clearData();
            faceListener.onReady();
        }
    }

    /**
     * 更新脸部数据
     */
    private void updateFaceDate(Face face) {
        // Get head angles.
        mFaceData.setEulerY(face.getEulerY());
        mFaceData.setEulerZ(face.getEulerZ());
        // Get face dimensions.
        mFaceData.setPosition(face.getPosition());
        mFaceData.setWidth(face.getWidth());
        mFaceData.setHeight(face.getHeight());

        // Get the positions of facial landmarks.
        mFaceData.setLeftEyePosition(getLandmarkPosition(face, Landmark.LEFT_EYE));
        mFaceData.setRightEyePosition(getLandmarkPosition(face, Landmark.RIGHT_EYE));
        mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_CHEEK));
        mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_CHEEK));
        mFaceData.setNoseBasePosition(getLandmarkPosition(face, Landmark.NOSE_BASE));
        mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_EAR));
        mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_EAR_TIP));
        mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_EAR));
        mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_EAR_TIP));
        mFaceData.setMouthLeftPosition(getLandmarkPosition(face, Landmark.LEFT_MOUTH));
        mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.BOTTOM_MOUTH));
        mFaceData.setMouthRightPosition(getLandmarkPosition(face, Landmark.RIGHT_MOUTH));

        //以下代码用于检测眼睛打开与关闭，是否微笑
        // 1
        final float EYE_CLOSED_THRESHOLD = 0.4f;
        float leftOpenScore = face.getIsLeftEyeOpenProbability();
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            mFaceData.setLeftEyeOpen(mPreviousIsLeftEyeOpen);
        } else {
            mFaceData.setLeftEyeOpen(leftOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsLeftEyeOpen = mFaceData.isLeftEyeOpen();
        }
        float rightOpenScore = face.getIsRightEyeOpenProbability();
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            mFaceData.setRightEyeOpen(mPreviousIsRightEyeOpen);
        } else {
            mFaceData.setRightEyeOpen(rightOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsRightEyeOpen = mFaceData.isRightEyeOpen();
        }

        // 2
        // See if there's a smile!
        // Determine if person is smiling.
        final float SMILING_THRESHOLD = 0.8f;
        mFaceData.setSmiling(face.getIsSmilingProbability() > SMILING_THRESHOLD);

        //trackerData.setLastHead();
    }

    // 3

    /**
     * 当被追踪的表面被假定为暂时地永久地消失时被调用。两者都从覆盖图中移除FaceGraphic实例
     *
     * @param detectionResults
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
    }

    @Override
    public void onDone() {
    }

    // Facial landmark utility methods
    // ===============================

    /**
     * Given a face and a facial landmark position,
     * return the coordinates of the landmark if known,
     * or approximated coordinates (based on prior data) if not.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF landmarkPosition = mPreviousLandmarkPositions.get(landmarkId);
        if (landmarkPosition == null) {
            return null;
        }

        float x = face.getPosition().x + (landmarkPosition.x * face.getWidth());
        float y = face.getPosition().y + (landmarkPosition.y * face.getHeight());
        return new PointF(x, y);
    }

    private void updatePreviousLandmarkPositions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousLandmarkPositions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }
}
