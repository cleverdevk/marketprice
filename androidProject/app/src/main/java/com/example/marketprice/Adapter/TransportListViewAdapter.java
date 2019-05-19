package com.example.marketprice.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marketprice.ListVO.listVO;
import com.example.marketprice.ListVO.transportlistVO;
import com.example.marketprice.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TransportListViewAdapter extends BaseAdapter {
    private ArrayList<transportlistVO> transportlistVOS = new ArrayList<>();
    public TransportListViewAdapter(){

    }


    @Override
    public int getCount() {
        return transportlistVOS.size();
    }

    @Override
    public Object getItem(int position) {
        return transportlistVOS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.transport_listview, parent, false);

        }
        TextView address = (TextView) convertView.findViewById(R.id.addressName) ;
        TextView distance = (TextView) convertView.findViewById(R.id.distance) ;
        TextView cost = (TextView) convertView.findViewById(R.id.cost) ;
        TextView time = (TextView)convertView.findViewById(R.id.time);

        transportlistVO listViewItem = transportlistVOS.get(position);

        address.setText(listViewItem.getDepart() + " ~ " + listViewItem.getArrival());
        distance.setText("총 거리 : " + listViewItem.getDistance() + "km");
        cost.setText("금액 : " + listViewItem.getCost() + " KRW");
        time.setText("시간 : " + listViewItem.getTime() + ":00");



        return convertView;
    }

    public void addVO(String depart, String arrival, String distance, String cost, String time) {

        transportlistVO item = new transportlistVO();

        item.setDepart(depart);
        item.setArrival(arrival);
        item.setDistance(distance);
        item.setCost(cost);
        item.setTime(time);

        transportlistVOS.add(item);
    }
}
