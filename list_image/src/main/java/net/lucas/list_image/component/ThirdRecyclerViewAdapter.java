package net.lucas.list_image.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.lucas.list_image.R;

public class ThirdRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SINGLE_ITEM = 0;
    private static final int DOUBLE_ITEM = 1;
    private static final int HEADER_ITEM = 3;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private String[] pictures;
    private int mHeaderCount = 1;
    private String headerPicture = "https://seopic.699pic.com/photo/40074/8212.jpg_wh1200.jpg";
    private OnLoadMoreListener onLoadMoreListener;

    public ThirdRecyclerViewAdapter(Context context, String[] pictures) {
        this.context = context;
        this.pictures = pictures;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SINGLE_ITEM) {
            return new SingleItemViewHolder(mLayoutInflater.inflate(R.layout.single_item, parent, false));
        } else if (viewType == DOUBLE_ITEM) {
            return new DoubleItemViewHolder(mLayoutInflater.inflate(R.layout.double_item, parent, false));
        } else {
            return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.recycler_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SingleItemViewHolder) {
            Glide.with(context).load(pictures[position - mHeaderCount]).into(((SingleItemViewHolder) holder).imageView);
        } else if (holder instanceof DoubleItemViewHolder) {
            String url = pictures[position - mHeaderCount];
            Glide.with(context).load(url).into(((DoubleItemViewHolder) holder).imageView1);
            Glide.with(context).load(url).into(((DoubleItemViewHolder) holder).imageView2);
        } else if (holder instanceof HeaderViewHolder) {
            Glide.with(context).load(headerPicture).into(((HeaderViewHolder) holder).imageView);
        }
    }

    @Override
    public int getItemCount() {
        return pictures == null ? mHeaderCount : pictures.length + mHeaderCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderCount != 0 && position < mHeaderCount) {
            return HEADER_ITEM;
        } else {
            return position % 2 == 0 ? SINGLE_ITEM : DOUBLE_ITEM;
        }
    }

    public static class SingleItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public SingleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.single_item_image_view);
        }

    }

    public static class DoubleItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView1;
        public ImageView imageView2;

        public DoubleItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.double_item_image_view1);
            imageView2 = itemView.findViewById(R.id.double_item_image_view2);
        }

    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycler_header_image_view);
        }

    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        this.onLoadMoreListener.onLoadMore();
    }

    public void setNewData(String[] newData) {
        String[] newDataArray = new String[pictures.length + newData.length];
        System.arraycopy(pictures, 0, newDataArray, 0, pictures.length);
        System.arraycopy(newData, 0, newDataArray, pictures.length, newData.length);
        pictures = newDataArray;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
