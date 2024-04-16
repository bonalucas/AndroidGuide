package net.lucas.list_image.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import net.lucas.list_image.R;

public class SecondListViewAdapter extends BaseAdapter {

    private static final int SINGLE_ITEM = 0;
    private static final int DOUBLE_ITEM = 1;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private String[] pictures;

    public SecondListViewAdapter(@NonNull Context context, @NonNull String[] objects) {
        this.context = context;
        this.pictures = objects;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return pictures.length;
    }

    @Override
    public Object getItem(int position) {
        return pictures[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? SINGLE_ITEM : DOUBLE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int type = getItemViewType(position);
        SingleItemViewHolder singleItemViewHolder = null;
        DoubleItemViewHolder doubleItemViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case SINGLE_ITEM:
                    singleItemViewHolder = new SingleItemViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.single_item, parent, false);
                    singleItemViewHolder.imageView = convertView.findViewById(R.id.single_item_image_view);
                    convertView.setTag(R.id.single_item, singleItemViewHolder);
                    break;
                case DOUBLE_ITEM:
                    doubleItemViewHolder = new DoubleItemViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.double_item, parent, false);
                    doubleItemViewHolder.imageView1 = convertView.findViewById(R.id.double_item_image_view1);
                    doubleItemViewHolder.imageView2 = convertView.findViewById(R.id.double_item_image_view2);
                    convertView.setTag(R.id.double_item, doubleItemViewHolder);
                    break;
            }
        } else {
            switch (type) {
                case SINGLE_ITEM:
                    singleItemViewHolder = (SingleItemViewHolder) convertView.getTag(R.id.single_item);
                    break;
                case DOUBLE_ITEM:
                    doubleItemViewHolder = (DoubleItemViewHolder) convertView.getTag(R.id.double_item);
                    break;
            }
        }
        switch (type) {
            case SINGLE_ITEM:
                Glide.with(context).load(pictures[position]).into(singleItemViewHolder.imageView);
                break;
            case DOUBLE_ITEM:
                Glide.with(context).load(pictures[position]).into(doubleItemViewHolder.imageView1);
                Glide.with(context).load(pictures[position]).into(doubleItemViewHolder.imageView2);
                break;
        }
        return convertView;
    }


    public static class SingleItemViewHolder {
        public ImageView imageView;
    }

    public static class DoubleItemViewHolder {
        public ImageView imageView1;
        public ImageView imageView2;
    }

}
