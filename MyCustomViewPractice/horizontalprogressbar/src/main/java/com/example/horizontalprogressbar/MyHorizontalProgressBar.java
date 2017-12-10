package com.example.horizontalprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * Created by Lenovo on 2017/11/22.
 */

public class MyHorizontalProgressBar extends ProgressBar {
    private int unreachedColor;
    private int reachedColor;
    private int progressHeight;
    private int textSize = (int) dpToPx(10);
    private int textColor;
    private Paint mPaint;
    private int mRealWidth;

    public MyHorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyHorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttributes(attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(textSize);  // 构造函数中使用的参数必须在定义的时候初始化
    }

    private void obtainStyledAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyHorizontalProgressBar);
        unreachedColor = a.getColor(R.styleable.MyHorizontalProgressBar_unreachedColor, 0xffffff);
        reachedColor = a.getColor(R.styleable.MyHorizontalProgressBar_reachedColor, 0xff0000);
        progressHeight = (int) a.getDimension(R.styleable.MyHorizontalProgressBar_progressHeight, dpToPx(3));
        textSize = (int) a.getDimension(R.styleable.MyHorizontalProgressBar_textSize, dpToPx(10));
        textColor = a.getColor(R.styleable.MyHorizontalProgressBar_textColor, 0xff0000);
    }

    private float dpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 要用wrap_content的维度进行逻辑的特殊处理，其余维度用measureSpec中得到的size
        if (heightMode != MeasureSpec.EXACTLY) {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            height = Math.max(textHeight, progressHeight) + getPaddingBottom() + getPaddingTop();
        }

        setMeasuredDimension(width, height);

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        int maxProgressWidth = (int) (mRealWidth - textWidth - dpToPx(10)); // 文字左右加上5dp的空白
        int reachedProgressEnd = (int) (getProgress() * 1.0f / getMax() * maxProgressWidth);
        int textHeight = (int) (-(mPaint.descent() + mPaint.ascent())/2);

        // 画reachedProgressBar
        if (reachedProgressEnd > 0) {
            mPaint.setColor(reachedColor);
            mPaint.setStrokeWidth(progressHeight);
            canvas.drawLine(0, 0, reachedProgressEnd, 0, mPaint);
        }

        // 画文本
        canvas.drawText(text, reachedProgressEnd + dpToPx(5), textHeight, mPaint);

        // 画unreachedProgressBar
        int unreachProgressStart = (int) (reachedProgressEnd + dpToPx(10) + textWidth);
        if (unreachProgressStart < mRealWidth) {
            mPaint.setColor(unreachedColor);
            mPaint.setStrokeWidth(progressHeight);
            canvas.drawLine(unreachProgressStart, 0, mRealWidth, 0, mPaint);
        }
    }
}
