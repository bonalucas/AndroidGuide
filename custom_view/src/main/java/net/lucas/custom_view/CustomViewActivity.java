package net.lucas.custom_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import net.lucas.custom_view.circle.CircleActionActivity;
import net.lucas.custom_view.circle.CircleActionView;
import net.lucas.custom_view.download.DownloadProgressActivity;
import net.lucas.custom_view.longpress.LongPressActionActivity;
import net.lucas.custom_view.round.FilletImageActivity;
import net.lucas.custom_view.round.GlideCornersTransformation;

public class CustomViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_view_layout);

        Button fillet_image_button = findViewById(R.id.fillet_image_button);
        fillet_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomViewActivity.this, FilletImageActivity.class);
                startActivity(intent);
            }
        });

        Button circle_action_button = findViewById(R.id.circle_action_button);
        circle_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomViewActivity.this, CircleActionActivity.class);
                startActivity(intent);
            }
        });

        Button long_press_action_button = findViewById(R.id.long_press_action_button);
        long_press_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomViewActivity.this, LongPressActionActivity.class);
                startActivity(intent);
            }
        });

        Button download_progress_button = findViewById(R.id.download_progress_button);
        download_progress_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomViewActivity.this, DownloadProgressActivity.class);
                startActivity(intent);
            }
        });
    }

}
