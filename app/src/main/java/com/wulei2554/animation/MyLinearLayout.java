package com.wulei2554.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当SlidingMenu打开的时候，拦截并消费触摸事件
 */

public class MyLinearLayout extends LinearLayout {

    private SlidingMenu slidingMenu;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSlidingMenu(SlidingMenu slidingMenu){
            this.slidingMenu = slidingMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slidingMenu != null && slidingMenu.getDragState() == SlidingMenu.DragState.Open){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slidingMenu != null && slidingMenu.getDragState() == SlidingMenu.DragState.Open){
            if (event.getAction() == MotionEvent.ACTION_UP){
                slidingMenu.closeMenu();
            }
            //如果slidingMenu打开则应该拦截并消费掉事件
            return true;
        }
        return super.onTouchEvent(event);
    }
}
