package com.example.marketprice.Accounts;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.marketprice.R;
import com.example.marketprice.RecyclerAdapter;
import com.example.marketprice.RecyclerItemClickListener;
import com.example.marketprice.databinding.ActivityAccountinglistBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AccountingListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    //private String[] names = {"LALALAND 따라 가는 LA 여행","Google! Apple! 컴공의 성지 탐방 Sanfrancisco 여행!","타코, 나초, 모히또 맛있는 것이 넘쳐나는 San Diego 여행!","종강기념 삿포로 조지기","기타등등","기타등등2","기타등등3","기타등등4","기타등등5"};
    private ArrayList<String> names;
    private ArrayList<String> no;
    private static final int LAYOUT = R.layout.activity_accountinglist;
    private ActivityAccountinglistBinding mainBinding;
    private RecyclerView.Adapter adapter;
    ImageButton imageButton;
    Toolbar toolbar;
    String strID;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
        }
    };

    private ArrayList<AccountingListItem> mItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,LAYOUT);
        Intent intent = getIntent();
        strID = intent.getStringExtra("id");
        setRecyclerView();
        setRefresh();
        getSupportActionBar().setTitle("내 가계부");



        //imageButton = (ImageButton) findViewById(R.id.toolbar2_addButton);

//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),AccountingWriteActivity.class);
//                startActivity(intent);
//
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.myaccounting, menu);
        return true;
    }
    //액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //or switch문을 이용하면 될듯 하다.
        if (id == R.id.action_add) {
            Toast.makeText(this, "추가", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),AccountingWriteActivity.class);
            intent.putExtra("id",strID);
            startActivityForResult(intent,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            if(resultCode == RESULT_OK){
                setRecyclerView();
            }
        }
    }

    public void setRecyclerView(){
        // 각 Item 들이 RecyclerView 의 전체 크기를 변경하지 않는 다면
        // setHasFixedSize() 함수를 사용해서 성능을 개선할 수 있습니다.
        // 변경될 가능성이 있다면 false 로 , 없다면 true를 설정해주세요.
        mainBinding.recyclerView.setHasFixedSize(true);

        // RecyclerView 에 Adapter 를 설정해줍니다.
        adapter = new RecyclerAdapter(mItems);

        mainBinding.recyclerView.setAdapter(adapter);

        // 다양한 LayoutManager 가 있습니다. 원하시는 방법을 선택해주세요.
        // 지그재그형의 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        // 그리드 형식
        //mainBinding.recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        // 가로 또는 세로 스크롤 목록 형식
        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
        mainBinding.recyclerView.addItemDecoration(dividerItemDecoration);
        //mainBinding.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(48));

        mainBinding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mainBinding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),position+"번 째 아이템 클릭", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),AccountDetailActivity.class);
                        intent.putExtra("no",no.get(position));
                        intent.putExtra("title",names.get(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),position+"번 째 아이템 롱 클릭",Toast.LENGTH_SHORT).show();
                    }
                }));
        PostData();
    }

    private void setRefresh(){
        mainBinding.swipeRefreshLo.setOnRefreshListener(this);
        mainBinding.swipeRefreshLo.setColorSchemeColors(getResources().getIntArray(R.array.google_colors));
    }


    @Override
    public void onRefresh() {
        mainBinding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(mainBinding.recyclerView,"Refresh Success",Snackbar.LENGTH_SHORT).show();
                mainBinding.swipeRefreshLo.setRefreshing(false);
            }
        },500);
    }

//    private void setData(){
//        mItems.clear();
//        PostData();
//        // RecyclerView 에 들어갈 데이터를 추가합니다.
//        if(names != null) {
//            for (String name : names) {
//                mItems.add(new AccountingListItem(name));
//            }
//        }
//        // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.
//        adapter.notifyDataSetChanged();
//    }


    public void PostData(){
        mItems.clear();
        OkHttpClient client = new OkHttpClient();
        String result;
        RequestBody body= new FormBody.Builder()
                .add("id",strID).build();

        Request request = new Request.Builder()
                .url("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/getAccounting.php")
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
                names = new ArrayList<>();
                no = new ArrayList<>();
                try {
                    JSONArray json = new JSONArray(mMessage);

                    for(int i=0;i<json.length();i++){
                        JSONObject jsonObject = json.getJSONObject(i);
                        names.add(jsonObject.getString("title"));
                        no.add(jsonObject.getString("no"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("[INBAE]",names.toString());
                Log.d("[INBAE_SUCCESS]",mMessage);
                for (int i=0;i<names.size();i++) {
                    mItems.add(new AccountingListItem(names.get(i), no.get(i)));
                }
                handler.sendMessage(new Message());
            }
        });
        // 데이터 추가가 완료되었으면 notifyDataSetChanged() 메서드를 호출해 데이터 변경 체크를 실행합니다.

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
