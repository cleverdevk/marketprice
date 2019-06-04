package com.example.marketprice.SearchAround;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marketprice.Adapter.FoodListViewAdapter;
import com.example.marketprice.ListVO.listVO;
import com.example.marketprice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAroundSouvByCondition extends Fragment {

    View v;

    private ArrayList<com.example.marketprice.ListVO.listVO> listVO = new ArrayList<listVO>() ;
    private ArrayList<listVO> searchResult ;

    private ListView listView;
    private FoodListViewAdapter adapter;
    private FoodListViewAdapter searchAdapter;

    private String[] no      = new String[100];
    private float[] lat      = new float[100];
    private float[] lng      = new float[100];
    private String[] imgurl  = new String[100];
    private String[] content = new String[100];
    private float[] rate     = new float[100];
    private String[] cost    = new String[100];
    private String[] name    = new String[100];
    private String[] good    = new String[100];
    private String[] bad     = new String[100];
    private String[] MoneyCode = new String[100];

    private int[] pos = new int[100];

    JSONArray results;

    Button search;
    EditText keyword, priceFrom, priceTo;

    String strKeyword, strFrom, strTo;

    List<Address> address_temp;
    double myLat, myLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.search_food_condition, container, false);

        listView = (ListView)v.findViewById(R.id.List_view);
        search = (Button)v.findViewById(R.id.search);
        keyword = (EditText)v.findViewById(R.id.keyword);
        priceFrom = (EditText)v.findViewById(R.id.priceFrom);
        priceTo = (EditText)v.findViewById(R.id.priceTo);

        listVO = (ArrayList<listVO>) getArguments().getSerializable("datas");

        Bundle b = this.getArguments();
        String jsonArray = b.getString("Array");
        myLat = b.getDouble("lat");
        myLng = b.getDouble("lng");

        try {
            results = new JSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new FoodListViewAdapter();
        adapter.setListVO(listVO);
        listView.setAdapter(adapter);


        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                strKeyword = keyword.getText().toString();
                strFrom = priceFrom.getText().toString();
                strTo = priceTo.getText().toString();

                int count = 0;

                //이름만 입력
                if(strFrom.length() == 0 && strTo.length() == 0){

                    searchAdapter = new FoodListViewAdapter();

                    for(int i = results.length() - 1 ; i > -1 ; i --){

                        String tempName = null;
                        try {
                            tempName = results.getJSONObject(i).getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(tempName.toLowerCase().contains(strKeyword.toLowerCase())){

                            JSONObject jObject = null;

                            try {

                                jObject = results.getJSONObject(i);

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

                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = null;

                                try {
                                    addresses = geocoder.getFromLocation(lat[i], lng[i], 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String ISOcode = "(USD)";

                                switch (addresses.get(0).getCountryName()){
                                    case "Vietnam" :
                                        ISOcode = "(VND)";
                                        break;
                                    case "Brunei" :
                                        ISOcode = "(BND)";
                                        break;
                                    case "Singapore" :
                                        ISOcode = "(SGD)";
                                        break;
                                    case "Indonesia" :
                                        ISOcode = "(IDR)";
                                        break;
                                    case "Cambodia" :
                                        ISOcode = "(KHR)";
                                        break;
                                    case "Thailand" :
                                        ISOcode = "(THB)";
                                        break;
                                    case "Philippines" :
                                        ISOcode = "(PHP)";
                                        break;
                                    case "China" :
                                        ISOcode = "(CHY)";
                                        break;
                                    case "Australia" :
                                        ISOcode = "(AUD)";
                                        break;
                                    case "Japan " :
                                        ISOcode = "(JPY)";
                                        break;
                                    case "Russia" :
                                        ISOcode = "(RUB)";
                                        break;
                                    case "New Zealand" :
                                        ISOcode = "(NZD)";
                                        break;
                                    case "Greece" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "France":
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Spain" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Sweden" :
                                        ISOcode = "(SEK)";
                                        break;
                                    case "Norway" :
                                        ISOcode = "(NOK)";
                                        break;
                                    case "Germany" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Finland" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Poland" :
                                        ISOcode = "(PLN)";
                                        break;
                                    case "Italy" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "United Kingdom" :
                                        ISOcode = "(GBP)";
                                        break;
                                    case "Hungary" :
                                        ISOcode = "(HUF)";
                                        break;
                                    case "Portugal" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Austria" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Czechia" :
                                        ISOcode = "(CZK)";
                                        break;
                                    case "Solvakia" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Denmark" :
                                        ISOcode = "(DKK)";
                                        break;
                                    case "Switzerland" :
                                        ISOcode = "(CHF)";
                                        break;
                                    case "Netherlands" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Belgium" :
                                        ISOcode = "(EUR)";
                                        break;
                                    case "Turkey" :
                                        ISOcode = "(TRY)";
                                        break;
                                    case "United States" :
                                        ISOcode = "(USD)";
                                        break;
                                    case "Canada" :
                                        ISOcode = "(CAD)";
                                        break;
                                    case "Mexico" :
                                        ISOcode = "(MXN)";
                                        break;
                                    case "Argentina" :
                                        ISOcode = "(ARS)";
                                        break;
                                    case "Bolivia" :
                                        ISOcode = "(BOB)";
                                        break;
                                    case "Brazil" :
                                        ISOcode = "(BRL)";
                                        break;
                                    case "Chile" :
                                        ISOcode = "(CLP)";
                                        break;
                                    case "Colombia" :
                                        ISOcode = "(COP)";
                                        break;
                                    case "Ecuador" :
                                        ISOcode = "(ECS)";
                                        break;
                                    case "Uruguay" :
                                        ISOcode = "(UYU)";
                                        break;
                                    case "Venezuela" :
                                        ISOcode = "(VEF)";
                                        break;
                                    case "Peru" :
                                        ISOcode = "(PEN)";
                                        break;
                                    case "South Korea" :
                                        ISOcode = "(KRW)";
                                        break;
                                }

                                MoneyCode[i] = ISOcode;

                                if (Math.abs(myLat - lat[i]) < 0.15 && Math.abs(myLng - lng[i]) < 0.15){
                                    searchAdapter.addVO(imgurl[i], name[i], cost[i], MoneyCode[i]);
                                    pos[count] = i;
                                    count = count + 1;
                                }
                                searchAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    listView.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();

                }

                //가격 정보만 입력
                else if(strKeyword.length() == 0){

                    if(strFrom.length() == 0 || strTo.length() == 0) {

                        Toast.makeText(getContext(), "최소금액과 최대금액을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();

                    } else {

                        searchAdapter = new FoodListViewAdapter();

                        for(int i = results.length() - 1 ; i > -1 ; i --){

                            int tempPrice = 0;

                            try {

                                tempPrice = results.getJSONObject(i).getInt("cost");

                            } catch (JSONException e) {

                                e.printStackTrace();

                            }

                            if(tempPrice >= Integer.parseInt(strFrom) && tempPrice <= Integer.parseInt(strTo)){

                                JSONObject jObject = null;

                                try {

                                    jObject = results.getJSONObject(i);

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

                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    List<Address> addresses = null;

                                    try {
                                        addresses = geocoder.getFromLocation(lat[i], lng[i], 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d("Country is :", "" + addresses.get(0).getCountryName());

                                    String ISOcode = "NULL";

                                    switch (addresses.get(0).getCountryName()){
                                        case "Vietnam" :
                                            ISOcode = "(VND)";
                                            break;
                                        case "Brunei" :
                                            ISOcode = "(BND)";
                                            break;
                                        case "Singapore" :
                                            ISOcode = "(SGD)";
                                            break;
                                        case "Indonesia" :
                                            ISOcode = "(IDR)";
                                            break;
                                        case "Cambodia" :
                                            ISOcode = "(KHR)";
                                            break;
                                        case "Thailand" :
                                            ISOcode = "(THB)";
                                            break;
                                        case "Philippines" :
                                            ISOcode = "(PHP)";
                                            break;
                                        case "China" :
                                            ISOcode = "(CHY)";
                                            break;
                                        case "Australia" :
                                            ISOcode = "(AUD)";
                                            break;
                                        case "Japan " :
                                            ISOcode = "(JPY)";
                                            break;
                                        case "Russia" :
                                            ISOcode = "(RUB)";
                                            break;
                                        case "New Zealand" :
                                            ISOcode = "(NZD)";
                                            break;
                                        case "Greece" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "France":
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Spain" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Sweden" :
                                            ISOcode = "(SEK)";
                                            break;
                                        case "Norway" :
                                            ISOcode = "(NOK)";
                                            break;
                                        case "Germany" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Finland" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Poland" :
                                            ISOcode = "(PLN)";
                                            break;
                                        case "Italy" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "United Kingdom" :
                                            ISOcode = "(GBP)";
                                            break;
                                        case "Hungary" :
                                            ISOcode = "(HUF)";
                                            break;
                                        case "Portugal" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Austria" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Czechia" :
                                            ISOcode = "(CZK)";
                                            break;
                                        case "Solvakia" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Denmark" :
                                            ISOcode = "(DKK)";
                                            break;
                                        case "Switzerland" :
                                            ISOcode = "(CHF)";
                                            break;
                                        case "Netherlands" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Belgium" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Turkey" :
                                            ISOcode = "(TRY)";
                                            break;
                                        case "United States" :
                                            ISOcode = "(USD)";
                                            break;
                                        case "Canada" :
                                            ISOcode = "(CAD)";
                                            break;
                                        case "Mexico" :
                                            ISOcode = "(MXN)";
                                            break;
                                        case "Argentina" :
                                            ISOcode = "(ARS)";
                                            break;
                                        case "Bolivia" :
                                            ISOcode = "(BOB)";
                                            break;
                                        case "Brazil" :
                                            ISOcode = "(BRL)";
                                            break;
                                        case "Chile" :
                                            ISOcode = "(CLP)";
                                            break;
                                        case "Colombia" :
                                            ISOcode = "(COP)";
                                            break;
                                        case "Ecuador" :
                                            ISOcode = "(ECS)";
                                            break;
                                        case "Uruguay" :
                                            ISOcode = "(UYU)";
                                            break;
                                        case "Venezuela" :
                                            ISOcode = "(VEF)";
                                            break;
                                        case "Peru" :
                                            ISOcode = "(PEN)";
                                            break;
                                        case "South Korea" :
                                            ISOcode = "(KRW)";
                                            break;
                                    }

                                    MoneyCode[i] = ISOcode;

                                    if (Math.abs(myLat - lat[i]) < 0.15 && Math.abs(myLng - lng[i]) < 0.15){
                                        searchAdapter.addVO(imgurl[i], name[i], cost[i], MoneyCode[i]);
                                        pos[count] = i;
                                        count = count + 1;
                                    }

                                    searchAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        listView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }

                    //둘다입력
                } else {

                    if(strFrom.length() == 0 || strTo.length() == 0) {
                        Toast.makeText(getContext(), "최소금액과 최대금액을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        searchAdapter = new FoodListViewAdapter();


                        for(int i = results.length() - 1 ; i > -1 ; i --){

                            String tempName = null;
                            try {
                                tempName = results.getJSONObject(i).getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            int tempPrice = 0;
                            try {
                                tempPrice = results.getJSONObject(i).getInt("cost");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(tempName.toLowerCase().contains(strKeyword.toLowerCase()) &&
                                    tempPrice >= Integer.parseInt(strFrom) && tempPrice <= Integer.parseInt(strTo)){

                                JSONObject jObject = null;

                                try {

                                    jObject = results.getJSONObject(i);

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

                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    List<Address> addresses = null;

                                    try {
                                        addresses = geocoder.getFromLocation(lat[i], lng[i], 1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Log.d("Country is :", "" + addresses.get(0).getCountryName());

                                    String ISOcode = "NULL";

                                    switch (addresses.get(0).getCountryName()){
                                        case "Vietnam" :
                                            ISOcode = "(VND)";
                                            break;
                                        case "Brunei" :
                                            ISOcode = "(BND)";
                                            break;
                                        case "Singapore" :
                                            ISOcode = "(SGD)";
                                            break;
                                        case "Indonesia" :
                                            ISOcode = "(IDR)";
                                            break;
                                        case "Cambodia" :
                                            ISOcode = "(KHR)";
                                            break;
                                        case "Thailand" :
                                            ISOcode = "(THB)";
                                            break;
                                        case "Philippines" :
                                            ISOcode = "(PHP)";
                                            break;
                                        case "China" :
                                            ISOcode = "(CHY)";
                                            break;
                                        case "Australia" :
                                            ISOcode = "(AUD)";
                                            break;
                                        case "Japan " :
                                            ISOcode = "(JPY)";
                                            break;
                                        case "Russia" :
                                            ISOcode = "(RUB)";
                                            break;
                                        case "New Zealand" :
                                            ISOcode = "(NZD)";
                                            break;
                                        case "Greece" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "France":
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Spain" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Sweden" :
                                            ISOcode = "(SEK)";
                                            break;
                                        case "Norway" :
                                            ISOcode = "(NOK)";
                                            break;
                                        case "Germany" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Finland" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Poland" :
                                            ISOcode = "(PLN)";
                                            break;
                                        case "Italy" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "United Kingdom" :
                                            ISOcode = "(GBP)";
                                            break;
                                        case "Hungary" :
                                            ISOcode = "(HUF)";
                                            break;
                                        case "Portugal" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Austria" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Czechia" :
                                            ISOcode = "(CZK)";
                                            break;
                                        case "Solvakia" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Denmark" :
                                            ISOcode = "(DKK)";
                                            break;
                                        case "Switzerland" :
                                            ISOcode = "(CHF)";
                                            break;
                                        case "Netherlands" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Belgium" :
                                            ISOcode = "(EUR)";
                                            break;
                                        case "Turkey" :
                                            ISOcode = "(TRY)";
                                            break;
                                        case "United States" :
                                            ISOcode = "(USD)";
                                            break;
                                        case "Canada" :
                                            ISOcode = "(CAD)";
                                            break;
                                        case "Mexico" :
                                            ISOcode = "(MXN)";
                                            break;
                                        case "Argentina" :
                                            ISOcode = "(ARS)";
                                            break;
                                        case "Bolivia" :
                                            ISOcode = "(BOB)";
                                            break;
                                        case "Brazil" :
                                            ISOcode = "(BRL)";
                                            break;
                                        case "Chile" :
                                            ISOcode = "(CLP)";
                                            break;
                                        case "Colombia" :
                                            ISOcode = "(COP)";
                                            break;
                                        case "Ecuador" :
                                            ISOcode = "(ECS)";
                                            break;
                                        case "Uruguay" :
                                            ISOcode = "(UYU)";
                                            break;
                                        case "Venezuela" :
                                            ISOcode = "(VEF)";
                                            break;
                                        case "Peru" :
                                            ISOcode = "(PEN)";
                                            break;
                                        case "South Korea" :
                                            ISOcode = "(KRW)";
                                            break;
                                    }

                                    MoneyCode[i] = ISOcode;

                                    if (Math.abs(myLat - lat[i]) < 0.15 && Math.abs(myLng - lng[i]) < 0.15){
                                        searchAdapter.addVO(imgurl[i], name[i], cost[i], MoneyCode[i]);
                                        pos[count] = i;
                                        count = count + 1;
                                    }
                                    searchAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        listView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Geocoder geocoder = new Geocoder (getContext(), Locale.getDefault());

                Bundle args = new Bundle();
                JSONObject jObject = null;

                try {

                    jObject = results.getJSONObject(pos[position]);

                    imgurl[pos[position]] = jObject.getString("imageurl");
                    no[pos[position]] = jObject.getString("no");
                    lat[pos[position]] = Float.parseFloat(jObject.getString("lat"));
                    lng[pos[position]] = Float.parseFloat(jObject.getString("lng"));
                    content[pos[position]] = jObject.getString("content");
                    rate[pos[position]] = Float.parseFloat(jObject.getString("rate"));
                    cost[pos[position]] = Integer.toString(jObject.getInt("cost"));
                    name[pos[position]] = jObject.getString("name");
                    good[pos[position]] = jObject.getString("good");
                    bad[pos[position]] = jObject.getString("bad");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    address_temp = geocoder.getFromLocation(lat[pos[position]], lng[pos[position]], 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String address = address_temp.get(0).getAddressLine(0);

                args.putString("img", imgurl[pos[position]]);
                args.putString("Name",  name[pos[position]]);
                args.putString("Price", cost[pos[position]]);
                args.putFloat("lat", lat[pos[position]]);
                args.putFloat("lng", lng[pos[position]]);
                args.putString("address", address);
                args.putFloat("rate", rate[pos[position]]);
                args.putString("ISOcode", MoneyCode[pos[position]]);
                args.putString("content", content[pos[position]]);



                SearchAroundSouvDetail fragment2 = new SearchAroundSouvDetail();

                fragment2.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment2)
                        .commit();

            }
        });




        return v;

    }

    //    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.search_food_condition);
//
//        listView = (ListView)findViewById(R.id.List_view);
//        search = (Button)findViewById(R.id.search);
//        keyword = (EditText) findViewById(R.id.keyword);
//        priceFrom = (EditText) findViewById(R.id.priceFrom);
//        priceTo = (EditText) findViewById(R.id.priceTo);
//
//        listVO = (ArrayList<listVO>) getIntent().getSerializableExtra("datas");
//
//        Bundle b = getIntent().getExtras();
//        String jsonArray = b.getString("Array");
//
//        try {
//            results = new JSONArray(jsonArray);
//            JSONObject jObject = results.getJSONObject(0);
//            System.out.println(jObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        adapter = new FoodListViewAdapter();
//        adapter.setListVO(listVO);
//        listView.setAdapter(adapter);
//
//        search.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d("get result is : ", " " + listVO.get(1).getPrice());
//                strKeyword = keyword.getText().toString();
//                strFrom = priceFrom.getText().toString();
//                strTo = priceTo.getText().toString();
//
//                //이름만 입력
//                if(strFrom.length() == 0 && strTo.length() == 0){
//
//                    searchAdapter = new FoodListViewAdapter();
//
//                    for(int i = 0 ; i < listVO.size() ; i ++){
//
//                        String tempName = listVO.get(i).getName();
//
//                        if(tempName.toLowerCase().contains(strKeyword.toLowerCase())){
//
//                            JSONObject jObject = null;
//
//                            try {
//
//                                jObject = results.getJSONObject(i);
//
//                                imgurl[i] = jObject.getString("imageurl");
//                                no[i] = jObject.getString("no");
//                                lat[i] = Float.parseFloat(jObject.getString("lat"));
//                                lng[i] = Float.parseFloat(jObject.getString("lng"));
//                                content[i] = jObject.getString("content");
//                                rate[i] = Float.parseFloat(jObject.getString("rate"));
//                                cost[i] = Integer.toString(jObject.getInt("cost"));
//                                name[i] = jObject.getString("name");
//                                good[i] = jObject.getString("good");
//                                bad[i] = jObject.getString("bad");
//                                pos[i] = i;
//
//                                searchAdapter.addVO(imgurl[i], name[i], cost[i]);
//                                searchAdapter.notifyDataSetChanged();
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                    listView.setAdapter(searchAdapter);
//                    searchAdapter.notifyDataSetChanged();
//
//                }
//
//                //가격 정보만 입력
//                else if(strKeyword.length() == 0){
//
//                    if(strFrom.length() == 0 || strTo.length() == 0) {
//                        Toast.makeText(SearchAroundFoodByCondition.this, "최소금액과 최대금액을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        searchAdapter = new FoodListViewAdapter();
//
//                        for(int i = 0 ; i < listVO.size() ; i ++){
//
//                            int tempPrice = Integer.parseInt(listVO.get(i).getPrice());
//
//                            if(tempPrice >= Integer.parseInt(strFrom) && tempPrice <= Integer.parseInt(strTo)){
//
//                                JSONObject jObject = null;
//
//                                try {
//
//                                    jObject = results.getJSONObject(i);
//
//                                    imgurl[i] = jObject.getString("imageurl");
//                                    no[i] = jObject.getString("no");
//                                    lat[i] = Float.parseFloat(jObject.getString("lat"));
//                                    lng[i] = Float.parseFloat(jObject.getString("lng"));
//                                    content[i] = jObject.getString("content");
//                                    rate[i] = Float.parseFloat(jObject.getString("rate"));
//                                    cost[i] = Integer.toString(jObject.getInt("cost"));
//                                    name[i] = jObject.getString("name");
//                                    good[i] = jObject.getString("good");
//                                    bad[i] = jObject.getString("bad");
//
//                                    searchAdapter.addVO(imgurl[i], name[i], cost[i]);
//                                    searchAdapter.notifyDataSetChanged();
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        listView.setAdapter(searchAdapter);
//                        searchAdapter.notifyDataSetChanged();
//                    }
//
//                //둘다입력
//                } else {
//
//                    if(strFrom.length() == 0 || strTo.length() == 0) {
//                        Toast.makeText(SearchAroundFoodByCondition.this, "최소금액과 최대금액을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        searchAdapter = new FoodListViewAdapter();
//
//                        for(int i = 0 ; i < listVO.size() ; i ++){
//
//                            String tempName = listVO.get(i).getName();
//                            int tempPrice = Integer.parseInt(listVO.get(i).getPrice());
//                            Log.d("price is : ", "" + listVO.get(i).getPrice());
//
//                            if(tempName.toLowerCase().contains(strKeyword.toLowerCase()) &&
//                                    tempPrice >= Integer.parseInt(strFrom) && tempPrice <= Integer.parseInt(strTo)){
//
//                                JSONObject jObject = null;
//
//                                try {
//
//                                    jObject = results.getJSONObject(i);
//
//                                    imgurl[i] = jObject.getString("imageurl");
//                                    no[i] = jObject.getString("no");
//                                    lat[i] = Float.parseFloat(jObject.getString("lat"));
//                                    lng[i] = Float.parseFloat(jObject.getString("lng"));
//                                    content[i] = jObject.getString("content");
//                                    rate[i] = Float.parseFloat(jObject.getString("rate"));
//                                    cost[i] = Integer.toString(jObject.getInt("cost"));
//                                    name[i] = jObject.getString("name");
//                                    good[i] = jObject.getString("good");
//                                    bad[i] = jObject.getString("bad");
//
//                                    searchAdapter.addVO(imgurl[i], name[i], cost[i]);
//                                    searchAdapter.notifyDataSetChanged();
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//
//                        listView.setAdapter(searchAdapter);
//                        searchAdapter.notifyDataSetChanged();
//                    }
//
//                }
//            }
//        });
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SearchAroundFoodByCondition.this, (position + 1) + "번째 리스트가 클릭되었습니다.", Toast.LENGTH_SHORT).show();
//
//                Bundle args = new Bundle();
//
//                args.putString("img", imgurl[pos[position]]);
//                args.putString("Name",  name[pos[position]]);
//                args.putString("Price", cost[pos[position]]);
//                args.putFloat("lat", lat[pos[position]]);
//                args.putFloat("lng", lng[pos[position]]);
//                args.putFloat("rate", rate[pos[position]]);
//                args.putString("content", content[pos[position]]);
//
//
//
//                SearchAroundFoodDetail fragment2 = new SearchAroundFoodDetail();
//
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                fragment2.setArguments(args);
//
//                fragmentTransaction.add(R.id.content_frame, fragment2);
//                fragmentTransaction.commit();
//
//                finish();
////                getFragmentManager().beginTransaction()
////                        .replace(R.id.content_frame, fragment2)
////                        .commit();
////                finish();
//            }
//        });
//    }

}
