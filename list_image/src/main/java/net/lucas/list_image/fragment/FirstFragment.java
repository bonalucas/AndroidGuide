package net.lucas.list_image.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.lucas.list_image.R;
import net.lucas.list_image.component.DividerItemDecoration;
import net.lucas.list_image.component.FirstRecyclerViewAdapter;

public class FirstFragment extends Fragment {

    private Activity activity;

    private boolean isLoading = false;

    private FirstRecyclerViewAdapter firstRecyclerViewAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment, container, false);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(activity, "下拉刷新数据", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        RecyclerView firstRecyclerView = view.findViewById(R.id.first_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        firstRecyclerView.setLayoutManager(layoutManager);
        firstRecyclerView.addItemDecoration(new DividerItemDecoration(activity));
        firstRecyclerViewAdapter = new FirstRecyclerViewAdapter(activity, getArguments().getStringArray("pictures"));
        firstRecyclerView.setAdapter(firstRecyclerViewAdapter);

        firstRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int visibleItem = layoutManager.getChildCount();
                int totalItem = layoutManager.getItemCount();
                int state = recyclerView.getScrollState();
                if (!isLoading) {
                    if (visibleItem > 0 && lastVisibleItem == totalItem - 1 && state == firstRecyclerView.SCROLL_STATE_IDLE){

                        firstRecyclerViewAdapter.setOnLoadMoreListener(new FirstRecyclerViewAdapter.OnLoadMoreListener() {
                            @Override
                            public void onLoadMore() {
                                Toast.makeText(activity, "上拉加载数据", Toast.LENGTH_SHORT).show();
                                loadMore();
                            }
                        });
                    }
                }
            }
        });

        return view;
    }

    public void loadMore() {
        isLoading = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                firstRecyclerViewAdapter.setNewData(new String[]{
                        "https://seopic.699pic.com/photo/50135/8221.jpg_wh1200.jpg",
                        "https://seopic.699pic.com/photo/50135/8179.jpg_wh1200.jpg",
                        "https://seopic.699pic.com/photo/50111/1635.jpg_wh1200.jpg",
                        "https://seopic.699pic.com/photo/50139/5280.jpg_wh1200.jpg",
                });
                firstRecyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

}
