package com.example.marketprice;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class AccountingWriteActivity extends FragmentActivity implements MapFragmentForNewAccounting.OnMyListner{
    Button btnNewAccounting;
    Fragment mapFragment;
    ViewPager pager;
    LatLng current;
    GoogleMap googleMap;

    @Override
    public void onReceivedData(LatLng data, GoogleMap googleMap){
        current = data;
        this.googleMap = googleMap;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountingwrite);

        btnNewAccounting = (Button)findViewById(R.id.btnNewAccounting);

        pager = (ViewPager)findViewById(R.id.pager2);

        pager.setAdapter(new AccountingWriteActivity.pagerAdapter(getSupportFragmentManager()));

        btnNewAccounting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("[INBAE]", "btnNewAccounting is Clicked!");
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
