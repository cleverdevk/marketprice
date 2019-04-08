package com.example.marketprice.Adapter;


import com.example.marketprice.ListVO.listVO;
import com.example.marketprice.R;
import com.example.marketprice.SearchAroundFood;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FoodListViewAdapter extends BaseAdapter {

    private ArrayList<listVO> listVO = new ArrayList<listVO>() ;
    public FoodListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listVO.size();
    }

    @Override
    public Object getItem(int position) {
        return listVO.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.food_listview, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.img) ;
        TextView name = (TextView) convertView.findViewById(R.id.name) ;
        TextView price = (TextView) convertView.findViewById(R.id.price) ;

        listVO listViewItem = listVO.get(position);

        image.setImageDrawable(listViewItem.getImg());
        name.setText(listViewItem.getName());
        price.setText(listViewItem.getPrice());


        // 리스트뷰 클릭 이벤트
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, (pos+1)+"번째 리스트가 클릭되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    public void addVO(Drawable icon, String name, String price) {
        listVO item = new listVO();

        item.setImg(icon);
        item.setName(name);
        item.setPrice(price);

        listVO.add(item);
    }
}
