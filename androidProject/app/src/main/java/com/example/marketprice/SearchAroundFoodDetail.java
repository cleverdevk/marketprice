package com.example.marketprice;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchAroundFoodDetail extends Fragment implements OnMapReadyCallback {

    View v;

    ImageView img;
    TextView Name, Price;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.search_food_detail, container, false);

        img = (ImageView)v.findViewById(R.id.img);
        Name = (TextView)v.findViewById(R.id.name);
        Price = (TextView)v.findViewById(R.id.price);


        Bundle bundle = getArguments();

        if(bundle !=null) {
            img.setImageResource(bundle.getInt("img"));
            Name.setText(bundle.getString("Name"));
            Price.setText(bundle.getString("Price"));
        }

        return v;
    }

    @Override
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
