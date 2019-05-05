package com.example.marketprice.SearchAround;

import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.marketprice.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchAroundFoodDetail extends Fragment implements OnMapReadyCallback {

    View v;

    ImageView img;
    TextView Name, Price, textAddr, textReview;
    RatingBar rating;
    List<Address> addresses;

    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.search_food_detail, container, false);

        img = (ImageView)v.findViewById(R.id.img);
        Name = (TextView)v.findViewById(R.id.name);
        Price = (TextView)v.findViewById(R.id.price);
        textAddr = (TextView)v.findViewById(R.id.textAddr);
        textReview = (TextView)v.findViewById(R.id.textReview);
        rating = (RatingBar)v.findViewById(R.id.rating);

        Bundle bundle = getArguments();

        Geocoder geocoder = new Geocoder (getContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(bundle.getFloat("lat"), bundle.getFloat("lng"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0);

        if(bundle !=null) {
            Picasso.with(getContext()).load(bundle.getString("img")).into(img);;
            Name.setText(bundle.getString("Name"));
            Price.setText(bundle.getString("Price"));
            textAddr.setText(address);
            textReview.setText(bundle.getString("content"));
            rating.setRating(bundle.getFloat("rate"));
        }

        //mapview로 데이터
        mapView = (MapView)v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this); // 비동기적 방식으로 구글 맵 실행



        mapView.getMapAsync(this); // 비동기적 방식으로 구글 맵 실행

        return v;
    }


    @Override
    public void onMapReady(final GoogleMap map){

        Bundle bundle = getArguments();

        LatLng Food = new LatLng(bundle.getFloat("lat"), bundle.getFloat("lng"));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Food);
        markerOptions.title(bundle.getString("Name"));
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Food,1));
        map.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

}
