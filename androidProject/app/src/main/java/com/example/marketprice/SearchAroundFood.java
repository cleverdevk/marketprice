package com.example.marketprice;

import com.example.marketprice.Adapter.FoodListViewAdapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SearchAroundFood extends Fragment {

    View v;

    private ListView listView;
    private FoodListViewAdapter adapter;

    //test용 데이터
    private int[] img = {R.drawable.food,R.drawable.souvenir,R.drawable.transportation};
    private String[] Name = {"음식 1","음식 2","음식 3"};
    private String[] Price = {"100","200","300"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.search_food, container, false);

        //변수 초기화
        adapter = new FoodListViewAdapter();
        listView = (ListView)v.findViewById(R.id.List_view);

        //어뎁터 할당
        listView.setAdapter(adapter);

        //adapter를 통한 값 전달
        for(int i = 0; i < img.length; i++){
            adapter.addVO(ContextCompat.getDrawable(this.getContext(), img[i]), Name[i], Price[i]);
        }




        return v;
    }
}
