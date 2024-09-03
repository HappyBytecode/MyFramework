package com.lirui.learn.face;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.lirui.learn.face.camera.CameraSourcePreview;
import com.lirui.learndemo.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.base.BaseView;
import com.lirui.lib_common.constant.Permissions;
import com.lirui.lib_common.net.netError.ApiException;
import com.lirui.lib_common.net.upload.UploadParam;
import com.lirui.lib_common.net.upload.UploadService;
import com.lirui.lib_common.util.FileUtils;
import com.lirui.lib_common.util.LogUtils;
import com.lirui.lib_common.util.SizeUtils;
import com.lirui.lib_common.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.functions.Consumer;

/**
 * 人脸检测app
 * 步骤：
 * 1.判断当前检测出的脸部是否与上一个一致（不一致，所有检测归零重新检测）CheckObject
 * 2.检测人脸是否在框中
 * 3.检测人脸是否面朝正方向
 * 4.检测点头，眨眼，微笑
 * 5.拍照，裁剪，显示
 */
public class FaceDetectActivity extends AbsBaseActivity implements IFaceListener, BaseView {

    private static final int RC_HANDLE_GMS = 9001;

    private CameraSourcePreview mPreview;
    private TextView mTvTextShow;
    private ImageView mFlipButton;
    private ImageView iv_camera;

    private FaceDetector detector;
    private CameraSource mCameraSource = null;
    private boolean mIsFrontFacing = true;//是否为前摄像头
    private int screenWidth, screenHeight;

    private FaceTrackerData trackerData;

    private Handler handler;
    private boolean isVisible;

