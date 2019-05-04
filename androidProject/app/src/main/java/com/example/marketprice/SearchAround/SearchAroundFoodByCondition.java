package com.example.marketprice.SearchAround;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketprice.Adapter.FoodListViewAdapter;
import com.example.marketprice.ListVO.listVO;
import com.example.marketprice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchAroundFoodByCondition extends AppCompatActivity {

    private ArrayList<listVO> listVO = new ArrayList<listVO>() ;
    private ArrayList<listVO> searchResult ;

    private ListView listView;
    private FoodListViewAdapter adapter;
    private FoodListViewAdapter searchAdapter;

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

    private int[] pos = new int[100];

    JSONArray results;

    Button search;
    EditText keyword, priceFrom, priceTo;

    String strKeyword, strFrom, strTo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_food_condition);

        listView = (ListView)findViewById(R.id.List_view);
        search = (Button)findViewById(R.id.search);
        keyword = (EditText) findViewById(R.id.keyword);
        priceFrom = (EditText) findViewById(R.id.priceFrom);
        priceTo = (EditText) findViewById(R.id.priceTo);

        listVO = (ArrayList<listVO>) getIntent().getSerializableExtra("datas");

        Bundle b = getIntent().getExtras();
        String jsonArray = b.getString("Array");

        try {
            results = new JSONArray(jsonArray);
            JSONObject jObject = results.getJSONObject(0);
            System.out.println(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new FoodListViewAdapter();
        adapter.setListVO(listVO);
        listView.setAdapter(adapter);

        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("get result is : ", " " + listVO.get(1).getPrice());
                strKeyword = keyword.getText().toString();
                strFrom = priceFrom.getText().toString();
                strTo = priceTo.getText().toString();

                //이름만 입력
                if(strFrom.length() == 0 && strTo.length() == 0){

                    searchAdapter = new FoodListViewAdapter();

                    for(int i = 0 ; i < listVO.size() ; i ++){

                        String tempName = listVO.get(i).getName();

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
                                pos[i] = i;

                                searchAdapter.addVO(imgurl[i], name[i], cost[i]);
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
                        Toast.makeText(SearchAroundFoodByCondition.this, "최소금액과 최대금액을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        searchAdapter = new FoodListViewAdapter();

                        for(int i = 0 ; i < listVO.size() ; i ++){

                            int tempPrice = Integer.parseInt(listVO.get(i).getPrice());

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

                                    searchAdapter.addVO(imgurl[i], name[i], cost[i]);
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
                        Toast.makeText(SearchAroundFoodByCondition.this, "최소금액과 최대금액을 모두 작성해 주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        searchAdapter = new FoodListViewAdapter();

                        for(int i = 0 ; i < listVO.size() ; i ++){

                            String tempName = listVO.get(i).getName();
                            int tempPrice = Integer.parseInt(listVO.get(i).getPrice());
                            Log.d("price is : ", "" + listVO.get(i).getPrice());

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

                                    searchAdapter.addVO(imgurl[i], name[i], cost[i]);
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
                Toast.makeText(SearchAroundFoodByCondition.this, (position+1)+"번째 리스트가 클릭되었습니다.", Toast.LENGTH_SHORT).show();

                Bundle args = new Bundle();

                args.putString("img", imgurl[pos[position]]);
                args.putString("Name",  name[pos[position]]);
                args.putString("Price", cost[pos[position]]);
                args.putFloat("lat", lat[pos[position]]);
                args.putFloat("lng", lng[pos[position]]);
                args.putFloat("rate", rate[pos[position]]);
                args.putString("content", content[pos[position]]);



//                SearchAroundFoodDetail fragment2 = new SearchAroundFoodDetail();
//
//                fragment2.setArguments(args);
//
//                getFragmentManager().beginTransaction()
//                        .replace(R.id.content_frame, fragment2)
//                        .commit();
//                finish();
            }
        });
    }
}
