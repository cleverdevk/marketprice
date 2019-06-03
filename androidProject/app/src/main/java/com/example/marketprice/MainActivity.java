package com.example.marketprice;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.marketprice.Accounts.AccountingMenuFragment;
import com.example.marketprice.Accounts.MyHistoryActivity;
import com.example.marketprice.SearchAround.SearchAroundActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String[] mTitles; // 주변검색, 가계부, 나의 기록
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mflContainer;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private Button addInfo;
    private int current_position = 0;

    private LocationManager locationManager;
    double mLatitude, mLongitude;

    Boolean isGPSEnabled = false;
    Boolean isNetworkEnabled = false;

    Double lat;
    Double lng;

    String strID;

    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("MarketPrice");

        // GPS 정보 받아오기

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//            }
//        }
//
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        lng = location.getLongitude();
//        lat = location.getLatitude();
//
//        Log.d("위도 : ", "" + location.getLongitude());
//        Log.d("경도 : ", "" + location.getLatitude());
//
//        // GPS 프로바이더 사용가능여부
//        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // 네트워크 프로바이더 사용가능여부
//        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);



        // GPS 정보 받아오기 종료

        addInfo = (Button)findViewById(R.id.addInfo);

        mTitle = mDrawerTitle = getTitle();
        mTitles = getResources().getStringArray(R.array.title_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); //activity_main 자체
        mDrawerList = (ListView) findViewById(R.id.left_drawer);  //activity_main 안에 Listview
        mflContainer = (FrameLayout)findViewById(R.id.content_frame);

        Intent LoginIntent = getIntent();
        strID = LoginIntent.getExtras().getString("userID");


        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
            finish();
        }
        if(Build.VERSION.SDK_INT >=23){
            //권한이 없는 경우
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION} , 1);
            }
            //권한이 있는 경우
            else{
                requestMyLocation();
            }


        }


//        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.content_frame, new PlanetFragment());
        fragTransaction.add(R.id.map, new MapFragmentForMain());
        fragTransaction.commit();




        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mTitles)); //drawer_list_item 은 text1 이름의 textview 하나 갖고 잇음.
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
//                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle); //mDrawerTitle = MarketPrice
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


//        if (savedInstanceState == null) {
//            selectItem(0);
//            // Fragment for google map
////            FragmentManager fragmentManager = getSupportFragmentManager();
////            SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
////            mapFragment.getMapAsync(this);
//        }
    }

    //새로운 정보 추가 버튼 클릭시 발생 이벤트
    public void newInfoClicked(View v){

        Bundle args = new Bundle();
        args.putString("userID", this.strID);

        AddMenuActivity fragment2 = new AddMenuActivity();
        fragment2.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment2)
                .commit();
    }

    //권한 요청후 응답 콜백
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //ACCESS_COARSE_LOCATION 권한
        if(requestCode==1){
            //권한받음
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                requestMyLocation();
            }
            //권한못받음
            else{
                Toast.makeText(this, "권한없음", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //나의 위치 요청
    public void requestMyLocation(){

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        //요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
    }

    //위치정보 구하기 리스너
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            //나의 위치를 한번만 가져오기 위해
            locationManager.removeUpdates(locationListener);

            //위도 경도
            mLatitude = location.getLatitude();   //위도
            mLongitude = location.getLongitude(); //경도


            Bundle bundle = new Bundle();
            bundle.putDouble("lat",mLatitude);
            bundle.putDouble("lng",mLongitude);
            MapFragmentForMain mapFragmentForMain = new MapFragmentForMain();

            FragmentManager fragmentManager = getSupportFragmentManager();
            mapFragmentForMain.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.map,mapFragmentForMain);





            //맵생성
            //SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            //콜백클래스 설정
            //mapFragment.getMapAsync(MainActivity.this);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { Log.d("gps", "onStatusChanged"); }

        @Override
        public void onProviderEnabled(String provider) { Log.d("INBAE","OnProviderEnabled Called!"); }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    //맵이 사용할 준비가 되었을 때 호출되는 메소드
    @Override
    public void onMapReady(final GoogleMap map){
        LatLng SEOUL = new LatLng(37.56, 126.97);
        LatLng MyPosition = new LatLng(mLatitude,mLongitude);
        //MarkerOptions markerOptions = new MarkerOptions();
        //markerOptions.position(SEOUL);
        //markerOptions.title("서울");
        //markerOptions.snippet("한국의 수도");
        //map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyPosition,1));
        map.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    /* invalidateOptionsMenu() 호출할 때 마다 호출된다. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            selectItem(position);
        }
    }

    //    Main content view에서 fragments 바꾸기
    private void selectItem(int position) {

        // 새로운 fragment 생성하고 position 기반으로 보여 줄 planet 명시
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);


        // 기존 fragment 교체함으로써, fragment insert
        FragmentManager fragmentManager = getSupportFragmentManager();
//        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        switch (position){
            case 0:

                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new SearchAroundActivity())
                        .commit();
                current_position = 0;
                break;
            case 1:

                AccountingMenuFragment accountingMenuFragment = new AccountingMenuFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",strID);
                accountingMenuFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, accountingMenuFragment)
                        .commit();
                current_position = 1;
                break;
            case 2:

                Intent intent = new Intent(this, MyHistoryActivity.class);
                intent.putExtra("id",strID);
                startActivity(intent);
                current_position = 2;
                break;

            case 3:

                Bundle args = new Bundle();
                args.putString("userID", this.strID);

                AddMenuActivity fragment2 = new AddMenuActivity();
                fragment2.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment2)
                        .commit();
                current_position = 3;
                break;
        }


        // 선택한 아이템을 표시하고, 타이틀 업데이트하고, drawer 닫기.
        mDrawerList.setItemChecked(position, true);
        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title){
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        getSupportActionBar().setTitle("MarketPrice");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */

    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//            View rootView = inflater.inflate(R.layout.fragment_basic, container, false);
//            int i = getArguments().getInt(ARG_PLANET_NUMBER);
//            String planet = getResources().getStringArray(R.array.title_array)[i];


//            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getActivity().getPackageName());
//            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
//            getActivity().setTitle(planet);

//            return rootView;
            return inflater.inflate(R.layout.fragment_basic, container, false);
        }
    }
}

