package net.lucas.custom_view.circle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CircleActionView extends View {

    private Paint paint;
    private ValueAnimator animator;
    private List<Circle> circleList;
    private Random random;


    public CircleActionView(Context context) {
        super(context);
        init();
    }

    public CircleActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circleList = new ArrayList<>();
        random = new Random();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float pointX = event.getX();
                    float pointY = event.getY();

                    Circle circle = new Circle(0, pointX, pointY);
                    circleList.add(circle);

                    animator = ValueAnimator.ofInt(20, random.nextInt( 100) + 100);
                    animator.setDuration(2000);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            circle.setRadius((int) animation.getAnimatedValue());
                            invalidate();
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            circle.setRadius(0);
                            invalidate();
                        }
                    });
                    animator.start();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Circle circle : circleList) {
            canvas.drawCircle(circle.getPointX(), circle.getPointY(), circle.getRadius(), paint);
        }
    }

    private static class Circle {

        private int radius;
        private float pointX;
        private float pointY;

        public Circle(int radius, float pointX, float pointY) {
            this.radius = radius;
            this.pointX = pointX;
            this.pointY = pointY;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public float getPointX() {
            return pointX;
        }

        public void setPointX(float pointX) {
            this.pointX = pointX;
        }

        public float getPointY() {
            return pointY;
        }

        public void setPointY(float pointY) {
            this.pointY = pointY;
        }
    }

}
