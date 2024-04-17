package net.lucas.custom_view.longpress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class LongPressActionView extends View {

    private Paint paint;
    private boolean isLongPressing = false;
    private float pointX, pointY;
    private Vibrator vibrator;
    private Handler handler = new Handler();
    private Runnable longPressRunnable;

    public LongPressActionView(Context context) {
        super(context);
        init(context);
    }

    public LongPressActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LongPressActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pointX = event.getX();
                        pointY = event.getY();
                        startLongPressTimer();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(event.getX() - pointX) > 10 || Math.abs(event.getY() - pointY) > 10) {
                            cancelLongPressTimer();
                            startLongPressTimer();
                            pointX = event.getX();
                            pointY = event.getY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        cancelLongPressTimer();
                        if (isLongPressing) {
                            isLongPressing = false;
                            invalidate();
                        }
                        break;
                }
                return true;
            }

            private void startLongPressTimer() {
                longPressRunnable = new Runnable() {
                    @Override
                    public void run() {
                        isLongPressing = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);
                            vibrator.vibrate(vibrationEffect);
                        } else {
                            vibrator.vibrate(100);
                        }
                        invalidate();
                    }
                };
                handler.postDelayed(longPressRunnable, 2000);
            }

            private void cancelLongPressTimer() {
                if (longPressRunnable != null) {
                    handler.removeCallbacks(longPressRunnable);
                }
            }

        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isLongPressing) {
            canvas.drawCircle(pointX, pointY, 20, paint);
        }
    }
}
