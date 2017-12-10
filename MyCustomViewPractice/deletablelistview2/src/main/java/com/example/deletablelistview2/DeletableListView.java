package com.example.deletablelistview2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by bfshao on 2017/11/29.
 */

public class DeletableListView extends ListView{
    private int touchSlop;
    private int xDown;
    private int yDown;
    private int xMove;
    private int yMove;
    private boolean isShown;
    private Button button;
    private ViewGroup mCurrentItem;
    private int position;
    private onDeleteListener onDeleteListener;

    public DeletableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeletableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        LayoutInflater inflater = LayoutInflater.from(context);
        button = (Button) inflater.inflate(R.layout.button, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                xDown = x;
                yDown = y;
                if (isShown) {
                    mCurrentItem.removeView(button);
                    isShown = false;
                    return false;
                }
                position = pointToPosition(x, y);
                mCurrentItem = (ViewGroup) getChildAt(position - getFirstVisiblePosition());
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = x;
                yMove = y;
                if (Math.abs(xMove - xDown) > touchSlop && Math.abs(yMove - yDown) < touchSlop && xMove < xDown && !isShown) {
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentItem.removeView(button);
                            onDeleteListener.deleteItem(position);
                            isShown = false;
                        }
                    });
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.CENTER_VERTICAL);
                    mCurrentItem.addView(button, params);
                    isShown = true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnDeleteListener (onDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public interface onDeleteListener {
        void deleteItem(int position);
    }
}
