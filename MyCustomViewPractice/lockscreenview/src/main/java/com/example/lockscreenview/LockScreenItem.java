package com.example.lockscreenview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 锁屏Item
 * Created by bfshao on 2017/12/6.
 */

public class LockScreenItem extends View {
    private Paint mPaint;
    private int strikeWidth = 2;
    private int innerRoundRadius;
    private int outerRoundRadius;
    private int noFingerOuterColor;
    private int noFingerInnerColor;
    private int fingerOnColor;
    private int fingerUpColor;
    private Path arrowPath;
    private int centerX;
    private int centerY;
    private int arrowLength;

    enum State {
        STATE_NO_FINGER, STATE_FINGER_ON, STATE_FINGER_UP
    }

    private State mCurrentState = State.STATE_NO_FINGER;
    private int mArrowDegree = -1;

    public LockScreenItem(Context context, int noFingerOuterColor, int noFingerInnerColor, int fingerOnColor, int fingerUpColor) {
        super(context);
        this.noFingerOuterColor = noFingerOuterColor;
        this.noFingerInnerColor = noFingerInnerColor;
        this.fingerOnColor = fingerOnColor;
        this.fingerUpColor = fingerUpColor;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 外层ViewGroup调用时指定了宽高的具体值，且宽和高需设置为一样，因此这里不需要对wrap_content的情况做特殊处理
        int width = MeasureSpec.getSize(widthMeasureSpec);
        outerRoundRadius = width/2 - strikeWidth/2;
        innerRoundRadius = (int) (width/2*0.3);

        centerX = centerY = width/2;
        arrowLength = (int) (0.333*centerX);

        arrowPath = new Path();
        arrowPath.moveTo(centerX, strikeWidth + 2);
        arrowPath.lineTo(centerX - arrowLength, strikeWidth + 2 + arrowLength);
        arrowPath.lineTo(centerX + arrowLength, strikeWidth + 2 +arrowLength);
        arrowPath.close();
        arrowPath.setFillType(Path.FillType.WINDING);  // 从图形内的点，向任意方向，射出一条射线，穿过一次顺时针区域+1，逆时针区域-1，最后为0表示在区域外，为正数表示在区域内
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mCurrentState) {
            case STATE_NO_FINGER:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(noFingerOuterColor);
                canvas.drawCircle(centerX, centerY, outerRoundRadius, mPaint);
                mPaint.setColor(noFingerInnerColor);
                canvas.drawCircle(centerX, centerY, innerRoundRadius, mPaint);
                break;
            case STATE_FINGER_ON:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(fingerOnColor);
                canvas.drawCircle(centerX, centerY, innerRoundRadius, mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(centerX, centerY, outerRoundRadius, mPaint);
                break;
            case STATE_FINGER_UP:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(fingerUpColor);
                canvas.drawCircle(centerX, centerY, innerRoundRadius, mPaint);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(centerX, centerY, outerRoundRadius, mPaint);

                drawArrow(canvas);
                break;
        }
    }

    public void setmArrowDegree(int mArrowDegree) {
        this.mArrowDegree = mArrowDegree;
    }

    public void setCurrentState(State state) {
        this.mCurrentState = state;
        invalidate();
    }

    private void drawArrow(Canvas canvas) {
        if (mArrowDegree != -1) {
            // 保存当前坐标系
            canvas.save();

            mPaint.setStyle(Paint.Style.FILL);
            // 将坐标系旋转指定角度
            canvas.rotate(mArrowDegree, centerX, centerY);
            canvas.drawPath(arrowPath, mPaint);

            // 恢复当前坐标系
            canvas.restore();
        }
    }
}
