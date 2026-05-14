package com.example.campuseventmanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class SwipeMenuItemLayout extends FrameLayout {
    private int mScreenWidth;
    private int mMenuWidth;
    private int mTouchSlop;
    private float mDownX;
    private float mDownY;
    private boolean mDragging;
    private boolean mSwiped;
    private View mContentView;
    private View mMenuView;
    private int mCurrentOffset = 0;

    public SwipeMenuItemLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMenuWidth = (int) (160 * context.getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() >= 2) {
            mContentView = getChildAt(0);
            mMenuView = getChildAt(1);
        }
    }

    /**
     * 重写onMeasure方法，用于自定义测量控件的尺寸
     * 
     * @param widthMeasureSpec  宽度测量规格，包含测量模式和大小
     * @param heightMeasureSpec 高度测量规格，包含测量模式和大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 首先调用父类的onMeasure方法，确保基本的测量逻辑被执行
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 计算总宽度为屏幕宽度加上菜单宽度
        int totalWidth = mScreenWidth + mMenuWidth;
        // 获取测量得到的高度
        int totalHeight = getMeasuredHeight();
        // 设置测量后的尺寸为计算得到的总宽度和高度
        setMeasuredDimension(totalWidth, totalHeight);

        // 如果内容视图不为空，则测量内容视图
        if (mContentView != null) {
            // 测量内容视图，宽度为屏幕宽度，高度为总高度，使用精确测量模式
            mContentView.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY));
        }
        // 如果菜单视图不为空，则测量菜单视图
        if (mMenuView != null) {
            // 测量菜单视图，宽度为菜单宽度，高度为总高度，使用精确测量模式
            mMenuView.measure(
                    MeasureSpec.makeMeasureSpec(mMenuWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mContentView != null && mMenuView != null) {
            mContentView.layout(0, 0, mScreenWidth, getHeight());
            mMenuView.layout(mScreenWidth, 0, mScreenWidth + mMenuWidth, getHeight());
            mContentView.setTranslationX(-mCurrentOffset);
            mMenuView.setTranslationX(-mCurrentOffset);
        }
    }

    /**
     * 重写onInterceptTouchEvent方法，用于拦截触摸事件
     * 
     * @param ev 触摸事件对象，包含触摸位置、动作等信息
     * @return 如果要拦截事件则返回true，否则返回false
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 根据触摸事件的不同动作进行判断
        switch (ev.getAction()) {
            // 当手指按下时的动作
            case MotionEvent.ACTION_DOWN:
                // 记录按下的X坐标
                mDownX = ev.getX();
                // 记录按下的Y坐标
                mDownY = ev.getY();
                // 重置拖拽状态为false
                mDragging = false;
                break;

            // 当手指移动时的动作
            case MotionEvent.ACTION_MOVE:
                // 计算X方向移动的距离
                float dx = Math.abs(ev.getX() - mDownX);
                // 计算Y方向移动的距离
                float dy = Math.abs(ev.getY() - mDownY);
                // 如果X方向移动距离大于触摸阈值，并且大于Y方向移动距离
                if (dx > mTouchSlop && dx > dy) {
                    // 设置拖拽状态为true
                    mDragging = true;
                    // 拦截该事件，不再向下传递
                    return true;
                }
                break;
        }
        // 默认不拦截事件
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 根据触摸事件类型进行不同的处理
        switch (ev.getAction()) {
            // 当手指按下时触发
            case MotionEvent.ACTION_DOWN:
                // 记录按下时的X坐标
                mDownX = ev.getX();
                // 记录按下时的Y坐标
                mDownY = ev.getY();
                // 重置拖拽和滑动状态为false
                mDragging = false;
                mSwiped = false;
                break;

            // 当手指移动时触发
            case MotionEvent.ACTION_MOVE:
                // 计算手指在X方向上的移动距离
                float dx = ev.getX() - mDownX;
                // 计算手指在Y方向上的移动距离
                float dy = ev.getY() - mDownY;

                // 如果当前没有在拖拽状态，且移动距离超过了触摸阈值，且水平移动大于垂直移动
                if (!mDragging && Math.abs(dx) > mTouchSlop && Math.abs(dx) > Math.abs(dy)) {
                    // 设置为拖拽状态
                    mDragging = true;
                    // 设置为已滑动状态
                    mSwiped = true;
                    // 请求父视图不要拦截触摸事件
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                // 如果处于拖拽状态
                if (mDragging) {
                    // 计算新的偏移量
                    int newOffset = (int) (mCurrentOffset - dx);
                    // 确保新偏移量不小于0
                    if (newOffset < 0)
                        newOffset = 0;
                    // 确保新偏移量不超过菜单宽度
                    if (newOffset > mMenuWidth)
                        newOffset = mMenuWidth;

                    // 如果新偏移量与当前偏移量不同
                    if (newOffset != mCurrentOffset) {
                        // 更新当前偏移量
                        mCurrentOffset = newOffset;
                        // 设置内容视图的平移
                        mContentView.setTranslationX(-mCurrentOffset);
                        // 设置菜单视图的平移
                        mMenuView.setTranslationX(-mCurrentOffset);
                    }
                    // 更新按下时的X坐标为当前X坐标
                    mDownX = ev.getX();
                }
                break;

            // 当手指抬起或取消时触发
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 重置拖拽状态为false
                mDragging = false;
                // 恢复父视图对触摸事件的拦截
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                // 如果是抬起事件且没有发生滑动
                if (ev.getAction() == MotionEvent.ACTION_UP && !mSwiped) {
                    // 执行点击事件
                    performClick();
                } else {
                    // 如果当前偏移量超过菜单宽度的一半，则完全展开菜单
                    if (mCurrentOffset > mMenuWidth / 2) {
                        animateToOffset(mMenuWidth);
                    } else {
                        // 否则收起菜单
                        animateToOffset(0);
                    }
                }
                break;
        }
        // 返回true表示已处理该触摸事件
        return true;
    }

    /**
     * 将视图平滑移动到目标偏移位置
     * 
     * @param targetOffset 目标偏移位置，单位为像素
     */
    private void animateToOffset(int targetOffset) {
        // 记录起始偏移位置
        final int startOffset = mCurrentOffset;
        // 计算总偏移量
        final int delta = targetOffset - startOffset;

        // 如果偏移量为0，则无需执行动画
        if (delta == 0)
            return;

        // 设置动画持续时间为200毫秒
        final long duration = 200;
        // 记录动画开始时间
        final long startTime = System.currentTimeMillis();

        // 使用post方法在主线程中执行动画
        post(new Runnable() {
            @Override
            public void run() {
                // 计算已过去的时间
                long elapsed = System.currentTimeMillis() - startTime;
                // 计算动画进度（0-1之间）
                float progress = Math.min(1f, elapsed / (float) duration);

                // 根据进度计算新的偏移位置
                int newOffset = startOffset + (int) (delta * progress);

                // 如果新偏移位置与当前偏移位置不同，则更新视图位置
                if (newOffset != mCurrentOffset) {
                    // 更新当前偏移位置
                    mCurrentOffset = newOffset;
                    // 如果内容视图存在，则更新其水平平移
                    if (mContentView != null) {
                        mContentView.setTranslationX(-mCurrentOffset);
                    }
                    // 如果菜单视图存在，则更新其水平平移
                    if (mMenuView != null) {
                        mMenuView.setTranslationX(-mCurrentOffset);
                    }
                }

                // 如果动画未完成，则继续执行下一帧动画
                if (progress < 1f) {
                    post(this);
                }
            }
        });
    }

    public boolean isMenuOpen() {
        return mCurrentOffset >= mMenuWidth;
    }

    public void closeMenu() {
        animateToOffset(0);
    }
}
