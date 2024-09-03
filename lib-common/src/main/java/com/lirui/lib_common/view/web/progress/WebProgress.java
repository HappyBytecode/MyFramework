package com.lirui.lib_common.view.web.progress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.lirui.lib_common.R;
import com.lirui.lib_common.util.SizeUtils;

/**
 * <pre>
 *      author  : lirui
 *      QQ      : 1735613836
 *      time    : 2017/08/24
 *      desc    : 网页加载进度条
 *      version : 1.0
 *  </pre>
 */

public class WebProgress extends BaseWebProgress {

    private int mColor;
    private Paint mPaint;
    private ValueAnimator mValueAnimator;
    private int targetWidth = 0;

    private int TAG = 0;
    public static final int UN_START = 0;
    public static final int STARTED = 1;
    public static final int FINISH = 2;

    private float targetProgress = 0f;
    private float currentProgress = 0f;

    public WebProgress(Context context) {
        this(context, null);
    }

    public WebProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mPaint = new Paint();
        mColor = getResources().getColor(R.color.colorPrimaryDark);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        targetWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    public void setColor(String color) {
        this.setColor(Color.parseColor(color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int w = View.MeasureSpec.getSize(widthMeasureSpec);

        int hMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int h = View.MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == View.MeasureSpec.AT_MOST) {
            w = w <= getContext().getResources().getDisplayMetrics().widthPixels ? w : getContext().getResources().getDisplayMetrics().widthPixels;
        }
        if (hMode == View.MeasureSpec.AT_MOST) {
            h = SizeUtils.dp2px(2);
        }
        this.setMeasuredDimension(w, h);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRect(0, 0, currentProgress / 100 * Float.valueOf(this.getWidth()), this.getHeight(), mPaint);
    }

    @Override
    public void show() {
        if (getVisibility() == View.GONE) {
            this.setVisibility(View.VISIBLE);
            currentProgress = 0f;
            startAnim(-1, true);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.targetWidth = getMeasuredWidth();
    }

    public void setProgress(float progress) {
        if (progress == 0) {
            reset();
        } else if (progress > 90) {
            startAnim(progress, false);
        }
        if (progress > 95) {
            hide();
        }
    }

    private float weightDuration(float value, float current) {
        if (value > 70 && value < 85) {
            return 1.5f;
        } else if (value > 85) {
            return 0.8f;
        }
        return small(value, current);
    }

    private float small(float value, float current) {
        float poor = Math.abs(value - current);
        if (poor < 25) {
            return 4f;
        } else if (poor > 25 && poor < 50) {
            return 3f;
        } else {
            return 2f;
        }
    }

    private void startAnim(float value, boolean isAuto) {
        if (targetProgress == value)
            return;
        if (value < currentProgress && value != -1)
            return;

        float v = (isAuto) ? 90f : value;
        if (mValueAnimator != null && mValueAnimator.isStarted()) {
            mValueAnimator.cancel();
        }
        currentProgress = currentProgress == 0f ? 0.00000001f : currentProgress;
        mValueAnimator = ValueAnimator.ofFloat(currentProgress, v);
        mValueAnimator.setInterpolator(new LinearInterpolator());

        long duration = (long) Math.abs((v / 100f * targetWidth) - (currentProgress / 100f * targetWidth));


        /*默认每个像素8毫秒*/
        mValueAnimator.setDuration(isAuto ? duration * 4 : (long) (duration * weightDuration(v, currentProgress)));
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
        mValueAnimator.addListener(mAnimatorListenerAdapter);
        mValueAnimator.start();
        TAG = STARTED;
        targetProgress = v;
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float t = (float) animation.getAnimatedValue();
            WebProgress.this.currentProgress = t;
            WebProgress.this.invalidate();
        }
    };

    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            doEnd();
        }
    };

    private void doEnd() {
        if (TAG == FINISH && currentProgress == 100f) {
            setVisibility(GONE);
            currentProgress = 0f;
        }
        TAG = UN_START;
    }

    @Override
    public void hide() {
        TAG = FINISH;
    }

    @Override
    public void reset() {
        currentProgress = 0;
        if (mValueAnimator != null && mValueAnimator.isStarted())
            mValueAnimator.cancel();
    }

    @Override
    public void setProgress(int newProgress) {
        setProgress(Float.valueOf(newProgress));
    }
}