    private FaceDetectPresenter faceDetectPresenter;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_face_detect;
    }

    @Override
    protected void initViewsAndEvents(final Bundle savedInstanceState) {
        new RxPermissions(this).request(Permissions.STORAGE_CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            init(savedInstanceState);
                        } else {
                            ToastUtils.warning(mContext, getResources().getString(com.lirui.lib_common.R.string.permissionsError, "读写权限")).show();
                        }
                    }
                });
    }

    /**
     * 初始化
     */
    private void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }
        initData();
        initView();
        initListener();
        createCameraSource();
    }

    private void initView() {
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mTvTextShow = (TextView) findViewById(R.id.tv_text_show);
        mFlipButton = (ImageView) findViewById(R.id.flipButton);
        iv_camera = (ImageView) findViewById(R.id.iv_camera);
    }

    private void initData() {
        faceDetectPresenter = new FaceDetectPresenter(this);
        createHandler();
        trackerData = new FaceTrackerData(-1);
        screenWidth = SizeUtils.getScreenWidth();
        screenHeight = SizeUtils.getScreenHeight();
    }

    private void initListener() {
        mFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsFrontFacing = !mIsFrontFacing;

                if (mCameraSource != null) {
                    mCameraSource.release();
                    mCameraSource = null;
                }
                createCameraSource();
                startCameraSource();
            }
        });
    }

    private void createCameraSource() {
        Log.d(TAG, "createCameraSource called.");

        // 1.创建一个FaceDetector对象，它可以从相机的数据流中检测图像中的人脸
        Context context = getApplicationContext();
        detector = createFaceDetector(context);

        // 2.确定哪个相机当前是活动的
        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CameraSource.CAMERA_FACING_BACK;
        }

        // 3
        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)//指定要使用的相机
                /*
                    从相机设置预览图像的分辨率。
                    较低的分辨率（如320×240分辨率）通过预算设备更好地工作，并提供更快的面部检测。
                    更高分辨率（640×480及以上）适用于高端设备，可以更好地检测小面孔和面部特征。
                    尝试不同的设置。
                    */
                .setRequestedPreviewSize(screenWidth, screenHeight)
                /*
                     设置摄像机帧速率。
                     更高的帧速率意味着更好的面部跟踪，但使用更多的处理器能力
                     试验不同的帧率。
                 */
                .setRequestedFps(60.0f)
                /*
                     关闭或打开自动对焦。
                     保持此设置为真，以获得更好的面部检测和用户体验。
                     如果设备没有自动对焦，则不起作用。
                 */
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCameraSource() {
        Log.d(TAG, "startCameraSource called.");

        // Make sure that the device has Google Play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, null);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @NonNull
    private FaceDetector createFaceDetector(final Context context) {
        Log.d(TAG, "createFaceDetector called.");

        //1.创建一个FaceDetector对象
        FaceDetector detector = new FaceDetector.Builder(context)
                /**
                 * 如果不需要检测到面部标志（这使得脸部检测更快），设置为NO_LANDMARKS，
                 * 如果检测到面部标志，则设置为ALL_LANDMARK。
                 */
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                /**
                 * 如果不需要检测对象的眼睛是否打开或关闭，或者是否正在微笑（加快了脸部检测速度），设置为NO_CLASSIFICATIONS
                 * 否则设置为ALL_CLASSIFICATIONS（如果它们应该检测到）。
                 */
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                /**
                 * 启用/禁用脸部跟踪，每个脸部保持一致的身份识别。
                 * 如果您需要脸部跟踪才能处理实时视频和多个人脸，请将其设置为true。
                 */
                .setTrackingEnabled(true)
                /**
                 * 设置为FAST_MODE：以检测较少的面孔（但更快）或
                 * 设置为ACCURATE_MODE来检测更多面部（但更慢）并检测面部的欧拉Y角度
                 */
                .setMode(FaceDetector.ACCURATE_MODE)
                /**
                 * 设置为true以仅检测框架中最突出的面
                 */
                .setProminentFaceOnly(mIsFrontFacing)
                /**
                 * 指定将被检测到的最小面部大小，表示为脸部相对于图像宽度的宽度的比例
                 */
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();
        //2.
        MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
            @Override
            public Tracker<Face> create(Face face) {
                return new FaceTracker(trackerData, FaceDetectActivity.this);
            }
        };
        //3.
        /**
         * 当检测到脸部时，会将结果传递给处理器，从而确定应采取什么行动。
         如果您一次只想处理一张脸，您将使用一个Processor的实例。
         在这个应用程序中，您将处理多个面，因此您将创建一个MultiProcessor实例，
         为每个检测到的面创建一个新的FaceTracker实例。
         一旦创建，我们将处理器连接到检测器。
         */
        Detector.Processor<Face> processor = new MultiProcessor.Builder<>(factory).build();
        detector.setProcessor(processor);
        //4.
        if (!detector.isOperational()) {
            LogUtils.w(TAG, "Face detector dependencies are not yet available.");

            // Check the device's storage.  If there's little available storage, the native
            // face detection library will not be downloaded, and the app won't work,
            // so notify the user.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                LogUtils.w(TAG, getString(R.string.low_storage_error));
                ToastUtils.error(mContext, getResources().getString(R.string.low_storage_error)).show();
            }
        }
        return detector;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume called.");
        isVisible = true;
        trackerData = new FaceTrackerData(-1);
        checkFace();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        mPreview.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onReady() {
        trackerData.setCameraStatus(true);
        mPreview.takePicture(new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .build();
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Face> faces = detector.detect(frame);
                if (faces != null && faces.size() > 0) {
                    //计算面部中心的x坐标和y坐标
                    bitmap = getCropImage(faces.valueAt(0), bitmap);
                }
                detector.release();
                String filePath = FileUtils.saveBitmap2file(bitmap);
                ArrayList<UploadParam> fileList = new ArrayList();
                fileList.add(new UploadParam("files", filePath));
                faceDetectPresenter.uploadImage(filePath);
            }
        });
    }

    @Override
    public void setTip(final String tip) {
        handler.sendEmptyMessage(0);
    }

    private void createHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    mTvTextShow.setText(trackerData.getTip());
                    return;
                }
            }
        };
    }

    private void checkFace() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - trackerData.getLastTrackTime() > 500) {
                    trackerData.setFaceId(-1);
                    handler.sendEmptyMessage(0);
                }
                if (isVisible) {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private Bitmap getCropImage(Face face, Bitmap bitmap) {
        if (face == null) {
            return bitmap;
        }
        float left = face.getPosition().x < 0 ? 0 : face.getPosition().x;
        float top = face.getPosition().y < 0 ? 0 : face.getPosition().y;
        float width = face.getWidth() > bitmap.getWidth() ? bitmap.getWidth() : face.getWidth();
        float height = face.getHeight() > bitmap.getHeight() ? bitmap.getHeight() : face.getHeight();
        return Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) width, (int) height, null, true);
    }

    @Override
    public void onStart(String url, HashMap<String, Object> request) {
        ToastUtils.info(mContext, "图片上传中").show();
    }

    @Override
    public void onSuccess(String url, HashMap<String, Object> request, Object t) {
        ToastUtils.info(mContext, "上传成功").show();
        trackerData.setCameraStatus(false);
        finish();
    }

    @Override
    public void onCompleted(String url, HashMap<String, Object> request) {

    }

    @Override
    public void onError(String url, HashMap<String, Object> request, ApiException e) {
        trackerData.setCameraStatus(false);
        ToastUtils.info(mContext, "上传失败").show();
        LogUtils.e(e);
    }
}