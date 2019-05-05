package com.example.marketprice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.marketprice.SearchAround.SearchAroundSouv;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddMenuActivity extends Fragment implements OnMapReadyCallback {

    View v;

    LinearLayout foodView, souView, transView;

    private Context context;

    private MapView mapView;

    String userID;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업 수행
        v = inflater.inflate(R.layout.activity_choose, container, false);

        foodView = (LinearLayout) v.findViewById(R.id.foodView);
        souView = (LinearLayout) v.findViewById(R.id.souView);
        transView = (LinearLayout) v.findViewById(R.id.transView);

        context = container.getContext();

        Bundle bundle = getArguments();

        userID = bundle.getString("userID");

        Log.d("user ID is : ", "" + userID);

        /*Fragment내에서는 mapView로 지도를 실행*/
        mapView = (MapView)v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this); // 비동기적 방식으로 구글 맵 실행



        foodView.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(getActivity(), AddFoodActivity.class);
                startActivity(intent1);

            }
        });

        souView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new SearchAroundSouv())
                        .commit();
            }
        });

        transView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InputTransportActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
        return v;
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
