package net.lucas.drag_view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DraggableView extends ViewGroup {

    private int screenWidth, screenHeight;
    private float downX;
    private float downY;

    public DraggableView(Context context) {
        super(context);
        init(context);
    }

    public DraggableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DraggableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.isEnabled()) {
            // 处理拖动操作
            drag(event);
            return super.dispatchTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                break;
            // 拦截移动事件 button不需要处理移动事件
            case MotionEvent.ACTION_MOVE:
                isIntercept = true;
                break;
        }
        super.onInterceptTouchEvent(ev);
        return isIntercept;
    }

    private void drag(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float xDistance = event.getX() - downX;
                final float yDistance = event.getY() - downY;
                if (xDistance != 0 && yDistance != 0) {
                    int l = (int) (getLeft() + xDistance);
                    int r = (int) (getRight() + xDistance);
                    int t = (int) (getTop() + yDistance);
                    int b = (int) (getBottom() + yDistance);
                    if (l < 0) {
                        l = 0;
                        r = l + getWidth();
                    }
                    if (t < 0) {
                        t = 0;
                        b = t + getHeight();
                    }
                    if (r > screenWidth) {
                        r = screenWidth;
                        l = screenWidth - getWidth();
                    }
                    if (b > screenHeight) {
                        b = screenHeight;
                        t = screenHeight - getHeight();
                    }
                    this.layout(l, t, r, b);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;
            default:
                break;
        }
    }

}
