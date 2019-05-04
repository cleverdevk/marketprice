package com.example.marketprice.SplashSignInUp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.marketprice.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends AppCompatActivity {

    String ServerURL = "http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/signup.php";
    EditText id, password, passwordChk, nickName;
    String strId, strPassword, strPasswordChk, strNickName;
    Button post;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        id = (EditText) findViewById(R.id.signup_id);
        password = (EditText) findViewById(R.id.signup_password);
        passwordChk = (EditText) findViewById(R.id.signup_passwordChk);
        nickName = (EditText) findViewById(R.id.signup_nickName);
        post = (Button) findViewById(R.id.signup_complete);

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordChk.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData();

                if(containsWhiteSpace(strId) == false || containsWhiteSpace(strPassword) == false || strNickName.length() == 0) {
                    Toast.makeText(SignUpActivity.this, "내용을 모두 채워주세요. 공백이 포함될수는 없습니다.", Toast.LENGTH_SHORT).show();
                } else if (!strPassword.equals(strPasswordChk)) {

                    Toast.makeText(SignUpActivity.this, "동일한 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();

                }else {

                    String Salt = BCrypt.gensalt(10);
                    String hashPass = BCrypt.hashpw(strPassword, Salt);

                    InsertData(strId, hashPass, strNickName, Salt);
                    startActivity((new Intent(SignUpActivity.this, LoginActivity.class)));
                    finish();
                }

//                if(containsWhiteSpace(strId) == true && containsWhiteSpace(strPassword) == true && strNickName.length() != 0) {
//                    InsertData(strId, strPassword, strNickName);
//                    startActivity((new Intent(SignUpActivity.this, LoginActivity.class)));
//                    finish();
//                } else {
//                    Toast.makeText(SignUpActivity.this, "내용을 모두 채워주세요. 공백이 포함될수는 없습니다.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    public void GetData() {
        strId           = id.getText().toString();
        strPassword     = password.getText().toString();
        strPasswordChk  = passwordChk.getText().toString();
        strNickName     = nickName.getText().toString();
    }

    public void InsertData(final String id, final String password, final String nickName, final String Salt) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... strings) {
                String IdHolder          = id;
                String PasswordHolder    = password;
                String NickNameHolder    = nickName;
                String SaltHolder        = Salt;

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id", IdHolder));
                nameValuePairs.add(new BasicNameValuePair("password", PasswordHolder));
                nameValuePairs.add(new BasicNameValuePair("nickname", NickNameHolder));
                nameValuePairs.add(new BasicNameValuePair("salt", SaltHolder));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpGet httpGet = new HttpGet(ServerURL + "?" + URLEncodedUtils.format(nameValuePairs, "utf-8"));

                    HttpResponse httpResponse = httpClient.execute(httpGet);

                    HttpEntity httpEntity = httpResponse.getEntity();


                } catch (ClientProtocolException e) {

                    return new String("ClientProtocol Exception: " + e.getMessage());

                } catch (IOException e) {

                    return new String("IOException: " + e.getMessage()); //Handles an incorrectly entered URL

                }
                return "Data Inserted Successfully";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Toast.makeText(SignUpActivity.this, "Data submitted successfully", Toast.LENGTH_LONG).show();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(id, password, nickName, Salt);

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


