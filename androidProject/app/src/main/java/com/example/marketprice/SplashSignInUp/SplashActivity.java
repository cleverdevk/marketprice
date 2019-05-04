package com.example.marketprice.SplashSignInUp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super .onCreate(savedInstanceState);

        try{
            Thread.sleep(1500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
