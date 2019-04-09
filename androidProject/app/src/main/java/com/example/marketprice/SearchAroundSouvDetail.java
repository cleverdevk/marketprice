package com.example.marketprice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchAroundSouvDetail extends Fragment {
    View v;

    ImageView img;
    TextView Name, Price;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.search_food_detail, container, false);

        img = (ImageView)v.findViewById(R.id.img);
        Name = (TextView)v.findViewById(R.id.name);
        Price = (TextView)v.findViewById(R.id.price);


        Bundle bundle = getArguments();

        if(bundle !=null) {
            img.setImageResource(bundle.getInt("img"));
            Name.setText(bundle.getString("Name"));
            Price.setText(bundle.getString("Price"));
        }

        return v;
    }


}
