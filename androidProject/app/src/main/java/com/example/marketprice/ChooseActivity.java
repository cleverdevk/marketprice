package com.example.marketprice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
    }

    public void foodClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), newFoodActivity.class);
        startActivity(intent);
    }
    public void transportClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), InputTransportActivity.class);
        startActivity(intent);
    }
}
