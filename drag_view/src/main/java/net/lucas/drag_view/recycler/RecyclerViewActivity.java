package net.lucas.drag_view.recycler;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.lucas.drag_view.R;

public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_recycler_layout);
        RecyclerView view = findViewById(R.id.parent_recycler_view);
        view.setBackgroundColor(Color.parseColor("#11B4DA"));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RecyclerViewActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(linearLayoutManager);
        ParentRecyclerViewAdapter parentRecyclerViewAdapter = new ParentRecyclerViewAdapter(RecyclerViewActivity.this);
        view.setAdapter(parentRecyclerViewAdapter);
    }
}
