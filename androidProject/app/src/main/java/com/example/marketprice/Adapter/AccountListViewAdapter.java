package com.example.marketprice.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.marketprice.ListVO.listAC;
import com.example.marketprice.R;

import java.util.ArrayList;

public class AccountListViewAdapter extends BaseAdapter {
    private ArrayList<listAC> listAC = new ArrayList<com.example.marketprice.ListVO.listAC>();
    public AccountListViewAdapter() {

    }

    public ArrayList<listAC> getListAC() { return listAC; }

    public void setListAC(ArrayList<listAC> listAC) { this.listAC = listAC; }

    public int getCount() { return listAC.size(); }

    @Override
    public Object getItem(int position) { return listAC.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.account_listview, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView detail = (TextView) convertView.findViewById(R.id.detail);
        TextView total = (TextView) convertView.findViewById(R.id.total);

        listAC listViewItem = listAC.get(position);

        title.setText(listViewItem.getTitle());
        date.setText(listViewItem.getDate());
        detail.setText(listViewItem.getDetail());
        total.setText(listViewItem.getTotal());

        return convertView;
    }

    public void addAC(String title, String date, String detail, String total) {

        listAC item = new listAC();

        item.setTitle(title);
        item.setDate(date);
        item.setDetail(detail);
        item.setTotal(total);

        listAC.add(item);
    }
}
