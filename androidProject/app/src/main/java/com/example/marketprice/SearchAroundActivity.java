package com.example.marketprice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SearchAroundActivity extends Fragment {
    View v;

    LinearLayout foodView, souView, transView;

    private Context context;

    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //inflate메소드는 XML데이터를 가져와서 실제 View객체로 만드는 작업 수행
        v = inflater.inflate(R.layout.search_around, container, false);

        foodView = (LinearLayout) v.findViewById(R.id.foodView);
        souView = (LinearLayout) v.findViewById(R.id.souView);
        transView = (LinearLayout) v.findViewById(R.id.transView);

        context = container.getContext();



        foodView.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "food", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new SearchAroundFood())
                        .commit();
            }
        });

        souView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new SearchAroundSouv())
                        .commit();
                Toast.makeText(context, "souvenier", Toast.LENGTH_SHORT).show();

            }
        });

        transView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "transportation", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
