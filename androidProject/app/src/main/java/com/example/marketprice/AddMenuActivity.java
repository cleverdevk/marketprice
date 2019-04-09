package com.example.marketprice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.AppComponentFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddMenuActivity extends Fragment implements OnMapReadyCallback {

    View v;

    LinearLayout foodView, souView, transView;

    private Context context;

    private MapView mapView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업 수행
        v = inflater.inflate(R.layout.activity_choose, container, false);

        foodView = (LinearLayout) v.findViewById(R.id.foodView);
        souView = (LinearLayout) v.findViewById(R.id.souView);
        transView = (LinearLayout) v.findViewById(R.id.transView);

        context = container.getContext();


        /*Fragment내에서는 mapView로 지도를 실행*/
        mapView = (MapView)v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this); // 비동기적 방식으로 구글 맵 실행



        foodView.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "food", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "souvenier", Toast.LENGTH_SHORT).show();

            }
        });

        transView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "transportation", Toast.LENGTH_SHORT).show();
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
