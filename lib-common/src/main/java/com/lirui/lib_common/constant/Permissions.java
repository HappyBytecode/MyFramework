package com.lirui.lib_common.constant;

import android.Manifest;

/**
 * Created by cenxiaozhong on 2017/8/18.
 */

public class Permissions {


    public static final String[] CAMERA;
    public static final String[] LOCATION;
    public static final String[] STORAGE;
    public static final String[] STORAGE_CAMERA;

    static {


        CAMERA = new String[]{
                Manifest.permission.CAMERA};


        LOCATION = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};


        STORAGE = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        STORAGE_CAMERA = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


}
