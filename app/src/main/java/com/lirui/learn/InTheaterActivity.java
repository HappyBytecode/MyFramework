package com.lirui.learn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import com.lirui.learndemo.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.base.BaseView;
import com.lirui.lib_common.net.netError.ApiException;
import com.lirui.lib_common.util.LogUtils;

import java.util.HashMap;

public class InTheaterActivity extends AbsBaseActivity implements BaseView {

    private InTheaterPresenter inTheaterPresenter;
    private Toolbar mToolbar;
    private RecyclerView mRcvContent;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, InTheaterActivity.class));
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_in_theater;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        inTheaterPresenter = new InTheaterPresenter(this);
        inTheaterPresenter.inTheaters();
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRcvContent = (RecyclerView) findViewById(R.id.rcv_content);

    }

    @Override
    public void onStart(String url, HashMap<String, Object> request) {
        LogUtils.i("start", url);
    }

    @Override
    public void onSuccess(String url, HashMap<String, Object> request, Object t) {
        LogUtils.i("success", url);
    }

    @Override
    public void onCompleted(String url, HashMap<String, Object> request) {
        LogUtils.i("completed", url);
    }

    @Override
    public void onError(String url, HashMap<String, Object> request, ApiException e) {
        LogUtils.i("error", url);
    }
}
