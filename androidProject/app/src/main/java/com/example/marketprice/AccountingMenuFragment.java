package com.example.marketprice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AccountingMenuFragment extends Fragment {
    Button btnView, btnWrite;


    public AccountingMenuFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accountingmenu, container, false);
        btnView = (Button) view.findViewById(R.id.btnAccountingView);
        btnWrite = (Button) view.findViewById(R.id.btnAccountingWrite);





        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //가계부 보기 전환 넣을 곳
                Log.d("[INBAE]", "BtnView Clicked!");
            }
        });
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("[INBAE]", "btnWrite Clicked!");
                Intent intent = new Intent(getActivity(),AccountingWriteActivity.class);
                //Intent intent = new Intent(getActivity(),AccountingListActivity.class);
                startActivity(intent);
            }
        });

        return view;

    }

}
