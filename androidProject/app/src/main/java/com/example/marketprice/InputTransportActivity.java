package com.example.marketprice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.util.Log;

public class InputTransportActivity extends FragmentActivity {

    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputtransport);


        final Spinner spinner_field = (Spinner) findViewById(R.id.spinner_field);
        String[] str = getResources().getStringArray(R.array.spinnerArray);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,str);


        pager = (ViewPager)findViewById(R.id.pager);

        pager.setAdapter(new pagerAdapter(getSupportFragmentManager()));

//        Button mBtnDeparture, mBtnDestination;
        //mBtnDeparture = (Button) findViewById(R.id.BtnSelectDeparture);
        //mBtnDestination=(Button) findViewById(R.id.BtnSelectDestination);

//        mBtnDeparture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(InputTransportActivity.this, SelectPositionActivity.class);
//                startActivity(intent);
//            }
//        });
//        mBtnDestination.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(InputTransportActivity.this, SelectPositionActivity.class);
//                startActivity(intent);
//            }
//        });

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(adapter);

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner_field.getSelectedItemPosition() > 0){
                    // 선택된 것
                    Log.v("알림",spinner_field.getSelectedItem().toString()+"is selected");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

    }
    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(FragmentManager fm )
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return new MapFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // total page count
            return 1;
        }
    }
}
