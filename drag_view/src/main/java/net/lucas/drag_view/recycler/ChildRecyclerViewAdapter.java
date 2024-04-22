package net.lucas.drag_view.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.lucas.drag_view.R;

public class ChildRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    public ChildRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildTextViewHolder(LayoutInflater.from(context).inflate(R.layout.child_text_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChildTextViewHolder viewHolder = (ChildTextViewHolder) holder;
        viewHolder.textView.setText("CHILD_DEMO" + position);
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    public static class ChildTextViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ChildTextViewHolder(@NonNull View view) {
            super(view);
            this.textView = view.findViewById(R.id.child_text_view);
        }
    }

}
