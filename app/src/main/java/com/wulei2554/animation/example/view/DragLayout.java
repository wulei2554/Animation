package com.wulei2554.animation.example.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;


public class DragLayout extends FrameLayout {

    private View redView;
    private View blueView;
    private ViewDragHelper helper;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViewDragHelper();
    }

    /**
     * 初始化ViewDragHelper
     */
    private void initViewDragHelper() {
        helper = ViewDragHelper.create(this, cb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        helper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = helper.shouldInterceptTouchEvent(ev);
        if (result){
            return true;
        }else{
            return super.onInterceptTouchEvent(ev);
        }

    }

    /**
     * 当DragLayout的xml布局的结束标签被读到，执行该方法，此时知道有几个子View
     * 一般用于初始化子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        redView = getChildAt(0);

        blueView = getChildAt(1);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int measureSpec = MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width, MeasureSpec.EXACTLY);
//        redView.measure(measureSpec,measureSpec);
//        blueView.measure(measureSpec,measureSpec);
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        int left = getPaddingLeft() + getMeasuredWidth()/2 - redView.getMeasuredWidth()/2; //左边距  在中间
        int left = getPaddingLeft();  //左边距  在最左边
        int top = getPaddingTop(); //上边距
        redView.layout(left, top, redView.getMeasuredWidth() + left, top + redView.getMeasuredHeight());
        blueView.layout(left, redView.getBottom(), left + blueView.getMeasuredWidth(),
                redView.getBottom() + blueView.getMeasuredHeight());
    }

    private ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件
         * @param child 手指对应的view
         * @param pointerId 跟多点触摸有关，手指的id
         * @return true:捕获并解析 false:不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == redView || child == blueView;
        }

        /**
         * 当view被开始捕获和解析的回调
         * 当tryCaptureView方法返回true调用
         * @param capturedChild 当前被捕获的View
         * @param activePointerId 跟多点触摸有关，手指的id
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            Log.e("wulei", "RedView被捕获了。。。");
        }

        /**
         * @param child 具体的View
         * @return 水平方向拖拽范围有多大
         * 一般为正数
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return 1;
        }

        /**
         *
         * @param child 具体的View
         * @return 垂直方向拖拽范围有多大
         * 一般为正数
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            return 1;
        }

        /**
         * 当tryCaptureView方法返回true后，手指又移动了，将会产生View移动，通过此方法控制View的移动
         *  这个方法作用：
         *  1、控制View的移动范围
         *  2、可以添加阻力效果
         * @param child 被捕获的View
         * @param left  建议值：表示ViewDragHelper认为你想让当前view的left改变的值：child.getLeft()+dx
         * @param dx    本次child水平方向移动的距离
         * @return 表示当前view的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            /**
             * 如果水平方向不滑动，则设置：return left -dx ;
             */
            if (left < 0) {
                left = 0;
            } else if (left > getMeasuredWidth() - redView.getMeasuredWidth()) {
                left = getMeasuredWidth() - redView.getMeasuredWidth();
            }
            return left;
        }

        /**
         * 当tryCaptureView方法返回true后，手指又移动了，将会产生View移动，通过此方法控制View的移动
         *  这个方法作用：
         *  1、控制View的移动范围
         *  2、可以添加阻力效果
         * @param child 被捕获的View
         * @param top  建议值：表示ViewDragHelper认为你想让当前view的top改变的值：child.getTop()+dy
         * @param dy    手指位置的变化情况
         * @return 表示当前view的top变成的值
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            //竖起方向不发生位移
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - redView.getMeasuredHeight()) {
                top = getMeasuredHeight() - redView.getMeasuredHeight();
            }
            return top;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        /**
         * 当child的位置改变的时候执行，一般用于做view的伴随运动
         * @param changedView  位置改变的child
         * @param left child当前最新的left
         * @param top  child当前最新的top
         * @param dx   本次水平移动的距离
         * @param dy   本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == redView) {
                //移动的是redView,需要改变blueView
                blueView.layout(blueView.getLeft() + dx, blueView.getTop() + dy, blueView.getRight() + dx, blueView.getBottom() + dy);
            } else if (changedView == blueView) {
                //移动的是blueView,需要改变redView
                redView.layout(redView.getLeft() + dx, redView.getTop() + dy, redView.getRight() + dx, redView.getBottom() + dy);
            }

            /**
             * 计算百分比
             */
            float fraction = (changedView.getLeft() * 1.0f / (getMeasuredWidth() - redView.getMeasuredWidth()));

            /**
             * 执行动画
             */
            excuteAnim(fraction);
        }


        /**
         * 手指抬起执行该方法
         * @param releasedChild 当前抬起的view
         * @param xvel x方向移动的速度 正：向右移动  负：向左移动
         * @param yvel  y方向移动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth() / 2 - redView.getMeasuredWidth() / 2;
            if (releasedChild.getLeft() < centerLeft) {
                helper.smoothSlideViewTo(releasedChild, 0, releasedChild.getTop());
            } else {
                helper.smoothSlideViewTo(releasedChild, getMeasuredWidth() - redView.getMeasuredWidth(), releasedChild.getTop());
            }
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }

        /**
         * 在通过触摸位置，查找x/y对应的View角标的时候，会调用此方法，不需要重写
         */
        @Override
        public int getOrderedChildIndex(int index) {
            return super.getOrderedChildIndex(index);
        }

        /**
         * 以下三个方法跟拖拽边缘有关
         */
        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return super.onEdgeLock(edgeFlags);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
        }
    };

    /**
     * 执行一系列动画
     * @param fraction 百分比
     */
    private void excuteAnim(float fraction) {

        /**
         * 旋转动画
         */
        ViewHelper.setRotation(redView,720*fraction); //平面转
//        ViewHelper.setRotationX(redView,720*fraction); //X轴转
//        ViewHelper.setRotationY(redView,720*fraction); //Y轴转

        /**
         * 缩放动画
         */
//        ViewHelper.setAlpha(redView,1 - fraction);

        /**
         * 设置过渡颜色的渐变
         */
        redView.setBackgroundColor((int) ColorUtil.evaluateColor(fraction, Color.RED,Color.GREEN));
    }

    /**
     * 一定要重写该方法，否则View不滑动
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (helper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }
}
