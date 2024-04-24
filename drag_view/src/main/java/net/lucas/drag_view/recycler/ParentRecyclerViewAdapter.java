package net.lucas.drag_view.recycler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.lucas.drag_view.R;

public class ParentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public ParentRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ChildViewHolder(LayoutInflater.from(context).inflate(R.layout.child_recycler_item, parent, false));
        } else {
            return new ParentViewHolder(LayoutInflater.from(context).inflate(R.layout.parent_text_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChildViewHolder) {
            ((ChildViewHolder) holder).recyclerView.setBackgroundColor(Color.WHITE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            ((ChildViewHolder) holder).recyclerView.setLayoutManager(linearLayoutManager);
            ChildRecyclerViewAdapter childRecyclerViewAdapter = new ChildRecyclerViewAdapter(context);
            ((ChildViewHolder) holder).recyclerView.setAdapter(childRecyclerViewAdapter);
            ((ChildViewHolder) holder).recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            ((ChildViewHolder) holder).recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            ((ChildViewHolder) holder).recyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                    return false;
                }
            });
        } else if (holder instanceof ParentViewHolder) {
            ((ParentViewHolder) holder).textView.setText("PARENT_DEMO" + position);
        }
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ParentViewHolder(@NonNull View view) {
            super(view);
            this.textView = view.findViewById(R.id.parent_text_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerView;

        public ChildViewHolder(@NonNull View view) {
            super(view);
            this.recyclerView = view.findViewById(R.id.child_recycler_view);
        }
    }

}
