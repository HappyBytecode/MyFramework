package com.lirui.lib_common.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lirui.lib_common.R;
import com.lirui.lib_common.net.download.DownloadEvent;
import com.lirui.lib_common.util.SizeUtils;
import com.lirui.lib_common.view.ToolBarHelper;
import com.lirui.lib_common.view.web.SmartWebView;
import com.lirui.lib_common.view.web.listener.IWebLifeCycle;
import com.lirui.lib_common.view.web.listener.IWebLoadListener;
import com.lirui.lib_common.view.web.progress.BaseWebProgress;
import com.lirui.lib_common.view.web.progress.WebProgress;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseWebViewActivity extends AbsBaseActivity implements View.OnClickListener, IWebLifeCycle, IWebLoadListener {

    private TextView mTvTitle;
    private FrameLayout mFlContent;

    private SmartWebView mWebView;
    private BaseWebProgress mWebProgress;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        ToolBarHelper toolBarHelper = new ToolBarHelper.Builder(this).setTitle("加载中...").setLeftIcon(this).builder();
        mTvTitle = (TextView) toolBarHelper.getViewById(R.id.tv_title);
        Toolbar.LayoutParams lp = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.LEFT;
        mTvTitle.setLayoutParams(lp);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        createGroupWithWeb();
        mWebView.setOnWebLoadListener(this);
    }

    public SmartWebView getWebView() {
        return mWebView;
    }

    /**
     * 创建整个WebView布局，包括加载进度下拉刷新
     */
    private void createGroupWithWeb() {
        mWebView = new SmartWebView(this);
        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(-1, -1);
        mFlContent.addView(mWebView, mLayoutParams);

        FrameLayout.LayoutParams lp = null;
        mWebProgress = new WebProgress(this);
        lp = new FrameLayout.LayoutParams(-2, SizeUtils.dp2px(2));
        lp.gravity = Gravity.TOP;
        mFlContent.addView(mWebProgress, lp);
        mWebProgress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_left) {
            goBack();
        }
    }

    private void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mWebView.onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void loadStart() {
        mTvTitle.setText("");
        mWebProgress.show();
    }

    @Override
    public void loadFinish() {
        mWebProgress.hide();
    }

    @Override
    public void loadError() {
        mWebProgress.hide();
    }

    @Override
    public void loadProgress(int nowProgress) {
        mWebProgress.setProgress(nowProgress);
    }

    @Override
    public void loadTile(String title) {
        mTvTitle.setText(title);
    }

    @Override
    public void loadIcom(Bitmap icon) {
    }

    //上传完成
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadEvent event) {
        mWebView.notifyWebDownloadFileProgress(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mWebView.notifyWebFileSelect(requestCode, resultCode, data);
    }
}
