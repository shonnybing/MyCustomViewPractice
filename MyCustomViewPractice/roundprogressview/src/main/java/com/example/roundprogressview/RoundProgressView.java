package com.example.roundprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by Lenovo on 2017/11/22.
 */

public class RoundProgressView extends ProgressBar {
    private int reachedCircleSize;
    private int reachedCircleColor;
    private int unReachedCircleSize;
    private int unReachedCircleColor;
    private int textColor;
    private int textSize;
    private int radius;
    private Paint mPaint;

    public RoundProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttributes(attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RoundProgressView);
        reachedCircleColor = array.getColor(R.styleable.RoundProgressView_reachedCircleColor, 0x00ff00);
        reachedCircleSize = (int) array.getDimension(R.styleable.RoundProgressView_reachedCircleSize, dpTopx(15));
        unReachedCircleColor = array.getColor(R.styleable.RoundProgressView_unReachedCircleColor, 0x000000);
        unReachedCircleSize = (int) array.getDimension(R.styleable.RoundProgressView_unReachedCircleSize, dpTopx(5));
        textSize = (int) array.getDimension(R.styleable.RoundProgressView_textSize, dpTopx(20));
        textColor = array.getColor(R.styleable.RoundProgressView_textColor, 0x00ff00);
        radius = (int) array.getDimension(R.styleable.RoundProgressView_radius, dpTopx(50));
    }

    private int dpTopx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            width = getPaddingLeft() + getPaddingRight() + 2 * radius + Math.max(unReachedCircleSize, reachedCircleSize);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height = getPaddingTop() + getPaddingBottom() + 2*radius + Math.max(unReachedCircleSize, reachedCircleSize);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        int lineWidth = Math.max(unReachedCircleSize, reachedCircleSize);
        // 画reachedPart
        mPaint.setStrokeWidth(reachedCircleSize);
        mPaint.setColor(reachedCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        int sweepAngle = (int) (getProgress() * 1.0f / getMax() * 360);
        canvas.drawArc(new RectF(getWidth()/2-radius, getWidth()/2-radius, getWidth()/2+radius, getWidth()/2+radius), 0, sweepAngle, false, mPaint);

        // 画unReachedPart
        mPaint.setStrokeWidth(unReachedCircleSize);
        mPaint.setColor(unReachedCircleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(getWidth()/2-radius, getWidth()/2-radius, getWidth()/2+radius, getWidth()/2+radius), sweepAngle, 360-sweepAngle, false, mPaint);

        // 画字
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        String text = getProgress() + "%";
        Rect bound = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), bound);
        int textWidth = bound.width();
        int textHeight = bound.height();
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, getWidth()/2 - textWidth/2, getWidth()/2 + textHeight/2, mPaint);
        canvas.restore();
    }
}
