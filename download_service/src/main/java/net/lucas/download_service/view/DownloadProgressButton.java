
package net.lucas.download_service.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import net.lucas.download_service.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DownloadProgressButton extends AppCompatButton {

    // 边框宽度
    private float mBorderWidth;
    // 按钮圆角度
    private float mButtonRadius;
    // 背景颜色
    private int mBackgroundColor;
    // 开启下载后背景颜色
    private int mBackgroundDownloadColor;
    // 文字颜色
    private int mTextColor;
    // 下载覆盖后颜色
    private int mTextCoverColor;

    // 背景画笔
    private Paint mBackgroundPaint;
    // 按钮文字画笔
    private volatile Paint mTextPaint;

    // 最大进度
    private int mMaxProgress;
    // 最小进度
    private int mMinProgress;

    // 下载动画
    private ValueAnimator mProgressAnimation;
    // 安装动画
    private ArrayList<ValueAnimator> mFinishAnimators;

    private float mTextRightBorder;
    private float mTextBottomBorder;

    // 未下载状态
    public static final int STATE_NORMAL = 0;
    // 下载之中状态
    public static final int STATE_DOWNLOADING = 1;
    // 暂停下载
    public static final int STATE_PAUSE = 2;
    // 下载完成
    public static final int STATE_FINISH = 3;
    // 打开状态
    public static final int STATE_AVAILABLE = 4;

    // 下载状态
    private int mState;
    // 记录当前文字
    private CharSequence mCurrentText;
    private float mProgress = -1;
    private float mToProgress;
    private float mProgressPercent;

    public static final float SCALE = 1.0f;

    private float[] scaleFloats = new float[]{ SCALE, SCALE, SCALE };

    private float[] translateYFloats = new float[3];

    public DownloadProgressButton(@NonNull Context context) {
        super(context);
    }

    public DownloadProgressButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init();
        setupAnimations();
    }

    public DownloadProgressButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
        setupAnimations();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressButton);
        try {
            mBorderWidth = a.getDimension(R.styleable.DownloadProgressButton_progress_btn_border_width, dp2px(2));
            mButtonRadius = a.getDimension(R.styleable.DownloadProgressButton_progress_btn_radius, 0);
            mBackgroundColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_background_color, Color.parseColor("#3385FF"));
            mBackgroundDownloadColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_background_download_color, Color.parseColor("#E8E8E8"));
            mTextColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_text_color, mBackgroundColor);
            mTextCoverColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_text_cover_color, Color.WHITE);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        // 初始化进度
        mMaxProgress = 100;
        mMinProgress = 0;
        // 设置背景画笔
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        // 设置文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(50f);
        // 解决文字有时候画不出问题
        setLayerType(LAYER_TYPE_SOFTWARE, mTextPaint);
        // 初始化状态设为NORMAL
        mState = STATE_NORMAL;
        mProgress = 0;
        invalidate();
    }

    private void setupAnimations() {
        // 下载动画
        mProgressAnimation = ValueAnimator.ofFloat(0, 1).setDuration(500);
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 更新进度
                float timePercent = (float) animation.getAnimatedValue();
                mProgress = (mToProgress - mProgress) * timePercent + mProgress;
                invalidate();
            }
        });
        // 安装动画
        mFinishAnimators = createBallPulseAnimators();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 通过 onMeasure 方法获取 View 的高并设置给按钮圆角度
        setButtonRadius((float) MeasureSpec.getSize(heightMeasureSpec) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawTextAbove(canvas);
    }

    /**
     * 绘制按钮背景
     */
    private void drawBackground(Canvas canvas) {
        // 绘制背景边框
        RectF mBackgroundBounds = new RectF(mBorderWidth, mBorderWidth, getMeasuredWidth() - mBorderWidth, getMeasuredHeight() - mBorderWidth);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStrokeWidth(mBorderWidth);
        canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
        // 绘制背景
        switch (mState) {
            case STATE_NORMAL:
            case STATE_FINISH:
            case STATE_AVAILABLE:
                mBackgroundPaint.setStyle(Paint.Style.FILL);
                mBackgroundPaint.setColor(mBackgroundColor);
                canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                break;
            case STATE_PAUSE:
            case STATE_DOWNLOADING:
                if (mProgress == mMaxProgress) {
                    mBackgroundPaint.setStyle(Paint.Style.FILL);
                    mBackgroundPaint.setColor(mBackgroundColor);
                    canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                } else {
                    // 计算当前的进度
                    mProgressPercent = mProgress / (mMaxProgress + 0f);
                    // 保存画布状态
                    canvas.save();
                    // 绘制右半边未下载进度图层
                    mBackgroundPaint.setStyle(Paint.Style.FILL);
                    mBackgroundPaint.setColor(mBackgroundDownloadColor);
                    canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                    // 设置图层显示模式为 SRC_ATOP 并绘制已下载进度图层
                    PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
                    mBackgroundPaint.setColor(mBackgroundColor);
                    mBackgroundPaint.setXfermode(porterDuffXfermode);
                    float right = mBackgroundBounds.right * mProgressPercent;
                    Path path = new Path();
                    RectF leftRect = new RectF(mBackgroundBounds.left, mBackgroundBounds.top, mBackgroundBounds.left + mButtonRadius * 2, mBackgroundBounds.bottom);
                    path.addArc(leftRect, 90, 180);
                    path.lineTo(right, mBackgroundBounds.top);
                    path.lineTo(right, mBackgroundBounds.bottom);
                    path.lineTo(mBackgroundBounds.left + mButtonRadius * 2, mBackgroundBounds.bottom);
                    canvas.drawPath(path, mBackgroundPaint);
                    canvas.restore();
                    mBackgroundPaint.setXfermode(null);
                }
                break;
        }
    }

    // 绘制按钮文本
    private void drawTextAbove(Canvas canvas) {
        // 找到基准线的y轴
        final float y = (float) canvas.getHeight() / 2 - (mTextPaint.descent() / 2 + mTextPaint.ascent() / 2);
        if (mCurrentText == null) {
            mCurrentText = "";
        }
        final float textWidth = mTextPaint.measureText(mCurrentText.toString());
        mTextBottomBorder = y;
        // 找到文本绘制的右边界位置（用于后续安装动画位置定位）
        mTextRightBorder = (getMeasuredWidth() + textWidth) / 2;
        // 根据下载状态绘制文本
        switch (mState) {
            case STATE_NORMAL:
            case STATE_AVAILABLE:
                mTextPaint.setShader(null);
                mTextPaint.setColor(mTextCoverColor);
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                break;
            case STATE_PAUSE:
            case STATE_DOWNLOADING:
                // 进度条压过距离
                float coverLength = getMeasuredWidth() * mProgressPercent;
                // 开始渐变指示器
                float indicator1 = getMeasuredWidth() / 2 - textWidth / 2;
                // 结束渐变指示器
                float indicator2 = getMeasuredWidth() / 2 + textWidth / 2;
                // 文字变色部分的距离
                float coverTextLength = textWidth / 2 - getMeasuredWidth() / 2 + coverLength;
                float textProgress = coverTextLength / textWidth;
                if (coverLength <= indicator1) {
                    mTextPaint.setShader(null);
                    mTextPaint.setColor(mTextColor);
                } else if (indicator1 < coverLength && coverLength <= indicator2) {
                    // 设置变色效果（参数依次为渐变起始点坐标、渐变结束点坐标、渐变起始和终止颜色数组、颜色渐变位置数组、渐变着色器）
                    LinearGradient mProgressTextGradient = new LinearGradient((getMeasuredWidth() - textWidth) / 2, 0, (getMeasuredWidth() + textWidth) / 2, 0,
                            new int[]{mTextCoverColor, mTextColor},
                            new float[]{textProgress, textProgress + 0.001f},
                            Shader.TileMode.CLAMP);
                    mTextPaint.setColor(mTextColor);
                    mTextPaint.setShader(mProgressTextGradient);
                } else {
                    mTextPaint.setShader(null);
                    mTextPaint.setColor(mTextCoverColor);
                }
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                break;
            case STATE_FINISH:
                mTextPaint.setColor(mTextCoverColor);
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                drawLoadingBall(canvas);
                break;
        }
    }

    // 绘制安装动画
    public void drawLoadingBall(Canvas canvas) {
        for (int i = 0; i < 3; i++) {
            canvas.save();
            // 点的间隙
            float mBallSpacing = 4;
            // 点的半径
            float mBallRadius = 6;
            float translateX = mTextRightBorder + 10 + (mBallRadius * 2) * i + mBallSpacing * i;
            canvas.translate(translateX, mTextBottomBorder);
            canvas.drawCircle(0, translateYFloats[i], mBallRadius * scaleFloats[i], mTextPaint);
            canvas.restore();
        }
    }

    // 创建安装动画
    public ArrayList<ValueAnimator> createBallPulseAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        int[] delays = new int[]{120, 240, 360};
        for (int i = 0; i < 3; i++) {
            final int index = i;

            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3f, 1);

            scaleAnim.setDuration(750);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);

            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animators.add(scaleAnim);
        }
        return animators;
    }

    // 开启安装动画
    private void startAnimators() {
        for (int i = 0; i < mFinishAnimators.size(); i++) {
            ValueAnimator animator = mFinishAnimators.get(i);
            animator.start();
        }
    }

    // 关闭安装动画
    private void stopAnimators() {
        if (mFinishAnimators != null) {
            for (ValueAnimator animator : mFinishAnimators) {
                if (animator != null && animator.isStarted()) {
                    animator.end();
                }
            }
        }
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        if (mState != state) {
            this.mState = state;
            invalidate();
            if (state == STATE_FINISH) {
                //开启点动画
                startAnimators();
            } else {
                stopAnimators();
            }
        }

    }

    /**
     * 设置当前按钮文字
     */
    public void setCurrentText(CharSequence charSequence) {
        mCurrentText = charSequence;
        invalidate();
    }

    /**
     * 设置带下载进度的文字
     */
    public void setProgressText(String text, float progress) {
        if (progress >= mMinProgress && progress <= mMaxProgress) {
            DecimalFormat format = new DecimalFormat("##0.0");
            mCurrentText = text + format.format(progress) + "%";
            mToProgress = progress;
            if (mProgressAnimation.isRunning()) {
                mProgressAnimation.resume();
                mProgressAnimation.start();
            } else {
                mProgressAnimation.start();
            }
        } else if (progress < mMinProgress) {
            mProgress = 0;
        } else if (progress > mMaxProgress) {
            mProgress = mMaxProgress;
            mCurrentText = text + mProgress + "%";
            invalidate();
        }
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int width) {
        this.mBorderWidth = dp2px(width);
    }

    public float getButtonRadius() {
        return mButtonRadius;
    }

    public void setButtonRadius(float buttonRadius) {
        mButtonRadius = buttonRadius;
    }

    public int getmBackgroundColor() {
        return mBackgroundColor;
    }

    public void setmBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
    }

    public int getmBackgroundDownloadColor() {
        return mBackgroundDownloadColor;
    }

    public void setmBackgroundDownloadColor(int mBackgroundDownloadColor) {
        this.mBackgroundDownloadColor = mBackgroundDownloadColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public int getTextCoverColor() {
        return mTextCoverColor;
    }

    public void setTextCoverColor(int textCoverColor) {
        mTextCoverColor = textCoverColor;
    }

    public int getMinProgress() {
        return mMinProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    /**
     * dp转px：确保在不同密度的设备上显示正确尺寸
     */
    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

}
