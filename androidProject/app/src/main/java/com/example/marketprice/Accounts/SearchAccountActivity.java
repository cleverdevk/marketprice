package com.example.marketprice.Accounts;

import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.marketprice.Adapter.AccountListViewAdapter;
import com.example.marketprice.R;
import com.example.marketprice.SearchAround.SearchAroundFood;

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
import java.util.List;
import java.util.Locale;

public class SearchAccountActivity extends AppCompatActivity {

    private ListView listView;
    private AccountListViewAdapter adapter;

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
    private int[] pos = new int[100];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_accounts);

        //변수 초기화
        adapter = new AccountListViewAdapter();
        listView = (ListView)findViewById(R.id.accountListView);

        listView.setAdapter((adapter));

        GetData task = new GetData("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/getAccountList.php",null);
        task.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MyAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    public String money(float lat, float lng) throws IOException {
        String currency = null;

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

        if(addresses.size() > 0){
            String countryName = addresses.get(0).getCountryName();

            if(countryName.equals("South Korea")) {
                currency = "KRW";
            }
        }

        return currency;
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
                results = new JSONArray(s);

                for (int i = 0; i < results.length(); i++) {

                    JSONObject jObject = results.getJSONObject(i);

                    no[i] = jObject.getString("no");
                    id[i] = jObject.getString("id");
                    lat[i] = Float.parseFloat(jObject.getString("lat"));
                    lng[i] = Float.parseFloat(jObject.getString("lng"));
                    title[i] = jObject.getString("title");
                    content[i] = jObject.getString("content");
                    start_time[i] = jObject.getString("start_time");
                    end_time[i] = jObject.getString("end_time");
                    member[i] = jObject.getString("member");
                    share[i] = jObject.getString("share");
                    pos[i] = i;

                    adapter.addAC(title[i], start_time[i] + "~" + end_time[i], content[i], "0");

                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
