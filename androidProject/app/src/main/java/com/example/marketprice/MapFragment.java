package com.example.marketprice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.marketprice.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView = null;

    Double LatFromActivity, LngFromActivity;
    String NameFromActivity;




    public MapFragment() {

    }

    public interface OnMyListner{
        void onReceivedData(LatLng data, GoogleMap googleMap);
    }

    private  OnMyListner mOnMyListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null && getActivity() instanceof OnMyListner){
            mOnMyListener = (OnMyListner) getActivity();

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_mapfragment, container, false);


        if(getArguments() != null) {
            LatFromActivity = getArguments().getDouble("lat");
            LngFromActivity = getArguments().getDouble("lng");
            NameFromActivity = getArguments().getString("name");
            Log.d("[INBAE]", LatFromActivity +", " + LngFromActivity + NameFromActivity);
        }
        else{
            Log.d("[INBAE]","NULL!!!!!!!!!!!!");
        }
        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.getMapAsync(this);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if(mOnMyListener != null)
            mOnMyListener.onReceivedData(new LatLng(0,0), googleMap);

        Log.d("[INBAE]","OnMapReady Called!!!");
        if(LatFromActivity!=null && LngFromActivity != null && NameFromActivity != null) {
            LatLng fromSearch = new LatLng(LatFromActivity, LngFromActivity);
            Log.d("[INBAE]", "LATLNG : " + LatFromActivity + ", " + LngFromActivity);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(fromSearch);
            markerOptions.title(NameFromActivity);
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromSearch, 15));
            mapView.getMapAsync(this);
        }
        //LatLng CAU = new LatLng(33.837812, -117.918424);
        //MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(CAU);
        //markerOptions.title("서울");
        //markerOptions.snippet("수도");
        //googleMap.addMarker(markerOptions);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAU,15));



        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                MarkerOptions mOption = new MarkerOptions();
                mOption.title("마커 좌표");
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;


                mOption.snippet("hello");
                mOption.position(new LatLng(lat,lng));
                googleMap.addMarker(mOption);
                if(mOnMyListener != null)
                    mOnMyListener.onReceivedData(new LatLng(lat,lng), googleMap);
                Toast.makeText(getActivity(),"현재 위치를 출발지/도착지로 지정하려면 버튼을 누르세요.",Toast.LENGTH_SHORT).show();
            }
        });
    }





}
