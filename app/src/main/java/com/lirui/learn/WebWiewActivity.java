package com.lirui.learn;

import android.os.Bundle;

import com.lirui.lib_common.base.BaseWebViewActivity;

public class WebWiewActivity extends BaseWebViewActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWebView().loadUrl("https://h5.m.jd.com/active/download/download.html?channel=jd-msy1");
    }

}
