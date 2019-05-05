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
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AccountingWriteActivity extends FragmentActivity implements MapFragmentForNewAccounting.OnMyListner2{
    Button btnNewAccounting;
    Fragment mapFragment;
    ViewPager pager;
    LatLng current;
    GoogleMap googleMap;
    EditText etStart, etEnd, etTitle, etMember, etContent;
    Switch mSwitchShare;
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
        etMember = (EditText)findViewById(R.id.etMember);
        etContent = (EditText)findViewById(R.id.etContent);
        mSwitchShare = (Switch)findViewById(R.id.switch1);

        //Don't Show Keyboard for this edittext
        etStart.setInputType(0);
        etEnd.setInputType(0);

        pager = (ViewPager)findViewById(R.id.pager2);

        pager.setAdapter(new AccountingWriteActivity.pagerAdapter(getSupportFragmentManager()));

        btnNewAccounting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("[INBAE]", "btnNewAccounting is Clicked!");
                String content;
                int share = 0;
                if(mSwitchShare.isChecked())
                    share = 1;
                if(etContent.getText() == null)
                    content = "";
                else
                    content = etContent.getText().toString();
                if(isDataReady()){
                    //POST DATA
                    Log.d("[INBAE]","Data will be posted.");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body= new FormBody.Builder()
                            .add("id","123") //it will be modified after data transfer with login activity
                            .add("title", etTitle.getText().toString())
                            .add("lat", Double.toString(current.latitude))
                            .add("lng",Double.toString(current.longitude))
                            .add("start_time",etStart.getText().toString())
                            .add("end_time",etEnd.getText().toString())
                            .add("member",etMember.getText().toString())
                            .add("content",content)
                            .add("share", Integer.toString(share))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://ec2-13-125-178-212.ap-northeast-2.compute.amazonaws.com/php/inputAccounting.php")
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            String mMessage = e.getMessage().toString();
                            Log.d("[INBAE_FAILURE]",mMessage);
                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                            String mMessage = response.body().string();
                            Log.d("[INBAE_SUCCESS]",mMessage);
                        }
                    });

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
            String Month = Integer.toString(i1+1);
            String Day = Integer.toString(i2);

            if(Month.length()<=1)
                Month = "0"+Month;
            if(Day.length() <=1)
                Day = "0"+Day;

            etStart.setText(i+"."+Month+"."+Day);
        }
    };
    private DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            String Month = Integer.toString(i1+1);
            String Day = Integer.toString(i2);

            if(Month.length()<=1)
                Month = "0"+Month;
            if(Day.length() <=1)
                Day = "0"+Day;
            etEnd.setText(i+"."+Month+"."+Day);
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
        return etStart.getText() != null && etEnd.getText() != null && etTitle.getText() != null && current != null && (current.latitude != 0 && current.longitude !=0) && etMember != null;
    }

}
