package com.lirui.learn.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lirui.learndemo.R;
import com.lirui.lib_common.base.adapter.BaseQuickAdapter;
import com.lirui.lib_common.base.adapter.BaseViewHolder;
import com.lirui.lib_common.image.loader.LoaderManager;

import java.util.List;

/**
 * 相册布局
 */

public class PhotoGridAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public static final int DEFAULT_VIEW_TYPE = 0x00000000;
    public static final int ADD_VIEW_TYPE = 0x00000001;

    public int maxCount;

    public PhotoGridAdapter(@LayoutRes int layoutResId, @Nullable List<String> data, int maxCount) {
        super(layoutResId, data);
        this.maxCount = maxCount;
    }

    @Override
    protected int getDataCount() {
        int size = mData == null ? 0 : mData.size();
        if (size < maxCount) {
            size++;
        }
        return size;
    }

    @Override
    protected int getDefItemViewType(int position) {
        int size = mData == null ? 0 : mData.size();
        if (position < size) {
            return DEFAULT_VIEW_TYPE;
        } else {
            return ADD_VIEW_TYPE;
        }
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, getLayoutId(viewType));
    }

    private int getLayoutId(int viewType) {
        if (viewType == DEFAULT_VIEW_TYPE) {
            return R.layout.item_photo;
        } else {
            return R.layout.item_add_photo;
        }
    }

    @Override
    protected void convert(final int position, final BaseViewHolder helper) {
        if (getDefItemViewType(position) == DEFAULT_VIEW_TYPE) {
            LoaderManager.getLoader().loadPath((ImageView) helper.getView(R.id.iv_photo), mData.get(position));
        } else {
            helper.addOnClickListener(R.id.iv_add);
        }
    }


}
