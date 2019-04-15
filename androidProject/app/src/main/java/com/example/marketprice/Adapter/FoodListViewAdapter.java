package com.example.marketprice.Adapter;


import com.example.marketprice.ListVO.listVO;
import com.example.marketprice.R;
import com.example.marketprice.SearchAroundFood;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
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

        Log.d("Tag : ","" + listViewItem.getImg());

//        try {
//            URL url = new URL(listViewItem.getImg());
//            URLConnection conn = url.openConnection();
//            conn.connect();
//            BufferedInputStream bis = new
//                    BufferedInputStream(conn.getInputStream());
//            Bitmap bm = BitmapFactory.decodeStream(bis);
//            bis.close();
//            image.setImageBitmap(bm);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        image.setImageURI(null);
//        image.setImageURI(Uri.parse(listViewItem.getImg()));
        Picasso.with(parent.getContext()).load(listViewItem.getImg()).into(image);
        name.setText(listViewItem.getName());
        price.setText(listViewItem.getPrice());


        // 리스트뷰 클릭 이벤트
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, (pos+1)+"번째 리스트가 클릭되었습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });

        return convertView;
    }

    public void addVO(String icon, String name, String price) {

        listVO item = new listVO();

        item.setImg(icon);
        item.setName(name);
        item.setPrice(price);

        listVO.add(item);
    }
}
