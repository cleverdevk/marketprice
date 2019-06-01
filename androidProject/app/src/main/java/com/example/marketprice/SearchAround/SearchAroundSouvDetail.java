package com.example.marketprice.SearchAround;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchAroundSouvDetail extends Fragment implements OnMapReadyCallback {
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

        double lat = 37.505135;
        double lng = 126.957096;
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAU, 15));

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }
        final LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLongitude();
            lat = lastKnownLocation.getLatitude();
            Log.d("[INBAE]", "longtitude=" + lng + ", latitude=" + lat);
            LatLng current = new LatLng(lat,lng);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
        }


        map.setMyLocationEnabled(true);
    }
}
