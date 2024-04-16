package net.lucas.list_image.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import net.lucas.list_image.R;
import net.lucas.list_image.component.SecondListViewAdapter;

public class SecondFragment extends Fragment {

    private Activity activity;
    private String headerPicture = "https://seopic.699pic.com/photo/40074/8212.jpg_wh1200.jpg";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.second_fragment, container, false);
        ListView listView = view.findViewById(R.id.second_list_view);
        View headerView = getLayoutInflater().inflate(R.layout.recycler_header, listView, false);
        ImageView imageView = headerView.findViewById(R.id.recycler_header_image_view);
        Glide.with(activity).load(headerPicture).into(imageView);
        listView.addHeaderView(headerView);
        SecondListViewAdapter secondListViewAdapter = new SecondListViewAdapter(activity, getArguments().getStringArray("pictures"));
        listView.setAdapter(secondListViewAdapter);
        return view;
    }

}
