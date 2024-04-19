package net.lucas.custom_view.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;

import net.lucas.custom_view.R;

public class GlideRoundImageView extends AppCompatImageView {

    public GlideRoundImageView(@NonNull Context context) {
        super(context);
    }

    public GlideRoundImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public GlideRoundImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GlideRoundImageView);
        String resource = typedArray.getString(R.styleable.GlideRoundImageView_resource);
        int radius = typedArray.getInteger(R.styleable.GlideRoundImageView_radius, 4);
        // 动态设置图片
        Glide.with(context)
                .load(resource)
                .transform(new GlideCornersTransformation(radius))
                .into(this);
        typedArray.recycle();
    }

}
