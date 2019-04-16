//package com.example.marketprice;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Looper;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.Toast;
//
////import noman.googleplaces.NRPlaces;
////import noman.googleplaces.Place;
////import noman.googleplaces.PlaceType;
////import noman.googleplaces.PlacesException;
////import noman.googleplaces.PlacesListener;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.net.PlacesClient;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//
//public class AddFoodLocation extends AppCompatActivity
//        implements OnMapReadyCallback,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener, PlacesListener {
//
//    private FusedLocationProviderClient mFusedLocationClient;
//    private LocationRequest locationRequest;
//    private Location location;
//
//    private GoogleApiClient mGoogleApiClient = null;
//    private GoogleMap mGoogleMap = null;
//    private Marker currentMarker = null;
//
//    private static final String TAG = "googlemap_ex";
//    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
//    private static final int UPDATE_INTERVAL_MS = 1000; //1초
//    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
//    private static final int PERMISSIONS_REQUEST_CODE = 100;
//    boolean needRequest = false;
//
//    // 앱을 실행하기 위해 필요한 퍼미션을 정의
//    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
//
//
//    private AppCompatActivity mActivity;
//    boolean askPermissionOnceAgain = false;
//    boolean mRequestingLocationUpdates = false;
//
//    Location mCurrentLocation;
//    LatLng currentPosition;
//
//    private View mLayout;
//
//    List<Marker> previous_marker = null;
//
//
//    private Button search;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.activity_addfoodlocation);
//
//        Log.d(TAG, "onCreate");
//
//        mLayout = findViewById(R.id.layout_main);
//
//        mActivity = this;
//
//
//        locationRequest = new LocationRequest()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(UPDATE_INTERVAL_MS)
//                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(locationRequest);
//
////        mGoogleApiClient = new GoogleApiClient.Builder(this)
////                .addConnectionCallbacks(this)
////                .addOnConnectionFailedListener(this)
////                .addApi(LocationServices.API)
////                .build();
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // 지도 띄우기
//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
////         마커
//        previous_marker = new ArrayList<Marker>();
//
////         주변 검색 버튼 클릭 시
//        search = (Button)findViewById(R.id.searchBtn);
//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPlaceInformation(currentPosition);
//            }
//        });
//
//    }
//    LocationCallback locationCallback = new LocationCallback(){
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//
//            List<Location> locationList = locationResult.getLocations();
//
//            if (locationList.size() > 0) {
//                location = locationList.get(locationList.size() - 1);
//                //location = locationList.get(0);
//
//                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
//
//
//                String markerTitle = getCurrentAddress(currentPosition);
//                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
//                        + " 경도:" + String.valueOf(location.getLongitude());
//
//                Log.d(TAG, "onLocationResult : " + markerSnippet);
//
//
//                //현재 위치에 마커 생성하고 이동
//                setCurrentLocation(location, markerTitle, markerSnippet);
//                mCurrentLocation = location;
//            }
//
//        }
//
//    };
//
//    private void startLocationUpdates() {
//        if (!checkLocationServicesStatus()) {
//
//            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
//            showDialogForLocationServiceSetting();
//        }else {
//
//            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION);
//            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION);
//
//            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
//                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {
//
//                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
//                return;
//            }
//
//            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
//
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//
//            if (checkPermission())
//                mGoogleMap.setMyLocationEnabled(true);
//
//        }
//    }
//
//    @Override
//    public void onMapReady(final GoogleMap map){
//        Log.d(TAG, "onMapReady :");
//
//
//        mGoogleMap = map;
//
//        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
//        //지도의 초기위치를 서울로 이동
//        setDefaultLocation();
//
//        //런타임 퍼미션 처리
//        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
//        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION);
//
//
//        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
//                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
//
//            // 2. 이미 퍼미션을 가지고 있다면
//            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
//            startLocationUpdates(); // 3. 위치 업데이트 시작
//
//        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
//
//            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
//
//                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
//                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
//                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//
//                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
//                        ActivityCompat.requestPermissions( AddFoodLocation.this, REQUIRED_PERMISSIONS,
//                                PERMISSIONS_REQUEST_CODE);
//                    }
//                }).show();
//
//
//            } else {
//                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
//                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
//                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
//                        PERMISSIONS_REQUEST_CODE);
//            }
//
//        }
//
//        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                Log.d( TAG, "onMapClick :");
//            }
//        });
//
//        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
//            @Override
//            public void onInfoWindowClick(Marker marker){
//                Intent intent = new Intent(getBaseContext(), AddFoodActivity.class);
//
//                String title = marker.getTitle();
//                String address = marker.getSnippet();
//
////                intent.putExtra("title", title);
//                intent.putExtra("address", address);
//
//                startActivity(intent);
//            }
//        });
//
//    }
//
//    @Override
//    protected void onStart(){
//        super.onStart();
//
//        Log.d(TAG, "onStart");
//
//        if (checkPermission()) {
//
//            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
//            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//
//            if (mGoogleMap!=null)
//                mGoogleMap.setMyLocationEnabled(true);
//
//        }
//    }
//
//    @Override
//    protected void onStop(){
//        super.onStop();
//
//        if (mFusedLocationClient != null) {
//
//            Log.d(TAG, "onStop : call stopLocationUpdates");
//            mFusedLocationClient.removeLocationUpdates(locationCallback);
//        }
//    }
//
//    public String getCurrentAddress(LatLng latlng) {
//
//        //지오코더... GPS를 주소로 변환
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//        List<Address> addresses;
//
//        try {
//
//            addresses = geocoder.getFromLocation(
//                    latlng.latitude,
//                    latlng.longitude,
//                    1);
//        } catch (IOException ioException) {
//            //네트워크 문제
//            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
//            return "지오코더 서비스 사용불가";
//        } catch (IllegalArgumentException illegalArgumentException) {
//            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
//            return "잘못된 GPS 좌표";
//
//        }
//
//
//        if (addresses == null || addresses.size() == 0) {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
//            return "주소 미발견";
//
//        } else {
//            Address address = addresses.get(0);
//            return address.getAddressLine(0).toString();
//        }
//
//    }
//
//    public boolean checkLocationServicesStatus() {
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
//        if (currentMarker != null) currentMarker.remove();
//
//
//        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentLatLng);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);
//
//
//        currentMarker = mGoogleMap.addMarker(markerOptions);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
//        mGoogleMap.moveCamera(cameraUpdate);
//
//
//    }
//
//    public void setDefaultLocation() {
//
//        //디폴트 위치, Seoul
//        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
//        String markerTitle = "위치정보 가져올 수 없음";
//        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";
//
//
//        if (currentMarker != null) currentMarker.remove();
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(DEFAULT_LOCATION);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        currentMarker = mGoogleMap.addMarker(markerOptions);
//
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
//        mGoogleMap.moveCamera(cameraUpdate);
//
//
//    }
//
////    @Override
////    public void onResume(){
////        super.onResume();
////        if(mGoogleApiClient.isConnected()){
////            if(!mRequestingLocationUpdates) startLocationUpdates();
////        }
////
////        if(askPermissionOnceAgain){
////            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
////                askPermissionOnceAgain = false;
////                checkPermissions();
////            }
////        }
////    }
//
//
////
////    private void stopLocationUpdates() {
////
//////        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
////        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
////        mRequestingLocationUpdates = false;
////    }
//
//
//    @Override
//    public void onLocationChanged(Location location){
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
//
//
//    @Override
//    public void onConnected(Bundle connectionHint){
//        if(mRequestingLocationUpdates == false){
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//
//                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//
//                if(hasFineLocationPermission == PackageManager.PERMISSION_DENIED){
//                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//                }else{
//                    startLocationUpdates();
//                    mGoogleMap.setMyLocationEnabled(true);
//                }
//            }else{
//                startLocationUpdates();
//                mGoogleMap.setMyLocationEnabled(true);
//            }
//        }
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult){
//        setDefaultLocation();
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//
////        Log.d(TAG, "onConnectionSuspended");
//        if (cause == CAUSE_NETWORK_LOST){
////            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
////                    "connection lost.  Cause: network lost.");
//        }else if (cause == CAUSE_SERVICE_DISCONNECTED){
////            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
////                    "connection lost.  Cause: service disconnected");
//        }
//    }
//
//
//    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
//    private boolean checkPermission() {
//
//        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION);
//
//
//
//        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
//                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
//            return true;
//        }
//
//        return false;
//
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int permsRequestCode,
//                                           @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//
//        if (permsRequestCode
//                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
//
//            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//
//            if (permissionAccepted) {
//
//
//                if ( mGoogleApiClient.isConnected() == false) {
//
////                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
//                    mGoogleApiClient.connect();
//                }
//
//
//
//            } else {
//
//                checkPermission();
//            }
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    private void showDialogForPermission(String msg) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodLocation.this);
//        builder.setTitle("알림");
//        builder.setMessage(msg);
//        builder.setCancelable(false);
//        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                ActivityCompat.requestPermissions(mActivity,
//                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            }
//        });
//
//        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                finish();
//            }
//        });
//        builder.create().show();
//    }
//
//    private void showDialogForPermissionSetting(String msg) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodLocation.this);
//        builder.setTitle("알림");
//        builder.setMessage(msg);
//        builder.setCancelable(true);
//        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//
//                askPermissionOnceAgain = true;
//
//                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                        Uri.parse("package:" + mActivity.getPackageName()));
//                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
//                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mActivity.startActivity(myAppSettings);
//            }
//        });
//        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                finish();
//            }
//        });
//        builder.create().show();
//    }
//
//    //여기부터는 GPS 활성화를 위한 메소드들
//    private void showDialogForLocationServiceSetting() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddFoodLocation.this);
//        builder.setTitle("위치 서비스 비활성화");
//        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
//                + "위치 설정을 수정하실래요?");
//        builder.setCancelable(true);
//        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                Intent callGPSSettingIntent
//                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
//            }
//        });
//        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//            }
//        });
//        builder.create().show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//
//            case GPS_ENABLE_REQUEST_CODE:
//
//                //사용자가 GPS 활성 시켰는지 검사
//                if (checkLocationServicesStatus()) {
//                    if (checkLocationServicesStatus()) {
//
//                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
//
//
//                        needRequest = true;
//
//                        return;
//                    }
//                }
//
//                break;
//        }
//    }
//
//
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//    @Override
//    public void onProviderDisabled(String provider) {}
//
//    @Override
//    public void onProviderEnabled(String provider) {}
//
//
//    @Override
//    public void onPlacesFailure(PlacesException e) {
//
//    }
//
//    @Override
//    public void onPlacesStart() {
//
//    }
//
//
//    @Override
//    public void onPlacesSuccess(final List<Place> places) {
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                for (noman.googleplaces.Place place : places) {
//                    LatLng latLng
//                            = new LatLng(place.getLatitude()
//                            , place.getLongitude());
//
//                    String markerSnippet = getCurrentAddress(latLng);
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(latLng);
//                    markerOptions.title(place.getName());
//                    markerOptions.snippet(markerSnippet);
//                    Marker item = mGoogleMap.addMarker(markerOptions);
//                    previous_marker.add(item);
//                }
//
//                //중복 마커 제거
//                HashSet<Marker> hashSet = new HashSet<Marker>();
//                hashSet.addAll(previous_marker);
//                previous_marker.clear();
//                previous_marker.addAll(hashSet);
//            }
//
//        });
//
//
//    }
//
//    @Override
//    public void onPlacesFinished() {
//
//    }
//
//
//    public void showPlaceInformation(LatLng location)
//    {
//        mGoogleMap.clear();//지도 클리어
//
//        if (previous_marker != null)
//            previous_marker.clear();//지역정보 마커 클리어
//
//        new NRPlaces.Builder()
//                .listener(AddFoodLocation.this)
//                .key("AIzaSyDXaFE3A85utTOpVFEGObMGBh_57KmtMKs")
//                .latlng(location.latitude, location.longitude)//현재 위치
//                .radius(500) //500 미터 내에서 검색
//                .type(PlaceType.RESTAURANT) //음식점
//                .build()
//                .execute();
//    }
//
//}
