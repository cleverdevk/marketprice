package com.example.marketprice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marketprice.Accounts.AccountingListItem;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.SphericalUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cz.msebera.android.httpclient.HttpConnection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class InputTransportActivity extends FragmentActivity implements MapFragment.OnMyListner {

    ViewPager pager;
    LatLng current = new LatLng(0,0);
    Position mDeparture = new Position();
    Position mDestination = new Position();
    GoogleMap googleMap;
    String mDepartureAddress, mDestinationAddress, mDistance, mType, mCost, mTime;
    EditText mEtAmount;
    ToggleButton shareAccounting;
    boolean isShare;
    String mjsonResult;
    HttpPost httpPost;
    HttpResponse httpResponse;
    HttpClient httpClient;
    List<NameValuePair> nameValuePairs;
    int AUTOCOMPLETE_REQUEST_COSE = 1;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);
    private String API_KEY = "AIzaSyClJpA5YRWaLkc7hXplUolDaCxFXtasK1k";
    Fragment mapFragment = new Fragment();
    String name;

    private android.support.v7.app.AlertDialog.Builder builder;
    private android.support.v7.app.AlertDialog.Builder builder_detail;
    int choosedItem;
    String choosedDate;

    private ArrayList<AccountingListItem> mItems;
    private ArrayList<String> no;
    private ArrayList<String> names;
    private ArrayList<String> start_date;
    private ArrayList<String> end_date;
    private ArrayList<String> bet_dates;

    String userID;

    @Override
    public void onReceivedData(LatLng data, GoogleMap googleMap){
        current = data;
        this.googleMap = googleMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputtransport);

