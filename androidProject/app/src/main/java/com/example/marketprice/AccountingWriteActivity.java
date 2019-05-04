package com.example.marketprice;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AccountingWriteActivity extends FragmentActivity implements MapFragmentForNewAccounting.OnMyListner2{
    Button btnNewAccounting;
    Fragment mapFragment;
    ViewPager pager;
    LatLng current;
    GoogleMap googleMap;
    EditText etStart, etEnd, etTitle;
    Calendar cal = new GregorianCalendar();
    int mYear, mMnoth, mDay;

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
        etStart = (EditText)findViewById(R.id.dateStart);
        etEnd = (EditText)findViewById(R.id.dateEnd);
        etTitle = (EditText)findViewById(R.id.etAccountingTitle);

        //Don't Show Keyboard for this edittext
        etStart.setInputType(0);
        etEnd.setInputType(0);

        pager = (ViewPager)findViewById(R.id.pager2);

        pager.setAdapter(new AccountingWriteActivity.pagerAdapter(getSupportFragmentManager()));

        btnNewAccounting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("[INBAE]", "btnNewAccounting is Clicked!");
                if(isDataReady()){
                    //POST DATA
                    Log.d("[INBAE]","Data will be posted.");
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"입력 항목을 모두 입력하시고, 여행지 위치도 선택해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do not show keyboard
                InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(etStart,InputMethodManager.SHOW_IMPLICIT);

            }
        });
        etStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    int mYear = cal.get(Calendar.YEAR);
                    int mMonth = cal.get(Calendar.MONTH);
                    int mDay = cal.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(AccountingWriteActivity.this,dateSetListener,mYear,mMonth,mDay);
                    datePickerDialog.show();
                }
                return false;
            }
        });
        etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do not show keyboard
                InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(etEnd,InputMethodManager.SHOW_IMPLICIT);

            }
        });
        etEnd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    int mYear = cal.get(Calendar.YEAR);
                    int mMonth = cal.get(Calendar.MONTH);
                    int mDay = cal.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(AccountingWriteActivity.this,dateSetListener2,mYear,mMonth,mDay);
                    datePickerDialog.show();
                }
                return false;
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            etStart.setText(i+"."+Integer.toString(i1+1)+"."+i2);
        }
    };
    private DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            etEnd.setText(i+"."+Integer.toString(i1+1)+"."+i2);
        }
    };



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
                    return new MapFragmentForNewAccounting();
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

    private boolean isDataReady(){
        return etStart.getText() != null && etEnd.getText() != null && etTitle.getText() != null && current != null && (current.latitude != 0 && current.longitude !=0);
    }

}
