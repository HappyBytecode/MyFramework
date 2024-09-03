package com.lirui.learn;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.lirui.learndemo.R;
import com.lirui.lib_common.base.adapter.BaseQuickAdapter;
import com.lirui.lib_common.base.adapter.BaseViewHolder;

import java.util.List;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/09/05
 *      desc    :
 *      version : 1.0
 *  </pre>
 */

public class TestAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public TestAdapter(@LayoutRes int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(int position, BaseViewHolder helper) {
        ((TextView) helper.getView(R.id.tv_content)).setText(mData.get(position));
    }
}
