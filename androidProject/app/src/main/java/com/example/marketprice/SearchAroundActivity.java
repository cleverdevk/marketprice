package com.example.marketprice;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.AppComponentFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

public class SearchAroundActivity extends Fragment implements OnMapReadyCallback{
    View v;

    LinearLayout foodView, souView, transView;
    AutocompleteSupportFragment autocompleteFragment;

    private Context context;

    private MapView mapView;

    LatLng myLocation;
//    final LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업 수행
        v = inflater.inflate(R.layout.search_around, container, false);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyC9fsk433XYbQqE8X1mMEkFtij6G2tGRlk");
        }

        myLocation = new LatLng(37.56, 126.97);

        autocompleteFragment = (AutocompleteSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

//        autocompleteFragment = new AutocompleteSupportFragment();
//
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        ft.replace(R.id.autocomplete_fragment, autocompleteFragment);
//        ft.commit();


        foodView = (LinearLayout) v.findViewById(R.id.foodView);
        souView = (LinearLayout) v.findViewById(R.id.souView);
        transView = (LinearLayout) v.findViewById(R.id.transView);

        context = container.getContext();

        /*Fragment내에서는 mapView로 지도를 실행*/
        mapView = (MapView)v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this); // 비동기적 방식으로 구글 맵 실행

//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());

                // .latitude .longitude로 불러오기 가능

                final LatLng selected = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                myLocation = selected;

                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(selected);
                        markerOptions.title(place.getName());
                        googleMap.addMarker(markerOptions);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selected,1));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                    }
                });

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });



        foodView.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "food", Toast.LENGTH_SHORT).show();

                Bundle data = new Bundle();

                data.putDouble("lat", myLocation.latitude);
                data.putDouble("lng", myLocation.longitude);

                SearchAroundFood fragment2 = new SearchAroundFood();
                fragment2.setArguments(data);

                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment2)
                        .commit();
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

                Bundle data = new Bundle();

                data.putDouble("lat", myLocation.latitude);
                data.putDouble("lng", myLocation.longitude);

                SearchAroundTransportation fragment3 = new SearchAroundTransportation();
                fragment3.setArguments(data);

                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment3)
                        .commit();
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
