package com.lirui.lib_common.view.web.progress;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/24
 *      desc    : 网页加载进度条
 *      version : 1.0
 *  </pre>
 */

public class BaseWebProgress extends FrameLayout implements OnWebProgressListener {



    public BaseWebProgress(Context context) {
        this(context, null);
    }

    public BaseWebProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseWebProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void reset() {


    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void setProgress(int newProgress) {

    }
}
