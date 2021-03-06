package com.example.marketprice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragmentForNewAccounting extends Fragment implements OnMapReadyCallback {
    private MapView mapView = null;

    public MapFragmentForNewAccounting() {

    }

    public interface OnMyListner2{
        void onReceivedData(LatLng data, GoogleMap googleMap);
    }

    private MapFragmentForNewAccounting.OnMyListner2 mOnMyListener2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null && getActivity() instanceof MapFragmentForNewAccounting.OnMyListner2){
            mOnMyListener2 = (MapFragmentForNewAccounting.OnMyListner2) getActivity();

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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.d("[INBAE]", "RESUMED!!!");

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
        if(mOnMyListener2 != null)
            mOnMyListener2.onReceivedData(new LatLng(0,0), googleMap);

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
                if(mOnMyListener2 != null)
                    mOnMyListener2.onReceivedData(new LatLng(lat,lng), googleMap);
            }
        });
    }
}
