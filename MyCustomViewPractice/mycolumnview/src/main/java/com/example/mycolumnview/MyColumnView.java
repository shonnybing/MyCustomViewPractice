package com.example.mycolumnview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by Lenovo on 2017/11/23.
 */

public class MyColumnView extends View {
    private int columnNum;
    private int defaultColumnNum;
    private int gapWidth;
    private int radius;
    private Bitmap bitmap;
    private int reachedColumnColor;
    private int unReachedColumnColor;
    private int strokeWidth;
    private Paint mPaint;
    private int currentColumnNum;
    private int mTouchSlop;
    private int xDown;
    private int yDown;
    private int xUp;
    private int yUp;

    private final int START_ANGLE = 135;
    private final int SWEEP_ANGLE = 270;

    public MyColumnView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyColumnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttributes(attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private void obtainStyleAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyColumnView);
        columnNum = a.getInt(R.styleable.MyColumnView_columnNum, 6);
        defaultColumnNum = a.getInt(R.styleable.MyColumnView_defaultColumnNum, 3);
        currentColumnNum = defaultColumnNum;
        gapWidth = a.getInt(R.styleable.MyColumnView_gapWidth, dpTopx(3));
        radius = (int) a.getDimension(R.styleable.MyColumnView_radius, dpTopx(100));
        int imageRes = a.getResourceId(R.styleable.MyColumnView_image, 0);
        bitmap = BitmapFactory.decodeResource(getResources(), imageRes);
        unReachedColumnColor = a.getColor(R.styleable.MyColumnView_unReachedColumnColor, 0x000000);
        reachedColumnColor = a.getColor(R.styleable.MyColumnView_reachedColumnColor, 0xcccccc);
        strokeWidth = (int) a.getDimension(R.styleable.MyColumnView_strokeWidth, dpTopx(10));
    }

    private int dpTopx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            width = getPaddingLeft() + getPaddingRight() + 2* radius + strokeWidth;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            height = getPaddingTop() + getPaddingBottom() + 2*radius + strokeWidth;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (columnNum <= 1) {
            return;
        }

        // 画reachedPart
        int columnWidth = (SWEEP_ANGLE - (columnNum - 1) * gapWidth) / columnNum;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(reachedColumnColor);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        for (int i = 0; i < currentColumnNum; i++) {
            canvas.drawArc(new RectF(strokeWidth / 2, strokeWidth / 2, strokeWidth / 2 + 2 * radius, strokeWidth / 2 + 2 * radius),
                    i*(columnWidth+gapWidth)+START_ANGLE, columnWidth, false, mPaint);
        }

        // 画unReachedPart
        mPaint.setColor(unReachedColumnColor);
        for (int i = currentColumnNum; i < columnNum; i++) {
            canvas.drawArc(new RectF(strokeWidth / 2, strokeWidth / 2, strokeWidth / 2 + 2 * radius, strokeWidth / 2 + 2 * radius),
                    i*(columnWidth+gapWidth)+START_ANGLE, columnWidth, false, mPaint);
        }

        // 画bitmap
        int squareWidth = (int) (Math.sqrt(2)*(radius- columnWidth/2));
        int realWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        RectF rectF = new RectF((float) (realWidth/2- Math.sqrt(2)*1.0f*radius/4),(float)(realWidth/2- Math.sqrt(2)*1.0f*radius/4),
                (float)(realWidth/2+ Math.sqrt(2)*1.0f*radius/4),(float)(realWidth/2+Math.sqrt(2)*1.0f*radius/4));
        if (bitmap.getWidth() < squareWidth && bitmap.getHeight() < squareWidth) {
            rectF.left = realWidth/2 - bitmap.getWidth()/2;
            rectF.top = realWidth/2 - bitmap.getHeight()/2;
            rectF.right = realWidth/2 + bitmap.getWidth()/2;
            rectF.bottom = realWidth/2 + bitmap.getHeight()/2;
        }
        canvas.drawBitmap(bitmap, null, rectF, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = currentX;
                yDown = currentY;
                break;
            case MotionEvent.ACTION_UP:
                xUp = currentX;
                yUp = currentY;
                if (yUp > yDown && Math.abs(yDown-yUp) > mTouchSlop) {
                    down();
                }
                if (yUp < yDown && Math.abs(yDown-yUp) > mTouchSlop) {
                    up();
                }
                break;
        }
        return true;
    }

    private void down() {
        if (currentColumnNum > 0) {
            currentColumnNum--;
            invalidate();
        }
    }

    private void up() {
        if (currentColumnNum < columnNum) {
            currentColumnNum++;
            invalidate();
        }
    }
}
