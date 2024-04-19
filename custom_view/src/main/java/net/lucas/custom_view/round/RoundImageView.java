package net.lucas.custom_view.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import net.lucas.custom_view.R;

public class RoundImageView extends CardView {

    public RoundImageView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RoundImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);
    }

    public RoundImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.rounded_image_view, this, true);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        String url = typedArray.getString(R.styleable.RoundImageView_url);
        int angle = typedArray.getInteger(R.styleable.RoundImageView_angle, 4);
        ImageView imageView = findViewById(R.id.rounded_image);
        // 动态设置图片
        Glide.with(context).load(url).into(imageView);
        // 设置圆角度
        setRadius(angle);
        typedArray.recycle();
    }

}
