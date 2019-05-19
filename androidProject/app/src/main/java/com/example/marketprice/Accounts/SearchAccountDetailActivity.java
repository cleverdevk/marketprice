package com.example.marketprice.Accounts;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.marketprice.Adapter.AccountListViewAdapter;
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

public class SearchAccountDetailActivity extends AppCompatActivity {

    private EditText keyword;
    private ImageButton search;
    private AccountListViewAdapter adapter;
    private ListView listView;

    String searchKey;
    JSONArray results;

    private String[] no = new String[100];
    private String[] id = new String[100];
    private float[] lat = new float[100];
    private float[] lng = new float[100];
    private String[] title = new String[100];
    private String[] content = new String[100];
    private String[] start_time = new String[100];
    private String[] end_time = new String[100];
    private String[] member = new String[100];
    private String[] share = new String[100];
    private String[] total = new String[100];
    private int[] pos = new int[100];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_search_detail);

        adapter = new AccountListViewAdapter();
        keyword = (EditText)findViewById(R.id.keyword);
        listView = (ListView)findViewById(R.id.resultView);
        search = (ImageButton)findViewById(R.id.search);

        listView.setAdapter((adapter));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AccountDetailActivity.class);
                intent.putExtra("no",no[position]);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new AccountListViewAdapter();
                listView.setAdapter((adapter));
                GetData task = new GetData("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/getAccountSearchResult.php",null);
                task.execute();
            }
        });
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
                searchKey = keyword.getText().toString();
                buffer.append("keyword").append("=").append(searchKey);

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
                if(s != null){
                    results = new JSONArray(s);
                    JSONObject jObject = results.getJSONObject(0);

                    int count = Integer.parseInt(jObject.getString("count"));

                    for (int i = 1; i < results.length() - count ; i++) {

                        jObject = results.getJSONObject(i);

                        System.out.println("Object is " + jObject);

                        no[i-1] = jObject.getString("no");
                        id[i-1] = jObject.getString("id");
                        lat[i-1] = Float.parseFloat(jObject.getString("lat"));
                        lng[i-1] = Float.parseFloat(jObject.getString("lng"));
                        title[i-1] = jObject.getString("title");
                        content[i-1] = jObject.getString("content");
                        start_time[i-1] = jObject.getString("start_time");
                        end_time[i-1] = jObject.getString("end_time");
                        member[i-1] = jObject.getString("member");
                        share[i-1] = jObject.getString("share");
                        pos[i-1] = i-1;

                    }

                    int k = 0;

                    for(int j = results.length() - count ; j < results.length(); j++) {

                        jObject = results.getJSONObject(j);

                        System.out.println("total Object is " + jObject);

                        total[k] = jObject.getString("sum(cost)");

                        adapter.addAC(title[k], start_time[k] + "~" + end_time[k], content[k], total[k]);

                        adapter.notifyDataSetChanged();

                        k ++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
