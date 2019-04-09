package com.example.marketprice;

import com.example.marketprice.Adapter.FoodListViewAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SearchAroundFood extends Fragment implements OnMapReadyCallback {

    View v;
    private GoogleMap mMap;

    private ListView listView;
    private FoodListViewAdapter adapter;

    //test용 데이터
    private int[] img = {R.drawable.hamburger,R.drawable.beer,R.drawable.transportation};
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getContext(), (position+1)+"번째 리스트가 클릭되었습니다.", Toast.LENGTH_SHORT).show();

                Bundle args = new Bundle();
                args.putInt("img", img[position]);
                args.putString("Name",  Name[position]);
                args.putString("Price", Price[position]);

                SearchAroundFoodDetail fragment2 = new SearchAroundFoodDetail();

                fragment2.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment2)
                        .commit();
            }
        });


        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void onMapReady(final GoogleMap map){

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,1));
        map.animateCamera(CameraUpdateFactory.zoomTo(18));
    }
}
