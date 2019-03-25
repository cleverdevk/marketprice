package com.example.marketprice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SignUpActivity extends AppCompatActivity {

    EditText id, password, passwordChk, nickName;
    String strId, strPassword, strPasswordChk, strNickName;
    ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        id = (EditText) findViewById(R.id.signup_id);
        password = (EditText) findViewById(R.id.signup_password);
        passwordChk = (EditText) findViewById(R.id.signup_passwordChk);
        nickName = (EditText) findViewById(R.id.signup_nickName);
    }

    private void insertToDatabase(String Id, String Pw, String nickName) {

        class InsertData extends AsyncTask<String, Void, String> {

            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignUpActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                    loading = null;
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String Id = (String) params[0];
                    String Pw = (String) params[1];
                    String nickName = (String) params[2];

                    String link = "http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/signup.php?";
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8");
                    data += "&" + URLEncoder.encode("nickName", "UTF-8") + "=" + URLEncoder.encode(nickName, "UTF-8");

                    link += data;

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertData task = new InsertData();
        task.execute(Id, Pw, nickName);

    }


    public void onClicked(View view) {
        strId           = id.getText().toString();
        strPassword     = password.getText().toString();
        strPasswordChk  = passwordChk.getText().toString();
        strNickName     = nickName.getText().toString();

        if(containsWhiteSpace(strId) == true && containsWhiteSpace(strPassword) == true && strNickName.length() != 0) {
            insertToDatabase(strId, strPassword, strNickName);
            startActivity((new Intent(SignUpActivity.this, LoginActivity.class)));
            finish();
        } else {
            Toast.makeText(SignUpActivity.this, "내용을 모두 채워주세요. 공백이 포함될수는 없습니다.", Toast.LENGTH_SHORT).show();
        }



    }

    public static boolean containsWhiteSpace(String testCode){

        if(testCode.length() == 0) {
            return false;
        }
        for(int i = 0; i < testCode.length(); i++) {
            if(Character.isWhitespace(testCode.charAt(i))) {
                return false;
            }
        }
        return true;
    }



}


