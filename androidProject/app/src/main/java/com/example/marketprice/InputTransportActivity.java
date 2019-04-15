package com.example.marketprice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class InputTransportActivity extends FragmentActivity implements MapFragment.OnMyListner {

    ViewPager pager;
    LatLng current = new LatLng(0,0);
    Position mDeparture = new Position();
    Position mDestination = new Position();
    GoogleMap googleMap;
    String mDepartureAddress, mDestinationAddress, mDistance, mType, mCost, mTime;
    EditText mEtAmount;
    Switch switchAccouting;
    boolean isShare;



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


        final Spinner spinner_field = (Spinner) findViewById(R.id.spinner_field);
        String[] str = getResources().getStringArray(R.array.spinnerArray);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,str);
        switchAccouting = (Switch) findViewById(R.id.switchAccounting);

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("정보를 모두 입력해 주세요.");
        alertBuilder.setPositiveButton("확인",null);

        final AlertDialog alert = alertBuilder.create();
        alert.setIcon(R.drawable.ic_launcher_foreground);
        alert.setTitle("교통 정보 입력");


        pager = (ViewPager)findViewById(R.id.pager);

        pager.setAdapter(new pagerAdapter(getSupportFragmentManager()));

        final Button mBtnDeparture, mBtnDestination, mBtnOK;
        mBtnDeparture = (Button) findViewById(R.id.btnDeparture);
        mBtnDestination=(Button) findViewById(R.id.btnDestination);
        mBtnOK = (Button) findViewById(R.id.btnOK);
        mEtAmount = (EditText) findViewById(R.id.editTextAmount);

        switchAccouting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData(spinner_field.toString(),switchAccouting.isChecked());
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


        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataReady()){
                    //push data to db
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
                    Toast.makeText(getApplicationContext(), "출발지 : " + current.latitude + ", " + current.longitude, Toast.LENGTH_SHORT).show();
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
        spinner_field.setAdapter(adapter);

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
}
