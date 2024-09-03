package com.lirui.lib_common.image;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.lirui.lib_common.R;
import com.lirui.lib_common.base.BaseApplication;
import com.lirui.lib_common.constant.IntentResultConstants;
import com.lirui.lib_common.image.cropper.CropImage;
import com.lirui.lib_common.image.cropper.CropImageView;
import com.lirui.lib_common.image.luban.Luban;
import com.lirui.lib_common.image.luban.OnCompressListener;
import com.lirui.lib_common.image.photopicker.PhotoPickerActivity;
import com.lirui.lib_common.image.photopicker.PhotoPickerConfig;
import com.lirui.lib_common.image.photopicker.bean.Photo;
import com.lirui.lib_common.util.FileUtils;
import com.lirui.lib_common.util.IntentUtils;
import com.lirui.lib_common.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 拍照或由相册获取入口
 */

public class ImageWay {

    public static final int FREE = -1;

    private Activity mActivity;
    private Fragment mFragment;

    private String cameraFilePath;
    private Uri cameraFileUri;
    private String cameraCropFilePath;
    private Uri cameraCropFileUri;

    private boolean isCrop = true;//是否裁剪照片
    private boolean isOval;//是否裁剪原型照片
    private int aspectRatioX = FREE;//宽高比例
    private int aspectRatioY = FREE;
    private boolean fixAspectRatio = true;//是否自由缩放

    //图片处理进度监听
    private OnPhotoProgressListener listener;

    public ImageWay(Activity mContext) {
        this.mActivity = mContext;
    }

    public ImageWay(Fragment mFragment) {
        this.mFragment = mFragment;
        this.mActivity = mFragment.getActivity();
    }

    //设置进度监听
    public void setOnPhotoProgressListener(OnPhotoProgressListener listener) {
        this.listener = listener;
    }

    /**
     * 打开相册
     */
    public void openGallery(PhotoPickerConfig config) {
        if (mFragment != null) {
            PhotoPickerActivity.start(mFragment, config);
        } else {
            PhotoPickerActivity.start(mActivity, config);
        }

    }

    /**
     * 打开照相机
     */
    public void openCameraNoCrop() {
        isCrop = false;
        openCamera();
    }

    /**
     * 带裁剪的拍照
     *
     * @param isOval       是否裁剪圆形
     * @param aspectRatioX 宽比例
     * @param aspectRatioY 高比例
     */
    public void openCameraWithCrop(boolean isOval, int aspectRatioX, int aspectRatioY) {
        this.isOval = isOval;
        this.aspectRatioX = aspectRatioX;
        this.aspectRatioY = aspectRatioY;
        if (aspectRatioX == FREE || aspectRatioY == FREE) {
            fixAspectRatio = false;
        } else {
            fixAspectRatio = true;
        }
        isCrop = true;
        openCamera();
    }

    private void openCamera() {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) { // Always true pre-M
                            try {
                                Intent intent = dispatchTakePictureIntent();
                                if (mFragment != null) {
                                    mFragment.startActivityForResult(intent, IntentResultConstants.REQUEST_TAKE_PHOTO);
                                } else {
                                    mActivity.startActivityForResult(intent, IntentResultConstants.REQUEST_TAKE_PHOTO);
                                }
                                if (listener != null) {
                                    listener.onStart();
                                }
                            } catch (IOException e) {
                                ToastUtils.warning(mActivity, "文件异常").show();
                                if (listener != null) {
                                    listener.onFailed();
                                }
                            } catch (ActivityNotFoundException e) {
                                ToastUtils.warning(mActivity, "该设备不支持拍摄").show();
                                if (listener != null) {
                                    listener.onFailed();
                                }
                            }
                        } else {
                            ToastUtils.warning(mActivity, mActivity.getString(R.string.permissionsError,"读写权限，拍照权限")).show();
                            if (listener != null) {
                                listener.onFailed();
                            }
                        }
                    }
                });
    }

    /**
     * 拍照Intent
     */
    private Intent dispatchTakePictureIntent() throws IOException {
        cameraFilePath = BaseApplication.getInstance().imagePath + FileUtils.getFileName() + ".jpg";
        cameraFileUri = FileUtils.getFileUri(new File(cameraFilePath));
        return IntentUtils.getCaptureIntent(cameraFileUri);
    }

    /**
     * ActivityResult
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //照片拍摄成功
        if (requestCode == IntentResultConstants.REQUEST_TAKE_PHOTO) {
            if (resultCode == mActivity.RESULT_OK) {
                if (isCrop) {
                    //裁剪，压缩
                    cameraCropFilePath = BaseApplication.getInstance().cachePath + "crop_" + FileUtils.getFileName() + ".jpg";
                    File file = new File(cameraCropFilePath);
                    cameraCropFileUri = FileUtils.getFileUri(file);
                    if (mFragment == null) {
                        CropImage.ActivityBuilder builder = CropImage.activity(cameraFileUri)
                                .setOutputUri(cameraCropFileUri)
                                .setFixAspectRatio(fixAspectRatio)
                                .setCropShape(isOval ? CropImageView.CropShape.OVAL : CropImageView.CropShape.RECTANGLE);
                        if (fixAspectRatio) {
                            builder.setAspectRatio(aspectRatioX, aspectRatioY);
                        }
                        builder.start(mActivity);
                    } else {
                        CropImage.ActivityBuilder builder = CropImage.activity(cameraFileUri)
                                .setOutputUri(cameraCropFileUri)
                                .setFixAspectRatio(fixAspectRatio)
                                .setCropShape(isOval ? CropImageView.CropShape.OVAL : CropImageView.CropShape.RECTANGLE);
                        if (fixAspectRatio) {
                            builder.setAspectRatio(aspectRatioX, aspectRatioY);
                        }
                        builder.start(mActivity, mFragment);
                    }
                } else {
                    //压缩
                    compress(cameraFilePath);
                }
            } else if (resultCode == mActivity.RESULT_CANCELED) {
                if (listener != null) {
                    listener.onCancel();
                }
            }
            return;
        }
        //裁剪结果
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == mActivity.RESULT_OK) {
                //Uri resultUri = result.getUri();
                compress(cameraCropFilePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                ToastUtils.error(mActivity, "裁剪失败").show();
                if (listener != null) {
                    listener.onFailed();
                }
            } else {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        }
    }

    private void compress(String filePath) {
        Luban.with()
                .load(filePath)                     //传人要压缩的图片
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(ArrayList<String> filePath) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        if (listener != null) {
                            ArrayList<Photo> compressPhotos = new ArrayList<Photo>();
                            compressPhotos.add(new Photo(-1, filePath.get(0)));
                            listener.onSuccess(compressPhotos);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.error(mActivity, "图片压缩失败").show();
                        if (listener != null) {
                            listener.onFailed();
                        }
                    }
                }).launch();
    }

    /**
     * 拍照进度
     */
    public interface OnPhotoProgressListener {
        void onStart();

        void onSuccess(List<Photo> selectPhotos);

        void onFailed();

        void onCancel();
    }
}




