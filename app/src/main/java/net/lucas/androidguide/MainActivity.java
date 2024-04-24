package net.lucas.androidguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import net.lucas.custom_dialog.DemoDialogActivity;
import net.lucas.custom_view.CustomViewActivity;
import net.lucas.download_service.DownloadActivity;
import net.lucas.drag_view.EventActivity;
import net.lucas.file_storage.FileStorageActivity;
import net.lucas.list_image.ListImageActivity;
import net.lucas.other.OtherKNActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button file_storage = findViewById(R.id.file_storage);
        file_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileStorageActivity.class);
                startActivity(intent);
            }
        });

        Button list_image = findViewById(R.id.list_image);
        list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListImageActivity.class);
                startActivity(intent);
            }
        });

        Button custom_dialog = findViewById(R.id.custom_dialog);
        custom_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DemoDialogActivity.class);
                startActivity(intent);
            }
        });

        Button custom_view = findViewById(R.id.custom_view);
        custom_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomViewActivity.class);
                startActivity(intent);
            }
        });

        Button event_dispatch = findViewById(R.id.event_dispatch);
        event_dispatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
            }
        });

        Button download_service = findViewById(R.id.download_service);
        download_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

        Button other = findViewById(R.id.other);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OtherKNActivity.class);
                startActivity(intent);
            }
        });

    }

}