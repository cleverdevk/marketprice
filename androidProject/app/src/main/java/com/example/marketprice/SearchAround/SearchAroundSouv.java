package com.example.marketprice.SearchAround;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marketprice.Adapter.FoodListViewAdapter;
import com.example.marketprice.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class SearchAroundSouv extends Fragment implements OnMapReadyCallback {

    View v;
    private GoogleMap mMap;

    JSONArray results;

    private ListView listView;
    private FoodListViewAdapter adapter;

    //test용 데이터
    private int[] img = {R.drawable.hamburger,R.drawable.beer,R.drawable.transportation};

    private String[] no = new String[100];
    private float[] lat = new float[100];
    private float[] lng = new float[100];
    private String[] imgurl = new String[100];
    private String[] content = new String[100];
    private float[] rate = new float[100];
    private String[] cost = new String[100];
    private String[] name = new String[100];
    private String[] good = new String[100];
    private String[] bad = new String[100];

    public int count = 0;

    private MapView mapView;
    private Button search;

    double myLat;
    double myLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.search_food, container, false);

        //변수 초기화
        adapter = new FoodListViewAdapter();
        listView = (ListView)v.findViewById(R.id.List_view);

        //위도경도 받아오기
        myLat = getArguments().getDouble("lat");
        myLng = getArguments().getDouble("lng");

        Log.d("my location is : ", ""+ myLat + ", " + myLng);

        //어뎁터 할당
        listView.setAdapter(adapter);

        //mapview로 데이터
        mapView = (MapView)v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        SearchAroundSouv.GetData task = new SearchAroundSouv.GetData("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/getSouvList.php",null);
        task.execute();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                LatLng myLocation = new LatLng(myLat, myLng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLocation);
                markerOptions.title("내 위치");

                googleMap.addMarker(markerOptions);

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,1));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            }
        }); // 비동기적 방식으로 구글 맵 실행

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(myLat, myLng, 1);

            Log.d("My Location is :", "" + myLat + ", " + myLng);
            Log.d("Country is :", "" + addresses.get(0).getCountryName());

        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d("Count is : ", "" + count);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Bundle args = new Bundle();

                Log.d("get Item : ", "item is " + listView.getAdapter().getItem(position));


                args.putString("img", imgurl[position]);
                args.putString("Name",  name[position]);
                args.putString("Price", cost[position]);
                args.putFloat("lat", lat[position]);
                args.putFloat("lng", lng[position]);
                args.putFloat("rate", rate[position]);
                args.putString("content", content[position]);



                SearchAroundFoodDetail fragment2 = new SearchAroundFoodDetail();

                fragment2.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment2)
                        .commit();
            }
        });

        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SearchAroundFoodByCondition.class);
//                intent.putExtra("datas", adapter.getListVO());
                Bundle b = new Bundle();
                b.putString("Array",results.toString());
                b.putSerializable("datas",adapter.getListVO());
//                intent.putExtras(b);
//                startActivity(intent);

                SearchAroundSouvByCondition fragment2 = new SearchAroundSouvByCondition();

                fragment2.setArguments(b);
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment2)
                        .commit();

            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

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

    public class GetData extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public GetData (String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;
            //Request 전송
            try {
                URL url = new URL(this.url);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setDefaultUseCaches(false);
                con.setDefaultUseCaches(true);
                con.setDoOutput(true);

                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                StringBuffer buffer = new StringBuffer();
                buffer.append("");                          ///여기다가 파라미터 추가

                OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream(), "utf-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();
                outStream.close();

                if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("message : ", "Connection Failed!");
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

                StringBuilder sb = new StringBuilder();
                String line;

                // Read Server Response

                while ((line = reader.readLine()) != null) {
                    Log.d("TAG", "line : " + line);
                    sb.append(line);
                    break;
                }

                return sb.toString();
            }
            catch (MalformedURLException e) {
                Log.d("catch : ", "MalformedURLException");
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.d("catch : ", "IOException");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("TAG", "onPostExecute" + s);

            super.onPostExecute(s);

            try {

                results = new JSONArray(s);

                Log.d("TAG", "results : " + results + " length :  " + results.length() );

                for (int i = 0; i < results.length(); i++) {

                    JSONObject jObject = results.getJSONObject(i);

                    imgurl[i] = jObject.getString("imageurl");

                    no[i] = jObject.getString("no");
                    lat[i] = Float.parseFloat(jObject.getString("lat"));
                    lng[i] = Float.parseFloat(jObject.getString("lng"));
                    content[i] = jObject.getString("content");
                    rate[i] = Float.parseFloat(jObject.getString("rate"));
                    cost[i] = Integer.toString(jObject.getInt("cost"));
                    name[i] = jObject.getString("name");
                    good[i] = jObject.getString("good");
                    bad[i] = jObject.getString("bad");

                    if (Math.abs(myLat - lat[i]) < 0.15 && Math.abs(myLng - lng[i]) < 0.15){
                        adapter.addVO(imgurl[i], name[i], cost[i]);
                    }

                    adapter.notifyDataSetChanged();

                    count = count + 1;
                    Log.d("Count Changed : ", "to " + count);

                    mapView.getMapAsync(SearchAroundSouv.this);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
