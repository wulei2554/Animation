package com.wulei2554.animation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;
import com.wulei2554.animation.example.view.ColorUtil;

public class SlidingMenu extends FrameLayout {

    private ViewDragHelper helper;
    private View menuView;
    private View mainView;
    private float dragRange;
    private FloatEvaluator fl;
    private IntEvaluator il;
    private DragState currentState = DragState.Close;

    public SlidingMenu(Context context) {
        super(context);
        initViewDragHelper();

    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context,attrs);
        initViewDragHelper();

    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViewDragHelper();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount != 2){
            throw new IllegalStateException("SlidingMenu only have 2 chfildren");
        }
        menuView = getChildAt(0);

        mainView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = getMeasuredWidth();
        //mainView拖拽范围
        dragRange = width * 0.6f;
    }


    enum DragState{
        Open,Close
    }

    private void initViewDragHelper() {
        helper = ViewDragHelper.create(this,cb);
        fl = new FloatEvaluator();
        il = new IntEvaluator();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        helper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return helper.shouldInterceptTouchEvent(event);
    }

    private ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView){
                if (left < 0){
                    left = 0;
                }else if(left > dragRange) {
                    left = (int) dragRange;
                }
            }
            //这里无法固定menuView
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menuView){
                menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getMeasuredHeight());
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0 ){
                    newLeft = 0;
                }else if (newLeft > dragRange){
                    newLeft = (int) dragRange;
                }
                mainView.layout(newLeft,mainView.getTop() + dy,newLeft + mainView.getMeasuredWidth(),mainView.getBottom() + dy);
            }

            /**
             * 计算百分比
             */
            float fraction = mainView.getLeft()/dragRange;
            /**
             * 执行动画
             */
            executeAnim(fraction);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int left = mainView.getLeft();
            if (left > dragRange/2){
               openMenu();
            }else{
                closeMenu();
            }

            /**
             * 根据用户滑动的速度快慢决定是否打开与关闭
             */
            if (xvel > 500 && currentState != DragState.Open){
                openMenu();
            }else if (xvel < -500 && currentState != DragState.Close){
                closeMenu();
            }
        }
    };

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        helper.smoothSlideViewTo(mainView,0,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
    }

    /**
     * 打开菜单
     */
    public void openMenu() {
        helper.smoothSlideViewTo(mainView, (int) dragRange,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
    }

    private void executeAnim(float fraction) {
        //绽放mainView
        //计算缩放值
        /**
         * 1、手动计算
         *  float scaleValue = 0.8f + 0.2f*(1 - fraction);
         */

        /**
         * android提供的计算器
         * evaluate(fraction,1f,0.8f)
         * 参一：百分比
         * 参二：起始值
         * 参三：终点值
         */
        float scaleValue = fl.evaluate(fraction,1f,0.8f);
        ViewHelper.setScaleX(mainView,scaleValue);
        ViewHelper.setScaleY(mainView,scaleValue);

        ViewHelper.setTranslationX(menuView,il.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));

        ViewHelper.setScaleX(menuView,fl.evaluate(fraction,0.5f,1));
        ViewHelper.setScaleY(menuView,fl.evaluate(fraction,0.5f,1));

        ViewHelper.setAlpha(menuView,fl.evaluate(fraction,0.3f,1));

        //给SlidingMenu背景添加黑色的遮盖效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);

        //SlidingMenu状态
        if (fraction == 0.0f && currentState!= DragState.Close){
            if (listen != null){
                currentState = DragState.Close;
                listen.Close();
            }

        }
        if (fraction > 0.95 && currentState != DragState.Open){
            if (listen != null){
                currentState = DragState.Open;
                listen.Open();
            }
        }

        if (listen != null){
            listen.Draging(fraction);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (helper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
        }
    }
    public IOnDragStateChangeListen listen;

    public void setIOnDragStateChangeListen(IOnDragStateChangeListen listen){
        this.listen = listen;
    }

    public interface IOnDragStateChangeListen{
        void Open();
        void Close();
        void Draging(float fraction);
    }

    public DragState getDragState() {
        return currentState;
    }
}
