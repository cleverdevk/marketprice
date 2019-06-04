package com.example.marketprice.SplashSignInUp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.marketprice.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(this, LoginActivity.class);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
                //Do something after 100ms
            }
        }, 1500);

//        try{
//            Thread.sleep(3000);
//            startActivity(new Intent(this,LoginActivity.class));
//            finish();
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }


    }
}
