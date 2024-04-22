package net.lucas.drag_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.lucas.drag_view.recycler.RecyclerViewActivity;
import net.lucas.drag_view.view.DragViewActivity;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_layout);
        Button drag_view = findViewById(R.id.drag_view);
        drag_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, DragViewActivity.class);
                startActivity(intent);
            }
        });
        Button recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, RecyclerViewActivity.class);
                startActivity(intent);
            }
        });
    }

}
