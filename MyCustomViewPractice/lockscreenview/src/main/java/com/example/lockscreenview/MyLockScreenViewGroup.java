package com.example.lockscreenview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by bfshao on 2017/12/7.
 */

public class MyLockScreenViewGroup extends RelativeLayout {
    private int noFingerInnerCircleColor;
    private int noFingerOutterCircleColor;
    private int fingerOnColor;
    private int fingerUpColor;
    private int lockScreenViewCount;
    private final int tryTimes;
    private int changeTryTimes;

    private LockScreenItem[] lockScreenViews;
    private RelativeLayout.LayoutParams params;

    private ArrayList<Integer> choosedItems;
    private Path mPath;

    // 连接线的当前端点
    private int itemCenterX;
    private int itemCenterY;

    // 悬空端点
    private Point skyPoint;

    private int[] answers = {1,2,3};

    private Paint mPaint;
    private OnGestureLockViewListener onGestureLockViewListener;

    public MyLockScreenViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLockScreenViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyLockScreenViewGroup);
        noFingerInnerCircleColor = a.getColor(R.styleable.MyLockScreenViewGroup_NoFinger_InnerCircle_Color, 0x00ff00);
        noFingerOutterCircleColor = a.getColor(R.styleable.MyLockScreenViewGroup_NoFinger_OutterCircle_Color, 0xff0000);
        fingerOnColor = a.getColor(R.styleable.MyLockScreenViewGroup_FingerOn_Color, 0x0000ff);
        fingerUpColor = a.getColor(R.styleable.MyLockScreenViewGroup_FingerUp_Color, 0x000000);
        lockScreenViewCount = a.getInt(R.styleable.MyLockScreenViewGroup_LockScreenView_Count, 4);
        tryTimes = a.getInt(R.styleable.MyLockScreenViewGroup_TryTimes, 4);
        changeTryTimes = tryTimes;
        a.recycle();

        choosedItems = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
        skyPoint = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 这里不做wrap_content处理，布局中只赋确定值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // viewGroup的宽度取长和宽中的较小值
        int viewGroupWidth = width < height ? width : height;


        if (lockScreenViews == null) {
            lockScreenViews = new LockScreenItem[lockScreenViewCount * lockScreenViewCount];

            // 假定LockScreenView之间的间隙为0.25*LockScreenViewWidth
            int lockScreenViewWidth = 4 * viewGroupWidth / (5 * lockScreenViewCount + 1);
            int margin = (int) (lockScreenViewWidth * 0.25);

            mPaint.setStrokeWidth((float) (lockScreenViewWidth * 0.3 * 0.8));
            for (int i = 0; i < lockScreenViewCount*lockScreenViewCount; i++) {
                lockScreenViews[i] = new LockScreenItem(getContext(), noFingerOutterCircleColor,
                        noFingerInnerCircleColor, fingerOnColor, fingerUpColor);
                lockScreenViews[i].setId(i + 1);  // 注意id需要为正数

                params = new RelativeLayout.LayoutParams(lockScreenViewWidth, lockScreenViewWidth);

                // 非第一行的item，设置below
                if (i > lockScreenViewCount -1) {
                    params.addRule(RelativeLayout.BELOW, lockScreenViews[i - lockScreenViewCount].getId());
                }

                // 非第一列的item, 设置rightOf
                if (i % lockScreenViewCount != 0) {
                    params.addRule(RelativeLayout.RIGHT_OF, lockScreenViews[i - 1].getId());
                }

                // 为item设置margin,间隙为0.25*LockScreenViewWidth

                int leftMargin = margin;
                int topMargin = margin;
                int rightMargin = 0;
                int bottomMargin = 0;

                if (i < lockScreenViewCount*lockScreenViewCount -lockScreenViewCount) {
                    params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                } else {
                    bottomMargin = margin;
                    params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                }
                lockScreenViews[i].setCurrentState(LockScreenItem.State.STATE_NO_FINGER);
                addView(lockScreenViews[i], params);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                resetStates();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(fingerOnColor);
                mPaint.setAlpha(50);
                LockScreenItem child = getChildView(x, y);
                if (child != null) {
                    int id = child.getId();
                    // 如果不包含才要进行添加，否则说明已经添加过了
                    if (!choosedItems.contains(id)) {
                        choosedItems.add(id);
                        child.setCurrentState(LockScreenItem.State.STATE_FINGER_ON);

                        itemCenterX = (child.getLeft() + child.getRight()) / 2;
                        itemCenterY = (child.getTop() + child.getBottom()) / 2;

                        if (choosedItems.size() == 1) {
                            mPath.moveTo(itemCenterX, itemCenterY);
                        } else {
                            mPath.lineTo(itemCenterX, itemCenterY);
                        }
                    }
                }
                skyPoint.x = x;
                skyPoint.y = y;
                break;
            case MotionEvent.ACTION_UP:
                mPaint.setColor(fingerUpColor);
                mPaint.setAlpha(50);
                skyPoint.x = itemCenterX;
                skyPoint.y = itemCenterY;

                if (choosedItems.size() == 0) {
                    break;
                }

                if (isRightGesture()) {
                    changeTryTimes = tryTimes;
                    onGestureLockViewListener.gestureResult(true);
                } else {
                    changeTryTimes--;
                    // 连续错误次数超过限制次数
                    if (changeTryTimes == 0) {
                        onGestureLockViewListener.exceedMaxTryTime(tryTimes);
                    } else {
                        onGestureLockViewListener.gestureResult(false);
                    }
                }

                // 设置箭头旋转角度
                for (int i = 0; i < choosedItems.size() -1; i++) {
                    LockScreenItem currentChild = (LockScreenItem) findViewById(choosedItems.get(i));
                    LockScreenItem nextChild = (LockScreenItem) findViewById(choosedItems.get(i + 1));

                    int dx = nextChild.getLeft() - currentChild.getLeft();
                    int dy = nextChild.getTop() - currentChild.getTop();

                    int angle = (int) (Math.toDegrees(Math.atan2(dy, dx)) + 90);
                    currentChild.setmArrowDegree(angle);
                }

                // 设置LockItemView的状态
                for (int i = 0; i < choosedItems.size(); i++) {
                    LockScreenItem item = (LockScreenItem) findViewById(choosedItems.get(i));
                    item.setCurrentState(LockScreenItem.State.STATE_FINGER_UP);
                }
                break;
        }
        invalidate();
        return true;
    }

    private boolean isRightGesture() {
        if (answers.length != choosedItems.size()) {
            return false;
        }
        for (int i = 0; i < choosedItems.size(); i++) {
            if (choosedItems.get(i) != answers[i]) {
                return false;
            }
        }
        return true;
    }

    public interface OnGestureLockViewListener{
        void gestureResult(boolean isRight);
        void exceedMaxTryTime(int tryTimes);
    }

    public void setOnGestureLockViewListener(OnGestureLockViewListener listener) {
        this.onGestureLockViewListener = listener;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (!mPath.isEmpty()) {
            canvas.drawPath(mPath, mPaint);
        }

        if (itemCenterX != 0 && itemCenterY != 0) {
            canvas.drawLine(itemCenterX, itemCenterY, skyPoint.x, skyPoint.y, mPaint);
        }
    }

    private LockScreenItem getChildView(int x, int y) {
        for (int i = 0; i < lockScreenViews.length; i++) {
            if (isInItemViewArea(x, y, lockScreenViews[i])) {
                return lockScreenViews[i];
            }
        }
        return null;
    }

    private boolean isInItemViewArea(int x, int y, LockScreenItem lockScreenItem) {
        // LockScreenItem中内切正方形边长的一半
        int halfWidth = (int) ((lockScreenItem.getRight() - lockScreenItem.getLeft()) * Math.sqrt(2) / 4);
        // 只有在LockScreenItem的内切正方形区域内，才算在ItemView之内
        int centerX = (lockScreenItem.getLeft() + lockScreenItem.getRight()) / 2;
        int centerY = (lockScreenItem.getTop() + lockScreenItem.getBottom()) / 2;
        if (x > centerX - halfWidth && x < centerX + halfWidth
                && y > centerY - halfWidth && y < centerY + halfWidth) {
            return true;
        }
        return false;
    }

    private void resetStates() {
        choosedItems.clear();
        mPath.reset();
        for (int i = 0; i < lockScreenViews.length; i++) {
            lockScreenViews[i].setCurrentState(LockScreenItem.State.STATE_NO_FINGER);
            lockScreenViews[i].setmArrowDegree(-1);
        }
    }
}