//        getSupportFragmentManager().beginTransaction().add(R.id.map, new MapFragment(),"MAP_FRAGMENT").commit();
//        mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.pager);



        Intent intentID = getIntent();
        userID = intentID.getExtras().getString("userID");


        final Spinner spinner_field = (Spinner) findViewById(R.id.spinner_field);
        final Spinner time_spinner_field = (Spinner) findViewById(R.id.time_spinner_field);

        String[] str = getResources().getStringArray(R.array.spinnerArray);
        String[] str2 = getResources().getStringArray(R.array.spinnerArray_timeslot);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,str);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,str2);

        shareAccounting = (ToggleButton) findViewById(R.id.shareaccounting);

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("정보를 모두 입력해 주세요.");
        alertBuilder.setPositiveButton("확인",null);

        final AlertDialog alert = alertBuilder.create();
        alert.setIcon(R.drawable.ic_launcher_foreground);
        alert.setTitle("교통 정보 입력");


        pager = (ViewPager)findViewById(R.id.pager);

        pager.setAdapter(new pagerAdapter(getSupportFragmentManager()));

        final Button mBtnDeparture, mBtnDestination, mBtnOK, mBtnSearch;
        mBtnDeparture = (Button) findViewById(R.id.btnDeparture);
        mBtnDestination=(Button) findViewById(R.id.btnDestination);
        mBtnOK = (Button) findViewById(R.id.btnOK);
        mBtnSearch = (Button)findViewById(R.id.btnFromSearch);
        mEtAmount = (EditText) findViewById(R.id.editTextAmount);

        builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder_detail = new android.support.v7.app.AlertDialog.Builder(this);
        mItems = new ArrayList<>();
        PostData();

        // 가계부 공유
        shareAccounting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(shareAccounting.isChecked()){
                    builder.setTitle("가계부를 선택해주세요.");

                    String[] elements = names.toArray(new String[names.size()]);

                    int checkedItem = 1;

                    builder.setSingleChoiceItems(elements, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //elements[which]
                            //user checked an item
                            choosedItem = which;

                            dialog.dismiss();


                            //시작날짜 - 끝날짜 계산
                            bet_dates = new ArrayList<>();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


                            Calendar start = Calendar.getInstance();
                            Calendar end =  Calendar.getInstance();

                            try {
                                start.setTime(df.parse(start_date.get(which)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                end.setTime(df.parse(end_date.get(which)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            while(start.compareTo(end) != 1){
                                bet_dates.add(df.format(start.getTime()));
                                start.add(Calendar.DATE, 1);
                            }

                            Log.d("DATE", bet_dates.toString());

                            final String[] dates = bet_dates.toArray(new String[bet_dates.size()]);

                            builder_detail.setTitle("날짜를 선택해주세요.");

                            int checkedItem2 = 1;

                            builder_detail.setSingleChoiceItems(dates, checkedItem2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // user checked an item
//                                    UploadToServer(which,  dates[which]);
                                    choosedDate = dates[which];
                                    Log.d("WHICH", Integer.toString(which));
                                    Log.d("CHOOSEDDATE", choosedDate);
                                }
                            });

                            builder_detail.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // user clicked OK

                                }
                            });

                            builder_detail.setNegativeButton("취소" , null);

                            android.support.v7.app.AlertDialog dialog_detail = builder_detail.create();
                            dialog_detail.show();

                        }
                    });

                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user clicked OK

                        }
                    });

                    builder.setNegativeButton("취소" , null);


                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();

                }else{

                }
            }
        });


        mEtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setData(spinner_field.toString(),true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                setData(spinner_field.toString(),true);
            }
        });


        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Places.initialize(getApplicationContext(),API_KEY);
                PlacesClient placesClient = Places.createClient(getApplicationContext());



                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_COSE);
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataReady()){
                    //push data to db
                    //PostData(mDeparture, mDestination,mDistance,mCost);
                    PostData2();
                    ShareAccounting(choosedItem, choosedDate);


                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                    finish();
                }
                else{
                    alertBuilder.create().show();
                }

            }
        });

        mBtnDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.latitude == 0 && current.longitude == 0)
                    Toast.makeText(getApplicationContext(),"출발지를 눌러주세요!",Toast.LENGTH_SHORT).show();
                else {

                    mDeparture.x = current.latitude;
                    mDeparture.y = current.longitude;
                }
            }
        });
        mBtnDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.latitude == 0 && current.longitude == 0)
                    Toast.makeText(getApplicationContext(),"도착지를 눌러주세요!",Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getApplicationContext(), "도착지 : " + current.latitude + ", " + current.longitude, Toast.LENGTH_SHORT).show();
                    mDestination.x = current.latitude;
                    mDestination.y = current.longitude;

                    if(mDestination.x != 0 && mDestination.y != 0){
                        String basereq = "https://maps.googleapis.com/maps/api/directions/json?unit=metric&";
                        //String API_KEY = "AIzaSyC9fsk433XYbQqE8X1mMEkFtij6G2tGRlk";
                        String API_KEY = "AIzaSyClJpA5YRWaLkc7hXplUolDaCxFXtasK1k";
                        String origins = Double.toString(mDeparture.x) + "," + Double.toString(mDeparture.y);
                        String destinations = Double.toString(mDestination.x) + "," + Double.toString(mDestination.y);
                        String REQ = basereq + "origin=" + origins + "&destination=" + destinations + "&key=" + API_KEY;
                        Log.d("[INBAE]","REQUSET SQL = " + REQ);
                        StringRequest stringRequest = new StringRequest(REQ, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("[INBAE]", response);
                                mjsonResult = response;
                                drawPath(response);
                                setData(response,spinner_field.getSelectedItem().toString(),isShare);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("[INBAE]","dpfjska");
                            }
                        });

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);

                    }
                }
            }
        });


        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(adapter);
        time_spinner_field.setAdapter(adapter2);

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner_field.getSelectedItemPosition() > 0){
                    // 선택된 것
                    Log.v("알림",spinner_field.getSelectedItem().toString()+"is selected");
                    setData(spinner_field.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        time_spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(time_spinner_field.getSelectedItemPosition() > 0){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }
    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(FragmentManager fm )
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return new MapFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // total page count
            return 1;
        }
    }

    public boolean isDataReady(){
        return mType != null && mDestinationAddress != null && mDepartureAddress != null && mTime != null && mDistance != null && mCost != null;
    }

    public  void setData(String result, String spinner_text, boolean switchValue){
        final JSONObject json;
        try {
            json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routePlace0 = routeArray.getJSONObject(0);
            JSONArray routePlace1 = routePlace0.getJSONArray("legs");
            JSONObject routePlace2 = routePlace1.getJSONObject(0);
            JSONObject routeDistance = routePlace2.getJSONObject("distance");
            JSONObject routeTime = routePlace2.getJSONObject("duration");

            mDistance = routeDistance.getString("text");
            mTime = routeTime.getString("text");
            mDepartureAddress = routePlace2.getString("start_address");
            mDestinationAddress = routePlace2.getString("end_address");
            mType = spinner_text;
            isShare = switchValue;
            mCost = mEtAmount.getText().toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void PostData2(){
        OkHttpClient client = new OkHttpClient();
        String km = Double.toString (Double.parseDouble(mDistance.replace(" mi","")) * 1.6);

        RequestBody body= new FormBody.Builder()
                .add("id",userID)
                .add("start_lat",Double.toString(mDeparture.x))
                .add("start_lng",Double.toString(mDeparture.y))
                .add("end_lat",Double.toString(mDestination.x))
                .add("end_lng",Double.toString(mDestination.y))
                .add("distance",km)
                .add("json",mjsonResult)
                .add("start_address",mDepartureAddress)
                .add("end_address",mDestinationAddress)
                .add("type",mType)
                .add("cost",mCost).build();

        Request request = new Request.Builder()
                .url("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/inputTransport.php")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.d("[INBAE_FAILURE]",mMessage);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String mMessage = response.body().string();
                Log.d("[INBAE_SUCCESS]",mMessage);
            }
        });
    }

    //서버로 전송 (가계부에 공유 한 항목)
    public void ShareAccounting(int which, String date) {
        Log.d("WHICH_share", Integer.toString(which));
        Log.d("CHOOSEDDATE_share", date);

        OkHttpClient client = new OkHttpClient();

        RequestBody body= new FormBody.Builder()
                .add("accountingno", no.get(which))
//                .add("no",review)
                .add("name", mType)
                .add("cost", mCost)
                .add("date",date).build();

        Request request = new Request.Builder()
                .url("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/inputAccountingDetail.php ")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.d("ERROR",mMessage);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String mMessage = response.body().string();
                Log.d("BODY",mMessage);
            }


        });

        finish();
    }



    public void setData(String spinner_text, boolean switchValue){
        mType = spinner_text;
        isShare = switchValue;
        mCost = mEtAmount.getText().toString();
    }

    public void setData(String spinner_text){
        mType = spinner_text;
        mCost = mEtAmount.getText().toString();
    }

    public void drawPath(String  result) {
        //Getting both the coordinates
        LatLng from = new LatLng(mDeparture.x,mDeparture.y);
        LatLng to = new LatLng(mDestination.x,mDestination.y);

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
            Log.d("[INBAE]",encodedString);
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = googleMap.addPolyline(new PolylineOptions()
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == AUTOCOMPLETE_REQUEST_COSE){
            if(resultCode == RESULT_OK){



                Place place = Autocomplete.getPlaceFromIntent(data);
                current = place.getLatLng();
                name = place.getName();
                Bundle bundle = new Bundle();
                bundle.putDouble("lat",place.getLatLng().latitude);
                bundle.putDouble("lng",place.getLatLng().longitude);
                bundle.putString("name",place.getName());
                mapFragment.setArguments(bundle);
                LatLng fromSearch = new LatLng(current.latitude, current.longitude);
                Log.d("[INBAE]", "LATLNG : " + current.latitude + ", " + current.longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(fromSearch);
                markerOptions.title(name);
                googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromSearch, 15));

                Log.i("[INBAE]", "Place : "+place.getName() + ", " + place.getId());
            }
            else if(resultCode == AutocompleteActivity.RESULT_ERROR){
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("[INBAE]",status.getStatusMessage());
            }
            else if(resultCode == RESULT_CANCELED){
                Log.i("[INBAE]", "Operation Canceled by User");
            }
        }
    }
    public LatLng getLatLng(){
        return current;
    }
    public  String getName(){
        return name;
    }

    public void PostData(){
        mItems.clear();
        OkHttpClient client = new OkHttpClient();
        RequestBody body= new FormBody.Builder()
                .add("id","123").build();

        Request request = new Request.Builder()
                .url("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/getAccounting.php")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.d("Connection error",mMessage);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String mMessage = response.body().string();
                no = new ArrayList<>();
                names = new ArrayList<>();
                start_date =  new ArrayList<>();
                end_date =  new ArrayList<>();
                try {
                    JSONArray json = new JSONArray(mMessage);

                    for(int i=0;i<json.length();i++){
                        JSONObject jsonObject = json.getJSONObject(i);
                        no.add(jsonObject.getString("no"));
                        names.add(jsonObject.getString("title"));
                        start_date.add(jsonObject.getString("start_time"));
                        end_date.add(jsonObject.getString("end_time"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("names",names.toString()); //Sanfrancisco, trip trip, Indiana
                Log.d("Post",mMessage);
                for (String name : names) {
                    mItems.add(new AccountingListItem(name));
                }

//                handler.sendMessage(new Message());
            }
        });
        // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.

    }
}
