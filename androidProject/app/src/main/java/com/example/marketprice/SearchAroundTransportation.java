package com.example.marketprice;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class SearchAroundTransportation extends FragmentActivity
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private View v;

    LatLng currentPosition = new LatLng(0,0);

    double myLat;
    double myLng;
//    private MapView mapView = null;
    private GoogleMap mMap = null;
    private GoogleApiClient googleApiClient = null;
    private Marker currentMarker = null;
    private Location location;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitide, longitude;

//    private TransportListViewAdapter adapter;

    private Button searchBtn;
    private Spinner spinner_field;
    private ArrayAdapter<String> item;

    boolean needRequest = false;

    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000; //1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private static String depart_address = null;
    private static String arrival_address = null;
    private static Double depart_lag = null;
    private static Double depart_lng = null;
    private static Double arrival_lag = null;
    private static Double arrival_lng = null;

    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    Location mCurrentLocation;
    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    AutocompleteSupportFragment autocompleteFragment_depart;
    AutocompleteSupportFragment autocompleteFragment_arrive;


    private Geocoder geocoder;
    Marker previous_marker = null;

    private Geocoder getGeocoder() {
        if(geocoder == null) {
            geocoder = new Geocoder(this);
        }

        return geocoder;
    }

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.search_transport);

        v = findViewById(R.id.layout_main);


        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        Log.d("my location is : ", "" + myLat + ", " + myLng);


        spinner_field = (Spinner) findViewById(R.id.spinner_field);
        String[] str = getResources().getStringArray(R.array.spinnerArray);
        item = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, str);

        item.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(item);

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), spinner_field.getSelectedItem().toString() + "가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        autocompleteFragment_depart = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        autocompleteFragment_arrive = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);

        autocompleteFragment_depart.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment_arrive.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment_depart.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment_arrive.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment_depart.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());

                // .latitude .longitude로 불러오기 가능

                final LatLng selected = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                currentPosition = selected;
                depart_address = place.getName();
                depart_lag = place.getLatLng().latitude;
                depart_lng = place.getLatLng().longitude;

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

        autocompleteFragment_arrive.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());

                // .latitude .longitude로 불러오기 가능

                final LatLng selected = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                currentPosition = selected;
                arrival_address = place.getName();
                arrival_lag = place.getLatLng().latitude;
                arrival_lng = place.getLatLng().longitude;

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

        searchBtn = (Button)v.findViewById(R.id.search_transport_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchAroundTransportationDetail.class);
                intent.putExtra("depart_lag", depart_lag);
                intent.putExtra("depart_lng", depart_lng);
                intent.putExtra("depart_address", depart_address);

                intent.putExtra("arrival_lag", arrival_lag);
                intent.putExtra("arrival_lng", arrival_lng);
                intent.putExtra("arrival_address", arrival_address);

                intent.putExtra("transportation", spinner_field.getSelectedItem().toString());

                startActivity(intent);

            }
        });




        //지도에서 출발지 도착지 받아옴
//        Bundle extras = getIntent().getExtras();
//
//        if(extras == null) {
//
//        }
//        else {
//
//            if(extras.getBoolean("isDepart")) {
//                depart_address = extras.getString("depart_address");
//                depart_lag = extras.getDouble("depart_lag");
//                depart_lng = extras.getDouble("depart_lng");
//                Log.d("받아진 출발주소", depart_address);
//            } else {
//                arrival_address = extras.getString("arrival_address");
//                arrival_lag = extras.getDouble("arrival_lag");
//                arrival_lng = extras.getDouble("arrival_lng");
//                Log.d("받아진 도착주소", arrival_address);
//            }
//
////            departText.setText(depart_address);
////            arrivalText.setText(arrival_address);
//        }
//
//    }
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

    private void startLocationUpdates(){
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


    public void setCurrentLcation(Location location, String markerTitle, String markerSnippet){
        if(currentMarker != null) currentMarker.remove();

        if(location != null){
            //현재 위치의 위도 경도 가져옴
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = this.mMap.addMarker(markerOptions);

            this.mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentMarker = this.mMap.addMarker(markerOptions);

        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));

    }



//    @Override
//    public void onSaveInstanceState(Bundle outState){
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        LatLng SEOUL = new LatLng(37.56, 126.97);
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        googleMap.addMarker(markerOptions);
//
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL,1));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("onMapReady", "onMapReady");
        mMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            // 2. 이미 퍼미션을 가지고 있다면
            startLocationUpdates(); // 3. 위치 업데이트 시작

        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(v, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( SearchAroundTransportation.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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

            Toast.makeText(SearchAroundTransportation.this, "정보창 클릭 Address : "+title, Toast.LENGTH_SHORT).show();
//
//            Intent intent = new Intent(SearchAroundTransportation.this, SearchAroundTransportation.class);
//
//            Log.e("위도", Double.toString(lag));
//            Log.e("WindowClickListener","onClick");
//
//            if(getIntent().getExtras().getBoolean("isDepart")) {
//                intent.putExtra("isDepart", true);
//                intent.putExtra("depart_address", address);
//                intent.putExtra("depart_lag", lag);
//                intent.putExtra("depart_lng", lng);
//            } else {
//                intent.putExtra("isDepart", false);
//                intent.putExtra("arrival_address", address);
//                intent.putExtra("arrival_lag", lag);
//                intent.putExtra("arrival_lng", lng);
//            }
//
//            if(address == null){
//                Log.d("에러 :", "error");
//            }else{
//                Log.d("주소:", markerId);
//
//            }
//            startActivity(intent);


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

    @Override
    public void onLocationChanged(Location location){

        Log.e("onLocationChanged : ", "222");


        latitide = location.getLatitude();
        longitude = location.getLongitude();

        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) +"경도: "+ String.valueOf(location.getLongitude());

        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocation = location;
    }

    @Override
    public void onConnected(Bundle connectionHint){
        Log.e("onConnected : ", "333");

        if(mRequestingLocationUpdates == false){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

                if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }else{
                    startLocationUpdates();
                    mMap.setMyLocationEnabled(true);
                }
            }else{
                startLocationUpdates();
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        setDefaultLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {


        Log.e("onConnectionSuspended :", "connection");
        if (cause == CAUSE_NETWORK_LOST){
//            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
//                    "connection lost.  Cause: network lost.");
        }else if (cause == CAUSE_SERVICE_DISCONNECTED){
//            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
//                    "connection lost.  Cause: service disconnected");
        }
    }

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
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchAroundTransportation.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        Log.e("showDialog", "ForLocationServiceSetting");

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchAroundTransportation.this);
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
