package com.example.marketprice.Accounts;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.example.marketprice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class AccountDetailActivity extends AppCompatActivity {

    private ExpandableListView listView;
    Display newDisplay;
    ExpandAdapter adapter;
    int width;
    int total_money=0;
    EditText when,what,howMuch;
    ArrayList<myGroup> DataList;
    public Button add_btn;
    public String requestWhen, requestWhat;
    int requestHowMuch;

    String no;

    JSONArray results;

    private String[] name = new String[100];
    private int[] cost = new int[100];
    private String[] date = new String[100];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);


        newDisplay = getWindowManager().getDefaultDisplay();
        width = newDisplay.getWidth();
        DataList = new ArrayList<>();
        listView = (ExpandableListView) findViewById(R.id.accountlist);

        adapter = new ExpandAdapter(getApplicationContext(), R.layout.group_row, R.layout.child_row, DataList);

//        /
        listView.setAdapter(adapter);

        //토탈 추가해주기.
        myGroup total = new myGroup("Total");
        total.child.add("Total Used Money");
        total.money.add(0);
        DataList.add(total);


        Intent intent = getIntent();

        no = intent.getStringExtra("no");
        Log.d("no is : ", "" + no);

        GetData task = new GetData("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/getAccountDetail.php",null);
        task.execute();

    }

    private void setResult(String when, String what, int howMuch){
        requestWhen = when;
        requestWhat = what;
        requestHowMuch = howMuch;
    }

    public void addItem(String date, String what, int howMuch){

        for(int i = 0 ; i<DataList.size() ; i++) {

            //이미 기록된 날짜인지 검사. 기록된 날짜면 같은 리스트에 넣어준다.
            if(DataList.get(i).groupName.contains(date)) {
                myGroup temp = DataList.get(i);
                DataList.remove(i);
                temp.child.add(what);
                temp.money.add(howMuch);

                //토탈 추가해주기
                myGroup total_temp = DataList.get(0);
                total_money += howMuch;
                total_temp.money.set(0,total_money);
                DataList.set(0,total_temp);

                DataList.add(temp);

                adapter.notifyDataSetChanged();
                return;
            }
        }

        // 이미 기록된 날짜가 없는 경우.
        myGroup temp = new myGroup(date);
        temp.child.add(what);
        temp.money.add(howMuch);
        DataList.add(temp);

        //토탈 추가해주기
        myGroup total_temp = DataList.get(0);
        total_money += howMuch;
        total_temp.money.set(0,total_money);
        DataList.set(0,total_temp);

        adapter.notifyDataSetChanged();

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
                buffer.append("no").append("=").append(no);

                OutputStreamWriter outStream = new OutputStreamWriter(con.getOutputStream(), "utf-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();
                outStream.close();                    ///여기다가 파라미터 추가



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

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if(s != null) {
                    results = new JSONArray(s);

                    for (int i = 0; i < results.length(); i++) {

                        JSONObject jObject = results.getJSONObject(i);

                        name[i] = jObject.getString("name");
                        cost[i] = Integer.parseInt(jObject.getString("cost"));
                        date[i] = jObject.getString("date");

                        Log.d("Datas : ","" + name[i]+ " " + cost[i] + " " + date[i]);

                        addItem(date[i], name[i], cost[i]);

                        adapter.notifyDataSetChanged();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
