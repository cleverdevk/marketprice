package com.example.marketprice.Accounts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.example.marketprice.R;

import java.util.ArrayList;

public class MyAccountActivity  extends Activity {
    private ExpandableListView listView;
    Display newDisplay;
    ExpandAdapter adapter;
    int width;
    int total_money=0;
    EditText when,what,howMuch;
    ArrayList<myGroup> DataList;
    //    ArrayList<String[]> mylist;
    public Button add_btn;
    public String requestWhen, requestWhat;
    int requestHowMuch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail_write);
        newDisplay = getWindowManager().getDefaultDisplay();
        width = newDisplay.getWidth();
        DataList = new ArrayList<>();
        listView = (ExpandableListView) findViewById(R.id.mylist);

        adapter = new ExpandAdapter(getApplicationContext(), R.layout.group_row, R.layout.child_row, DataList);
        listView.setIndicatorBounds(width - 50, width); //이 코드를 지우면 화살표 위치가 바뀐다.
        listView.setAdapter(adapter);

        //토탈 추가해주기.
        myGroup total = new myGroup("Total");
        total.child.add("Total Used Money");
        total.money.add(0);
        DataList.add(total);

        //더미데이터 추가
        addItem("1996-10-27", "one", 100);
        addItem("1996-10-28", "two", 100);
        addItem("1996-10-28", "three", 100);
        addItem("1996-10-28", "four", 100);
        addItem("1996-10-27", "five", 100);

        //디비에서 이미 저장된 부분 불러다 addItem
        //
        //
        //

    }

    public void addBtnOnClicked(View v){
        CustomDialog dialog = new CustomDialog(this, requestWhen, requestWhat, requestHowMuch);
        dialog.setDialogListener(new MyDialogListener() {
            @Override
            public void onPositiveClicked(String when, String what, int howMuch) {

                setResult(when,what,howMuch);
                addItem(when, what, howMuch);

            }

            @Override
            public void onNegativeClicked() {
                Log.d("MyDialogListener","onNegativeClicked");
            }
        });
        dialog.show();
//                break;
    }

    private void setResult(String when, String what, int howMuch){
        requestWhen = when;
        requestWhat = what;
        requestHowMuch = howMuch;
    }

    public void addItem(String date, String what, int howMuch){

        for(int i = 0 ; i<DataList.size() ; i++) {

            //이미 기록된 날짜인지 검사. 기록된 날짜면 같은 리스트에 넣어준다.
            if(DataList.get(i).groupName.contains(date)) {
                myGroup temp = DataList.get(i);
                DataList.remove(i);
                temp.child.add(what);
                temp.money.add(howMuch);

                //토탈 추가해주기
                myGroup total_temp = DataList.get(0);
                total_money += howMuch;
                total_temp.money.set(0,total_money);
                DataList.set(0,total_temp);

                DataList.add(temp);

                adapter.notifyDataSetChanged();
                return;
            }
        }

        // 이미 기록된 날짜가 없는 경우.
        myGroup temp = new myGroup(date);
        temp.child.add(what);
        temp.money.add(howMuch);
        DataList.add(temp);

        //토탈 추가해주기
        myGroup total_temp = DataList.get(0);
        total_money += howMuch;
        total_temp.money.set(0,total_money);
        DataList.set(0,total_temp);

        adapter.notifyDataSetChanged();

    }

}
