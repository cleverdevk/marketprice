package com.example.marketprice;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marketprice.Adapter.TransportListViewAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchAroundTransportationDetail extends FragmentActivity implements OnMapReadyCallback {

    private View v;

    private GoogleMap mMap = null;
    private GoogleApiClient googleApiClient = null;

    private TextView address_tv;
    private TextView distance_tv;
    private TextView cost_tv;

    private ListView listView;
    private TransportListViewAdapter adapter;

    private String[] start_address = new String[100];
    private String[] end_address = new String[100];
    private String[] distance = new String[100];
    private String[] cost = new String[100];
    private String[] time = new String[100];
    public int count = 0;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private int ProximityRadius = 1000;



    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.search_transport_detail);

        v = findViewById(R.id.layout_main);
        address_tv = (TextView)v.findViewById(R.id.addressName);
        distance_tv = (TextView)v.findViewById(R.id.distance);
        cost_tv = (TextView)v.findViewById(R.id.cost);

        adapter = new TransportListViewAdapter();
        listView = (ListView)v.findViewById(R.id.list_view);

        listView.setAdapter(adapter);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        // 출발지 ~ 목적지
        String depart_address = intent.getExtras().getString("depart_address");
        Double depart_lag = intent.getExtras().getDouble("depart_lag");
        Double depart_lng = intent.getExtras().getDouble("depart_lng");
        String arrival_address = intent.getExtras().getString("arrival_address");
        Double arrival_lag = intent.getExtras().getDouble("arrival_lag");
        Double arrival_lng = intent.getExtras().getDouble("arrival_lng");
        String transportation = intent.getExtras().getString("transportation");
        address_tv.setText(depart_address + " ~ " + arrival_address);

        place1 = new MarkerOptions().position(new LatLng(depart_lag, depart_lng)).title(depart_address);
        place2 = new MarkerOptions().position(new LatLng(arrival_lag, arrival_lng)).title(arrival_address);


//        String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");


        ContentValues values = new ContentValues();

        values.put("depart_address", depart_address);
        values.put("depart_lag", ((double)Math.round(depart_lag*10000000000L))/10000000000L);
        values.put("depart_lng", depart_lng);
        values.put("arrival_address", arrival_address);
        values.put("arrival_lag", arrival_lag);
        values.put("arrival_lng", arrival_lng);
        values.put("transportation", transportation);

        GetData task = new GetData("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/getTransportList.php",values);
        task.execute();


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("error","onMapReady111111");


        mMap.addMarker(place1);
        mMap.addMarker(place2);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(40));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(place1.getPosition()));   // 마커생성위치로 이동

        String basereq = "https://maps.googleapis.com/maps/api/directions/json?unit=metric&";
        String API_KEY = "AIzaSyDXaFE3A85utTOpVFEGObMGBh_57KmtMKs";


        String str_origin = place1.getPosition().latitude + "," + place1.getPosition().longitude;
        String str_dest = place2.getPosition().latitude + "," + place2.getPosition().longitude;
