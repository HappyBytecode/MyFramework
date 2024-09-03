package com.lirui.lib_common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.lirui.lib_common.R;
import com.lirui.lib_common.util.SizeUtils;

/**
 * 圆形加载进度
 */

public class CircleProgress extends View {

    private static final Interpolator MATERIAL_INTERPOLATOR = new FastOutSlowInInterpolator();

    private Paint outerCirclePaint;//外部圆形
    private RectF outerCircleRect = new RectF();
    private float outerCircleWidth;
    private int outerCircleColor;

    private Paint innerSectorPaint;//内部扇形
    private RectF innerSectorRect = new RectF();
    private int innerSectorColor;

    private int progress = 0;
    private float progressAngle = 0;
    private float startAngle = 0;//绘制圆弧的起始角度
    float index = 0;//计算差值的0~1
    private float step = 9;//起始角度的步进值

    private float outerCircleRadius;//半径

    private final int default_innerSectorColor = Color.argb(0xaa, 0xe0, 0xe0, 0xe0);
    private final int default_outerCircleColor = Color.argb(0xaa, 0xff, 0x40, 0x80);
    private final int default_stroke_width = SizeUtils.dp2px(4);
    private int min_size = (int) SizeUtils.dp2px(100);

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_OUTER_CIRCLE_WIDTH = "outer_circle_width";
    private static final String INSTANCE_OUTER_CIRCLE_COLOR = "outer_circle_color";
    private static final String INSTANCE_INNER_SECTOR_COLOR = "inner_sector_color";
    private static final String INSTANCE_PROGRESS = "progress";

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initPainters() {

        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(outerCircleColor);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setAntiAlias(true);
        outerCirclePaint.setStrokeWidth(outerCircleWidth);
        outerCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        innerSectorPaint = new Paint();
        innerSectorPaint.setColor(innerSectorColor);
        innerSectorPaint.setStyle(Paint.Style.FILL);
        innerSectorPaint.setAntiAlias(true);

    }

    protected void initByAttributes(TypedArray attributes) {
        outerCircleColor = attributes.getColor(R.styleable.CircleProgress_outerCircleColor, default_outerCircleColor);
        outerCircleWidth = attributes.getDimension(R.styleable.CircleProgress_outerCircleStrokeWidth, default_stroke_width);

        innerSectorColor = attributes.getColor(R.styleable.CircleProgress_innerSectorColor, default_innerSectorColor);

        progress = attributes.getInt(R.styleable.CircleProgress_progress, 0);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        outerCircleRadius = (getWidth() - outerCircleWidth) / 2f;
        float interval = outerCircleWidth;
        outerCircleRect.set(outerCircleWidth,
                outerCircleWidth,
                getWidth() - outerCircleWidth,
                getHeight() - outerCircleWidth);
        innerSectorRect.set(outerCircleWidth + interval
                , outerCircleWidth + interval
                , getWidth() - outerCircleWidth - interval,
                getHeight() - outerCircleWidth - interval);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, outerCircleRadius, outerCirclePaint);
        canvas.drawArc(outerCircleRect, startAngle, (float) 0.5, false, outerCirclePaint);
        canvas.drawArc(innerSectorRect, 0, progressAngle, true, innerSectorPaint);
        updateView();
    }

    private void updateView() {
        if (getVisibility() == VISIBLE) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAngle = startAngle + step + MATERIAL_INTERPOLATOR.getInterpolation(index) * step;
                    index = (float) ((index + 0.05) % 1);
                    startAngle = startAngle % 360;
                    invalidate();
                }
            }, 100);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(INSTANCE_OUTER_CIRCLE_WIDTH, getOuterCircleWidth());
        bundle.putInt(INSTANCE_OUTER_CIRCLE_COLOR, getOuterCircleColor());
        bundle.putInt(INSTANCE_INNER_SECTOR_COLOR, getInnerSectorColor());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            outerCircleWidth = bundle.getFloat(INSTANCE_OUTER_CIRCLE_WIDTH);
            outerCircleColor = bundle.getInt(INSTANCE_OUTER_CIRCLE_COLOR);
            innerSectorColor = bundle.getInt(INSTANCE_INNER_SECTOR_COLOR);
            initPainters();
            setProgress(bundle.getInt(INSTANCE_PROGRESS));

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public float getOuterCircleWidth() {
        return outerCircleWidth;
    }

    public void setOuterCircleWidth(float outerCircleWidth) {
        this.outerCircleWidth = outerCircleWidth;
        initPainters();
    }

    public int getOuterCircleColor() {
        return outerCircleColor;
    }

    public void setOuterCircleColor(int outerCircleColor) {
        this.outerCircleColor = outerCircleColor;
        initPainters();
    }

    public int getInnerSectorColor() {
        return innerSectorColor;
    }

    public void setInnerSectorColor(int innerSectorColor) {
        this.innerSectorColor = innerSectorColor;
        initPainters();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        progressAngle = (float) (progress * (360.0 / 100));
    }
}

