package com.example.marketprice.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.marketprice.R;

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

        Bundle extra = getArguments();
        final String strID = extra.getString("id");

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //가계부 보기 전환 넣을 곳
                Log.d("[INBAE]", "BtnView Clicked!");
                Intent intent = new Intent(getActivity(), SearchAccountActivity.class);
                startActivity(intent);
            }
        });
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("[INBAE]", "btnWrite Clicked!");
                //Intent intent = new Intent(getActivity(),AccountingWriteActivity.class);
                Intent intent = new Intent(getActivity(),AccountingListActivity.class);
                intent.putExtra("id", strID);
                startActivity(intent);
            }
        });

        return view;

    }

}