//        String mode = "mode=" + directionMode;


        String REQ = basereq + "origin=" + str_origin + "&destination=" + str_dest + "&key=" + API_KEY;
        Log.d("[BOWON]","REQUSET SQL = " + REQ);

        StringRequest stringRequest = new StringRequest(REQ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                drawPath(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error","error");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

//    private String getUrl(LatLng origin, LatLng dest, String directionMode)
//    {
//        Log.e("getUrl", "getUrl" );
//        String basereq = "https://maps.googleapis.com/maps/api/directions/json?unit=metric&";
//        String API_KEY = "AIzaSyClJpA5YRWaLkc7hXplUolDaCxFXtasK1k";
//
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        String str_dest = "origin=" + dest.latitude + "," + dest.longitude;
//        String mode = "mode=" + directionMode;
//
//
//        String REQ = basereq + "origin=" + str_origin + "&destination=" + str_dest + "&key=" + API_KEY;
//        StringRequest stringRequest = new StringRequest(REQ, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                drawPath(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("error","error");
//            }
//        });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(stringRequest);
//
//
//        return REQ;
//    }


    public void drawPath(String  result) {
        //Getting both the coordinates
        LatLng from = new LatLng(place1.getPosition().latitude,place1.getPosition().longitude);
        LatLng to = new LatLng(place2.getPosition().latitude,place2.getPosition().longitude);

        //Calculating the distance in meters
        Double distance = SphericalUtil.computeDistanceBetween(from, to);

        //Displaying the distance
        //Toast.makeText(this,String.valueOf(distance+" Meters"),Toast.LENGTH_SHORT).show();

        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            Log.d("[BOWON]",encodedString);
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(20)
                    .color(Color.RED)
                    .geodesic(true)
            );


        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public class GetData extends AsyncTask<Void, Void, String>{

        private String url;
        private ContentValues values;

        public GetData(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = null;

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
                buffer.append("depart_lag=");                          ///여기다가 파라미터 추가
                buffer.append(values.get("depart_lag"));
                buffer.append("&depart_lng=");                          ///여기다가 파라미터 추가
                buffer.append(values.get("depart_lng"));
                buffer.append("&depart_address=");                          ///여기다가 파라미터 추가
                buffer.append(values.get("depart_address"));
                buffer.append("&arrival_lag=");                          ///여기다가 파라미터 추가
                buffer.append(values.get("arrival_lag"));
                buffer.append("&arrival_lng=");                          ///여기다가 파라미터 추가
                buffer.append(values.get("arrival_lng"));
                buffer.append("&arrival_address=");                          ///여기다가 파라미터 추가
                buffer.append(values.get("arrival_address"));           //여기다가 파라미터 추가
                buffer.append("&transportation=");

                String trans = (String) values.get("transportation");
                if (trans.equals("택시")) {
                    buffer.append("택시");
                } else if (trans.equals("대중교통")){
                    buffer.append("대중교통");
                }

                String requestBody = buffer.toString();

                Log.d("BODY", requestBody);

                OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream(), "utf-8");
                PrintWriter writer = new PrintWriter(outStream);
//                writer.write(body.toString());
                writer.write(requestBody);

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
            catch (
                    MalformedURLException e) {
                Log.d("catch : ", "MalformedURLException");
                e.printStackTrace();
            }
            catch (
                    IOException e) {
                Log.d("catch : ", "IOException");
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s){
            Log.d("TAG", "onPostExecute" + s);

            super.onPostExecute(s);

            JSONArray results = null;
            JSONArray results2 = null;

            String distance_right;
            String cost_right;

            try {
                results = new JSONArray(s);

                JSONObject json = results.getJSONObject(0);

                distance_right = json.getString("distance");
                cost_right = json.getString("average_cost");

                distance_tv.setText("총 거리 : " + distance_right +"km");
                cost_tv.setText("평균금액 : " + cost_right + " KRW");

                results2 = results.getJSONArray(1);

                for (int i = 0; i < results2.length(); i++) {

                    JSONObject json1 = results2.getJSONObject(i);

                    start_address[i] = json1.getString("start_address");
                    end_address[i] = json1.getString("end_address");
                    distance[i] = json1.getString("distance");
                    cost[i] = json1.getString("cost");
                    time[i] = json1.getString("timeslot");

                    adapter.addVO(start_address[i],end_address[i], distance[i], cost[i], time[i]);
                    adapter.notifyDataSetChanged();

                }



            } catch ( Exception e ) {
                e.printStackTrace();
            }
//
//            try {
//
//
//                Log.d("TAG", "results : " + results + " length :  " + results.length() );
//
//                for (int i = 0; i < results.length(); i++) {
//
//                    JSONObject jObject = results.getJSONObject(i);
//
//                    start_address[i] = jObject.getString("start_address");
//                    end_address[i] = jObject.getString("end_address");
//                    distance[i] = Double.toString(jObject.getDouble("distance"));
//                    cost[i] = Integer.toString(jObject.getInt("cost"));
//                    time[i] = Integer.toString(jObject.getInt("timeslot"));
//
//
//                    adapter.addVO(start_address[i],end_address[i], distance[i], cost[i], time[i]);
//                    adapter.notifyDataSetChanged();
//
//                    count = count + 1;
//                    Log.d("Count Changed : ", "to " + count);
//
////                    mapView.getMapAsync(SearchAroundFood.this);
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }

    }
}
