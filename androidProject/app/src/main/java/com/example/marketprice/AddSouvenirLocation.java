package com.example.marketprice;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AddSouvenirLocation extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        Button.OnClickListener {

    private Button searchByButton;

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap = null;
    private GoogleApiClient googleApiClient = null;
    private LocationRequest locationRequest;
    private Location location;

    private Marker currentMarker = null;

    //    private static final int Request_User_Location_Code = 99;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 100000; //1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500000; // 0.5초
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private double latitide, longitude;
    private int ProximityRadius = 1000;

    private static String auto_address = null;
    private static Double auto_lag = null;
    private static Double auto_lng = null;

    boolean needRequest = false;

    AutocompleteSupportFragment autocompleteFragment = null;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    private FragmentActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    String userID = "";

    Location mCurrentLocation;
    LatLng currentPosition;

    private View mLayout;

    private Geocoder geocoder;
    Marker previous_marker = null;

    private Geocoder getGeocoder() {
        if(geocoder == null) {
            geocoder = new Geocoder(this);
        }

        return geocoder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_addsouvenirlocation);

        Intent intentID = getIntent();
        userID = intentID.getExtras().getString("userID");

        mLayout = findViewById(R.id.layout_main);
        mActivity = this;

        searchByButton = (Button) findViewById(R.id.searchBtn);

        searchByButton.setOnClickListener(this);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//         지도 띄우기
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),"AIzaSyClJpA5YRWaLkc7hXplUolDaCxFXtasK1k");
        }

        autocompleteFragment = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());

                // .latitude .longitude로 불러오기 가능

                final LatLng selected = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                currentPosition = selected;
                auto_address = place.getName();
                auto_lag = place.getLatLng().latitude;
                auto_lng = place.getLatLng().longitude;
                latitide = auto_lag;
                longitude = auto_lng;

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(selected);
                        markerOptions.title(place.getName());
                        googleMap.addMarker(markerOptions);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(selected));
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


    }



    public void onClick(View v)
    {

        String store = "store";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();


        switch (v.getId())
        {
            // 주변 검색 버튼 눌렀을 때
            case R.id.searchBtn:
                mMap.clear();
//                String url = getUrl(location.getLatitude(),location.getLongitude(), restaurant);

                String url = getUrl(latitide, longitude, store);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, "Searching for Nearby Store...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Showing Nearby Store...", Toast.LENGTH_SHORT).show();


                break;
        }
    }

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.e("LocationCallback", "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocation = location;
            }

        }

    };

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {

            Log.e("startLocationUpdates :",  "call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.e("startLocationUpdates", "퍼미션 안가지고 있음");
                return;
            }

            Log.e("startLocationUpdates" , ": call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }
    }

    private String getUrl(double latitide, double longitude, String nearbyPlace)
    {
        Log.e("getUrl", "getUrl" );

        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitide + "," + longitude);
//        googleURL.append("location=" + 37.56 + "," + 126.97);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyDXaFE3A85utTOpVFEGObMGBh_57KmtMKs");

        Log.e("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("onMapReady", "onMapReady");
        mMap = googleMap;

        double lat = 37.505135;
        double lng = 126.957096;
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAU, 15));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            lng = lastKnownLocation.getLongitude();
            lat = lastKnownLocation.getLatitude();
            Log.d("[INBAE]", "longtitude=" + lng + ", latitude=" + lat);
            LatLng current = new LatLng(lat,lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
        }


        googleMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> address = null;
                try {
                    address = getGeocoder().getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch ( IOException e) {
                    Log.e("GeocoderError", e.getMessage());
                }

                if(previous_marker != null)
                    previous_marker.remove();

                MarkerOptions markerOptions = new MarkerOptions();
                String title = getTitleFromAddress(address.get(0));

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                if(address != null) {
                    markerOptions.title(title);
                }

                markerOptions.position(latLng); //마커위치설정
                markerOptions.snippet(title);
                System.out.println(address.get(0).toString());

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));   // 마커생성위치로 이동

                previous_marker = mMap.addMarker(markerOptions); //마커 생성

            }
        });

        googleMap.setOnInfoWindowClickListener(infoWindowClickListener);




    }

    // Address 객체에서 주소 가져오기
    private String getTitleFromAddress(Address address) {
        int MAX_LENGTH = 1;
        int addressLineIndex = address.getMaxAddressLineIndex();

        if(addressLineIndex == -1) {
            return "";
        }

        if(addressLineIndex < MAX_LENGTH) {
            MAX_LENGTH = addressLineIndex;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= MAX_LENGTH; i++) {
            sb.append(address.getAddressLine(i));
        }
        return sb.toString();
    }

    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerId = marker.getId();
            String title = marker.getTitle();
            String address = marker.getSnippet();
            double lag = marker.getPosition().latitude;
            double lng = marker.getPosition().longitude;

            Toast.makeText(AddSouvenirLocation.this, "정보창 클릭 Address : "+title, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AddSouvenirLocation.this, AddSouvenirActivity.class);

            Log.e("위도", Double.toString(lag));
            Log.e("WindowClickListener","onClick");

            intent.putExtra("address", address);
            intent.putExtra("lat", lag);
            intent.putExtra("lon", lng);

            if(address == null){
                Log.d("에러 :", "error");
            }else{
                Log.d("주소:", markerId);

            }
            setResult(RESULT_OK,intent);
            finish();
        }
    };


    @Override
    protected void onStart(){
        super.onStart();

        Log.e("onStart", "onStart");

        if (checkPermission()) {

//            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }
    }



    @Override
    protected void onStop(){
        super.onStop();

        if (mFusedLocationClient != null) {

            Log.e("onStop :", "call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getCurrentAddress(LatLng latlng) {
        Log.e("getCurrentAddress" ,"111");

        //지오코더... GPS를 주소로 변환
        List<Address> addresses;

        try {

            addresses = getGeocoder().getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public boolean checkLocationServicesStatus() {
        Log.e("checkLcoationService : ","status");
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        Log.e("setCurrentLocation", "location");
        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);


    }

    public void setDefaultLocation() {

        Log.e("setDefaultLocation : ", "default");

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(35.17, 129.07);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);


    }

//    @Override
//    public void onLocationChanged(Location location){
//
//        Log.e("onLocationChanged : ", "222");
//
//
//        latitide = location.getLatitude();
//        longitude = location.getLongitude();
//
//        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
//
//        String markerTitle = getCurrentAddress(currentPosition);
//        String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) +"경도: "+ String.valueOf(location.getLongitude());
//
//        setCurrentLocation(location, markerTitle, markerSnippet);
//
//        mCurrentLocation = location;
//    }
//
//    @Override
//    public void onConnected(Bundle connectionHint){
//        Log.e("onConnected : ", "333");
//
//        if(mRequestingLocationUpdates == false){
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//
//                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//
//                if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED){
//                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//                }else{
//                    startLocationUpdates();
//                    mMap.setMyLocationEnabled(true);
//                }
//            }else{
//                startLocationUpdates();
//                mMap.setMyLocationEnabled(true);
//            }
//        }
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        setDefaultLocation();
    }

//    @Override
//    public void onConnectionSuspended(int cause) {
//
//
//        Log.e("onConnectionSuspended :", "connection");
//        if (cause == CAUSE_NETWORK_LOST){
////            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
////                    "connection lost.  Cause: network lost.");
//        }else if (cause == CAUSE_SERVICE_DISCONNECTED){
////            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
////                    "connection lost.  Cause: service disconnected");
//        }
//    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        Log.e("checkPermission :", "permission");

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if ( googleApiClient.isConnected() == false) {

                    Log.e("onRequestPermissions :", " request");
                    googleApiClient.connect();
                }



            } else {

                checkPermission();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        Log.e("showDialog", "ForLocationServiceSetting");

        AlertDialog.Builder builder = new AlertDialog.Builder(AddSouvenirLocation.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "2333");

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

//                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }


}