package com.lirui.lib_common.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lirui.lib_common.util.SizeUtils;

/**
 * 作者：lirui on 2017/2/7 16:00
 * 邮箱：liruilirui89@126.com
 * RecyclerView分割线
 * 1.onDraw及onDrawOver绘制四周
 */
public class ItemDivider extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    private final int dividerWith;
    private final float offSet;

    public ItemDivider(int dividerWith, int color) {
        mPaint = new Paint();
        this.dividerWith = SizeUtils.dp2px(dividerWith);
        offSet = (int) Math.ceil(this.dividerWith * 1f / 2);
        mPaint.setColor(color);
    }

    /**
     * 指定item之间的间距(就是指定分割线的宽度) 回调顺序 1
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemCount = state.getItemCount();
        int position = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            int spanIndex = spanSizeLookup.getSpanIndex(position, spanCount);
            int spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount);
            int lastSpanGroupIndex = spanSizeLookup.getSpanGroupIndex(itemCount - 1, spanCount);

            outRect.left = spanIndex * dividerWith / spanCount;//0,1/3,2/3
            outRect.right = dividerWith - (spanIndex + 1) * dividerWith / spanCount;//2/3,1/3,0
            outRect.bottom = dividerWith;
            if (lastSpanGroupIndex == spanGroupIndex) {
                outRect.bottom = 0;
            }
            return;
        } else if (layoutManager instanceof LinearLayoutManager) {
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                // 水平分割线将绘制在item底部
                outRect.bottom = dividerWith;
                if (position == itemCount) outRect.bottom = 0;
            } else if (orientation == LinearLayoutManager.HORIZONTAL) { // 垂直分割线将绘制在item右侧
                outRect.right = dividerWith;
                if (position == itemCount) outRect.bottom = 0;
            }
            return;
        }
        throw new IllegalArgumentException("It is not currently supported StaggeredGridLayoutManager");
    }

    /**
     * 在item 绘制之前调用(就是绘制在 item 的底层) 回调顺序 2
     * 一般分割线在这里绘制
     * 看到canvas,对自定义控件有一定了解的话,就能想到为什么说给RecyclerView设置分割线更灵活了
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            drawLeftDivider(c, child);
            drawBottomDivider(c, child);
        }
    }

    /**
     * 在item 绘制之后调用(就是绘制在 item 的上层) 回调顺序 3
     * 也可以在这里绘制分割线,和上面的方法 二选一
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    private void drawBottomDivider(Canvas c, View child) {
        c.drawRect(
                child.getLeft() - offSet,
                child.getBottom(),
                child.getRight() + offSet,
                child.getBottom() + dividerWith,
                mPaint
        );
    }

    private void drawLeftDivider(Canvas c, View child) {
        c.drawRect(
                child.getLeft() - dividerWith,
                child.getTop() - offSet,
                child.getLeft(),
                child.getBottom() + offSet,
                mPaint
        );
    }
}
