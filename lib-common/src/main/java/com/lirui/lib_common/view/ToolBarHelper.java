package com.lirui.lib_common.view;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lirui.lib_common.R;

/**
 * ToolBar帮助工具类
 * 仅支持最多左边一个图标，标题，右边两个图标，两个文字图标
 */

public class ToolBarHelper {
    private final AppCompatActivity activity;

    private int leftIcon = R.mipmap.ic_back;
    private String title = "";
    private int rightIcon1 = -1;
    private int rightIcon2 = -1;
    private String rightText1 = "";
    private String rightText2 = "";
    private View.OnClickListener leftIconClickListener;
    private View.OnClickListener rightIcon1ClickListener;
    private View.OnClickListener rightIcon2ClickListener;
    private View.OnClickListener rightText1ClickListener;
    private View.OnClickListener rightText2ClickListener;

    private Toolbar mToolbar;
    private ImageView mIvLeft;
    private TextView mTvTitle;
    private ImageView mIvRight2;
    private ImageView mIvRight1;
    private TextView mTvRight2;
    private TextView mTvRight1;

    private ToolBarHelper(AppCompatActivity activity, int leftIcon, String title, int rightIcon1
            , int rightIcon2, String rightText1, String rightText2, View.OnClickListener leftIconClickListener
            , View.OnClickListener rightIcon1ClickListener, View.OnClickListener rightIcon2ClickListener
            , View.OnClickListener rightText1ClickListener, View.OnClickListener rightText2ClickListener) {
        this.activity = activity;
        this.leftIcon = leftIcon;
        this.title = title;
        this.rightIcon1 = rightIcon1;
        this.rightIcon2 = rightIcon2;
        this.rightText1 = rightText1;
        this.rightText2 = rightText2;
        this.leftIconClickListener = leftIconClickListener;
        this.rightIcon1ClickListener = rightIcon1ClickListener;
        this.rightIcon2ClickListener = rightIcon2ClickListener;
        this.rightText1ClickListener = rightText1ClickListener;
        this.rightText2ClickListener = rightText2ClickListener;
        initView();
        initDataAndListener();
    }


    /**
     * 初始化View
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar == null) {
            return;
        }

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        mIvLeft = (ImageView) activity.findViewById(R.id.iv_left);
        mTvTitle = (TextView) activity.findViewById(R.id.tv_title);
        mIvRight2 = (ImageView) activity.findViewById(R.id.iv_right_2);
        mIvRight1 = (ImageView) activity.findViewById(R.id.iv_right_1);
        mTvRight2 = (TextView) activity.findViewById(R.id.tv_right_2);
        mTvRight1 = (TextView) activity.findViewById(R.id.tv_right_1);
    }

    /**
     * 初始化数据和监听
     */
    private void initDataAndListener() {
        mIvLeft.setImageResource(leftIcon);
        if (leftIconClickListener == null && leftIcon == R.mipmap.ic_back) {
            mIvLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }
        if (leftIconClickListener != null) {
            mIvLeft.setOnClickListener(leftIconClickListener);
        }

        if (TextUtils.isEmpty(title)) {
            mTvTitle.setVisibility(View.GONE);
        } else {
            mTvTitle.setVisibility(View.VISIBLE);
            mTvTitle.setText(title);
        }

        if (rightIcon1 <= 0) {
            mIvRight1.setVisibility(View.GONE);
        } else {
            mIvRight1.setVisibility(View.VISIBLE);
            mIvRight1.setImageResource(rightIcon1);
            if (rightIcon1ClickListener != null) {
                mIvRight1.setOnClickListener(rightIcon1ClickListener);
            }
        }

        if (rightIcon2 <= 0) {
            mIvRight2.setVisibility(View.GONE);
        } else {
            mIvRight2.setVisibility(View.VISIBLE);
            mIvRight2.setImageResource(rightIcon2);
            if (rightIcon2ClickListener != null) {
                mIvRight2.setOnClickListener(rightIcon2ClickListener);
            }
        }

        if (TextUtils.isEmpty(rightText1)) {
            mTvRight1.setVisibility(View.GONE);
        } else {
            mTvRight1.setVisibility(View.VISIBLE);
            mTvRight1.setText(rightText1);
            if (rightText1ClickListener != null) {
                mTvRight1.setOnClickListener(rightText1ClickListener);
            }
        }

        if (TextUtils.isEmpty(rightText2)) {
            mTvRight2.setVisibility(View.GONE);
        } else {
            mTvRight2.setVisibility(View.VISIBLE);
            mTvRight2.setText(rightText2);
            if (rightText2ClickListener != null) {
                mTvRight2.setOnClickListener(rightText2ClickListener);
            }
        }
    }

    /**
     * 根据Id获取控件
     */
    public View getViewById(int id) {
        return activity.findViewById(id);
    }

    /**
     * 构造类
     */
    public static class Builder {
        private final AppCompatActivity activity;
        private int leftIcon = R.mipmap.ic_back;
        private String title = "";
        private int rightIcon1 = -1;
        private int rightIcon2 = -1;
        private String rightText1 = "";
        private String rightText2 = "";
        private View.OnClickListener leftIconClickListener;
        private View.OnClickListener rightIcon1ClickListener;
        private View.OnClickListener rightIcon2ClickListener;
        private View.OnClickListener rightText1ClickListener;
        private View.OnClickListener rightText2ClickListener;

        public Builder(AppCompatActivity activity) {
            this.activity = activity;
        }

        public Builder setLeftIcon(int leftIcon, View.OnClickListener leftIconClickListener) {
            this.leftIcon = leftIcon;
            this.leftIconClickListener = leftIconClickListener;
            return this;
        }
        public Builder setLeftIcon(View.OnClickListener leftIconClickListener) {
            this.leftIconClickListener = leftIconClickListener;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setRightIcon1(int rightIcon1, View.OnClickListener rightIcon1ClickListener) {
            this.rightIcon1 = rightIcon1;
            this.rightIcon1ClickListener = rightIcon1ClickListener;
            return this;
        }

        public Builder setRightIcon2(int rightIcon2, View.OnClickListener rightIcon2ClickListener) {
            this.rightIcon2 = rightIcon2;
            this.rightIcon2ClickListener = rightIcon2ClickListener;
            return this;
        }

        public Builder setRightText1(String rightText1, View.OnClickListener rightText1ClickListener) {
            this.rightText1 = rightText1;
            this.rightText1ClickListener = rightText1ClickListener;
            return this;
        }

        public Builder setRightText2(String rightText2, View.OnClickListener rightText2ClickListener) {
            this.rightText2 = rightText2;
            this.rightText2ClickListener = rightText2ClickListener;
            return this;
        }

        public ToolBarHelper builder() {
            return new ToolBarHelper(activity, leftIcon, title, rightIcon1
                    , rightIcon2, rightText1, rightText2, leftIconClickListener
                    , rightIcon1ClickListener, rightIcon2ClickListener
                    , rightText1ClickListener, rightText2ClickListener);
        }
    }
}
