package com.example.marketprice.Accounts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketprice.R;

public class CustomDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private MyDialogListener dialogListener;

    //
    private android.widget.DatePicker DatePicker;
    int mYear, mMonth,mDay;
    String dateFormat;
    private EditText when;
    private EditText what;
    private EditText howMuch;
    private Button okButton;
    private Button cancelButton;

    private String whenResult;
    private String whatResult;
    private int howMuchResult;

    public CustomDialog(@NonNull Context context){
        super(context);
        this.context=context;
    }

    public CustomDialog(Context context, String whenResult, String whatResult, int howMuchResult){
        super(context);
        this.context = context;
//        this.whenResult = whenResult;
//        this.whatResult = whatResult;
//        this.howMuchResult = howMuchResult;
    }

    public void setDialogListener(MyDialogListener dialogListener){
        this.dialogListener = dialogListener;
    }


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
        DatePicker = (DatePicker)findViewById(R.id.when);
        mYear=DatePicker.getYear();
        mMonth=DatePicker.getMonth();
        mDay=DatePicker.getDayOfMonth();
        DatePicker.init(DatePicker.getYear(), DatePicker.getMonth(), DatePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

            }
        });
        what = (EditText) findViewById(R.id.what);
        howMuch = (EditText)findViewById(R.id.howMuch);
        okButton = (Button)findViewById(R.id.okButton);
        cancelButton = (Button)findViewById(R.id.cancelButton);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

//        if(whenResult!=null){
//            what.setText(whatResult);
//            howMuch.setText(howMuchResult);
//        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancelButton:
                cancel();
                break;
            case R.id.okButton:
//                whenResult = when.getText().toString();
                String tempMonth;
                String tempDay;

                if(Integer.toString(mMonth+1).length() == 1) {
                    tempMonth = "0" + (mMonth + 1);
                } else {
                    tempMonth = Integer.toString(mMonth + 1);
                }

                if(Integer.toString(mDay).length() == 1) {
                    tempDay = "0" + mDay;
                } else {
                    tempDay = Integer.toString(mDay);
                }

                whenResult = String.format("%d-%s-%s",mYear,tempMonth,tempDay);
                whatResult = what.getText().toString();
                howMuchResult = Integer.parseInt(howMuch.getText().toString());
                dialogListener.onPositiveClicked(whenResult,whatResult,howMuchResult);
                dismiss();
                break;
        }
    }

}
