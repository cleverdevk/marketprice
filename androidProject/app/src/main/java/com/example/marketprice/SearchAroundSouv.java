package com.example.marketprice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marketprice.Adapter.FoodListViewAdapter;

public class SearchAroundSouv extends Fragment {

    View v;

    private ListView listView;
    private FoodListViewAdapter adapter;

    //test용 데이터
    private int[] img = {R.drawable.food,R.drawable.souvenir,R.drawable.transportation};
    private String[] Name = {"기념품 1","기념품 2","기념품 3"};
    private String[] Price = {"100","200","300"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.search_souv, container, false);

        //변수 초기화
        adapter = new FoodListViewAdapter();
        listView = (ListView)v.findViewById(R.id.List_view);

        //어뎁터 할당
        listView.setAdapter(adapter);

//        //adapter를 통한 값 전달
//        for(int i = 0; i < img.length; i++){
//            adapter.addVO(ContextCompat.getDrawable(this.getContext(), img[i]), Name[i], Price[i]);
//        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getContext(), (position+1)+"번째 리스트가 클릭되었습니다.", Toast.LENGTH_SHORT).show();

                Bundle args = new Bundle();
                args.putInt("img", img[position]);
                args.putString("Name",  Name[position]);
                args.putString("Price", Price[position]);

                SearchAroundSouvDetail fragment2 = new SearchAroundSouvDetail();

                fragment2.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment2)
                        .commit();
            }
        });


        return v;
    }
}
