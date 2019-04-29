package com.example.marketprice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView = null;
    private String API_KEY = "AIzaSyClJpA5YRWaLkc7hXplUolDaCxFXtasK1k";
    int AUTOCOMPLETE_REQUEST_COSE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);



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
            Places.initialize(getContext(),API_KEY);
            PlacesClient placesClient = Places.createClient(getContext());

            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(getContext());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_COSE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_mapfragment, container, false);


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
        LatLng CAU = new LatLng(33.837812, -117.918424);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(CAU);

        markerOptions.title("서울");

        markerOptions.snippet("수도");

        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAU,15));

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
