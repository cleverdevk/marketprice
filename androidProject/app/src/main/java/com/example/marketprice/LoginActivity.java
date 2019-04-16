package com.example.marketprice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    EditText login_id, password;
    String strLogin, strPassword;
    ProgressDialog dialog = null;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_id = (EditText)findViewById(R.id.login_id);
        password = (EditText)findViewById(R.id.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

    }

    public void loginClicked(View v) {

        dialog = ProgressDialog.show(LoginActivity.this, "",
                "Validating user...", true);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                login();
                Looper.loop();
            }
        }).start();
    }

    void login() {
        try {

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/userCheck.php");

            strLogin = login_id.getText().toString();
            strPassword = password.getText().toString();

            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", login_id.getText().toString()));
//            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();


            final String response = httpclient.execute(httppost, responseHandler);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });

            //로그인 성공했을 때 echo로 값
            if (!response.equalsIgnoreCase("User Not Found")) {

                JSONArray results = new JSONArray(response);

                JSONObject jObject = results.getJSONObject(0);

                String id = jObject.getString("id");
                String password = jObject.getString("password");
                String Salt = jObject.getString("salt");

                if(password.equals(BCrypt.hashpw(strPassword, Salt))) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //성공하고 다른 activity로 넘어감
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userID", strLogin);
                    startActivityForResult(intent, 1);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "로그인 실패. 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    //회원가입
    public void signUpClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }


    public String getUserID() {
        return strLogin;
    }




}
