package com.example.marketprice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;


public class SearchAroundTransportation extends Fragment implements OnMapReadyCallback {

    View v;

    LatLng current = new LatLng(0,0);

    double myLat;
    double myLng;

    private ListView listView;
//    private TransportListViewAdapter adapter;

    private EditText departText;
    private ImageButton departbtn;
    private EditText arrivalText;
    private ImageButton arrivalbtn;
    private Spinner spinner_field;
    private ArrayAdapter<String> item;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        v = inflater.inflate(R.layout.search_transport, container, false);


        listView = (ListView)v.findViewById(R.id.List_view);


        GoogleMap googleMap;

//        myLat = getArguments().getDouble("lat");
//        myLng = getArguments().getDouble("lng");

        Log.d("my location is : ", ""+ myLat + ", " + myLng);

        departText = (EditText) v.findViewById(R.id.depart);
        departbtn = (ImageButton) v.findViewById(R.id.depart_btn);
        arrivalText = (EditText) v.findViewById(R.id.arrival);
        arrivalbtn = (ImageButton) v.findViewById(R.id.arrival_btn);

        spinner_field = (Spinner) v.findViewById(R.id.spinner_field);
        String[] str = getResources().getStringArray(R.array.spinnerArray);
        item = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,str);

        item.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_field.setAdapter(item);

        spinner_field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),spinner_field.getSelectedItem().toString()+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return v;

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
