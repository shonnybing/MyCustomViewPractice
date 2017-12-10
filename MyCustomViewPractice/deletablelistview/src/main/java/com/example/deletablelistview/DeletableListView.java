package com.example.deletablelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

/**
 * Created by Lenovo on 2017/11/21.
 */

public class DeletableListView extends ListView {
    private PopupWindow mPopupWindow;
    float xDown;
    float yDown;
    float xMove;
    float yMove;
    View mCurrentView;
    int mCurrentPosition;
    int mTouchSlope;
    int mPopupWindowWidth;
    int mPopupWindowHeight;
    OnDeleteClickListener mOnDeleteClickListener;
    Button deleteBtn;

    public DeletableListView(Context context) {
        super(context);
    }

    public DeletableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View btnView = inflater.inflate(R.layout.delete_btn, null);
        deleteBtn = (Button) btnView.findViewById(R.id.delete_btn);
        mPopupWindow = new PopupWindow(btnView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();
        mPopupWindowWidth = mPopupWindow.getContentView().getMeasuredWidth();
        mPopupWindowHeight = mPopupWindow.getContentView().getMeasuredHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x =  ev.getX();
        float y = ev.getY();


        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = x;
                yDown = y;
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    return false;
                }
                mCurrentPosition = pointToPosition((int)xDown, (int)yDown);
                mCurrentView = getChildAt(mCurrentPosition - getFirstVisiblePosition());
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = x;
                yMove = y;
                int[] position = new int[2];
                mCurrentView.getLocationOnScreen(position);
                if (xMove < xDown && Math.abs(xMove - xDown) >= mTouchSlope && Math.abs(yMove - yDown) < mTouchSlope) {
                    mPopupWindow.showAtLocation(mCurrentView, Gravity.NO_GRAVITY, position[0] + mCurrentView.getMeasuredWidth(),
                            position[1]);
                    deleteBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnDeleteClickListener != null) {
                                mOnDeleteClickListener.clickDelete(mCurrentPosition);
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                }
                break;
        }
        return true;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        mOnDeleteClickListener = onDeleteClickListener;
    }

    interface OnDeleteClickListener {
        public void clickDelete(int position);
    }
}
