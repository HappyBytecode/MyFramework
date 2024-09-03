package com.lirui.lib_common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 基类Fragment
 */
public abstract class AbsBaseFragment extends Fragment {

    protected static String TAG = null;

    protected Context mContext;
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        loadData();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutView() != null) {
            return getLayoutView();
        }
        if (getLayoutId() != 0) {
            return inflater.inflate(getLayoutId(), container, false);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view, savedInstanceState);
    }

    /**
     * Fragment 布局 id
     */
    protected abstract int getLayoutId();

    /**
     * Fragment 布局
     */
    public View getLayoutView() {
        return null;
    }

    /**
     * 初始化View
     */
    protected abstract void initViews(View rootView, Bundle savedInstanceState);

    /**
     * 加载数据
     */
    protected abstract void loadData();
}

