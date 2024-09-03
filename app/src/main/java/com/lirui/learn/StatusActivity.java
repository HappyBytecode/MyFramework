package com.lirui.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lirui.learndemo.R;
import com.lirui.lib_common.base.AbsBaseActivity;
import com.lirui.lib_common.util.ToastUtils;
import com.lirui.lib_common.view.StateView;
import com.lirui.lib_common.view.ToolBarHelper;

import java.util.Random;

public class StatusActivity extends AbsBaseActivity implements View.OnClickListener {
    private StateView mStateView;
    private int position = 0;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_status;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mStateView = StateView.inject(this, true);
        new ToolBarHelper.Builder(this).setRightText2("切换", this).builder();
        mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                ToastUtils.info(StatusActivity.this, "重试").show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        position++;
        position = position % 4;
        if (position == 0) {
            mStateView.showContent();
        } else if (position == 1) {
            mStateView.showEmpty();
        } else if (position == 2) {
            mStateView.showLoading();
        } else if (position == 3) {
            mStateView.showRetry();
        }
    }
}
