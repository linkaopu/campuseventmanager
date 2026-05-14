package com.example.campuseventmanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * 侧滑菜单布局组件
 * <p>
 * 自定义FrameLayout实现侧滑菜单功能，支持用户从右向左滑动显示操作菜单。
 * 布局结构要求：
 * - 第一个子View为内容区域（滑动时会被拖动）
 * - 第二个子View为菜单区域（默认隐藏在右侧）
 * </p>
 */
public class SwipeMenuItemLayout extends FrameLayout {
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;

    /**
     * 菜单宽度（默认160dp转换为像素）
     */
    private int mMenuWidth;

    /**
     * 触摸滑动阈值（判断是否开始滑动的最小距离）
     */
    private int mTouchSlop;

    /**
     * 按下时的X坐标
     */
    private float mDownX;

    /**
     * 按下时的Y坐标
     */
    private float mDownY;

    /**
     * 是否正在拖拽状态
     */
    private boolean mDragging;

    /**
     * 是否已发生滑动（用于区分点击和滑动事件）
     */
    private boolean mSwiped;

    /**
     * 内容视图（第一个子View）
     */
    private View mContentView;

    /**
     * 菜单视图（第二个子View）
     */
    private View mMenuView;

    /**
     * 当前偏移量（内容视图向左滑动的距离）
     */
    private int mCurrentOffset = 0;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public SwipeMenuItemLayout(Context context) {
        this(context, null);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param attrs   属性集合
     */
    public SwipeMenuItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造函数
     *
     * @param context  上下文
     * @param attrs    属性集合
     * @param defStyle 默认样式
     */
    public SwipeMenuItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 初始化方法
     * <p>
     * 获取屏幕宽度、触摸滑动阈值和菜单宽度。
     * </p>
     *
     * @param context 上下文
     */
    private void init(Context context) {
        // 获取屏幕宽度
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        // 获取触摸滑动阈值（系统定义的最小滑动距离）
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // 设置菜单宽度为160dp（转换为像素）
        mMenuWidth = (int) (160 * context.getResources().getDisplayMetrics().density);
    }

    /**
     * 布局加载完成后回调
     * <p>
     * 获取内容视图和菜单视图的引用。
     * </p>
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 确保至少有两个子View（内容区和菜单区）
        if (getChildCount() >= 2) {
            mContentView = getChildAt(0); // 第一个子View为内容区域
            mMenuView = getChildAt(1); // 第二个子View为菜单区域
        }
    }

    /**
     * 测量控件尺寸
     * <p>
     * 自定义测量逻辑：总宽度为屏幕宽度+菜单宽度，确保菜单能完全显示。
     * </p>
     *
     * @param widthMeasureSpec  宽度测量规格
     * @param heightMeasureSpec 高度测量规格
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 总宽度 = 屏幕宽度 + 菜单宽度
        int totalWidth = mScreenWidth + mMenuWidth;
        int totalHeight = getMeasuredHeight();
        setMeasuredDimension(totalWidth, totalHeight);

        // 测量内容视图（宽度=屏幕宽度）
        if (mContentView != null) {
            mContentView.measure(
                    MeasureSpec.makeMeasureSpec(mScreenWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY));
        }
        // 测量菜单视图（宽度=菜单宽度）
        if (mMenuView != null) {
            mMenuView.measure(
                    MeasureSpec.makeMeasureSpec(mMenuWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY));
        }
    }

    /**
     * 布局子View
     * <p>
     * 内容视图显示在屏幕左侧，菜单视图显示在屏幕右侧（默认隐藏）。
     * </p>
     *
     * @param changed 是否发生变化
     * @param l       左边界
     * @param t       上边界
     * @param r       右边界
     * @param b       下边界
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mContentView != null && mMenuView != null) {
            // 内容视图从(0,0)到(屏幕宽度, 高度)
            mContentView.layout(0, 0, mScreenWidth, getHeight());
            // 菜单视图从(屏幕宽度,0)到(屏幕宽度+菜单宽度, 高度)
            mMenuView.layout(mScreenWidth, 0, mScreenWidth + mMenuWidth, getHeight());
            // 应用当前偏移量
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

    /**
     * 判断菜单是否处于打开状态
     *
     * @return true: 菜单已打开，false: 菜单已关闭
     */
    public boolean isMenuOpen() {
        return mCurrentOffset >= mMenuWidth;
    }

    /**
     * 关闭菜单（带动画效果）
     */
    public void closeMenu() {
        animateToOffset(0);
    }
}
