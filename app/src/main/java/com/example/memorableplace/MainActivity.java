package com.example.memorableplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inspector.StaticInspectionCompanionProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView place_list;
    static ArrayList<LatLng> points = new ArrayList<LatLng>();
    static ArrayList<String> addresses= new ArrayList<String>();
    static ArrayAdapter arrayAdapter;
    SharedPreferences sharedPreferences;
    ArrayList<String> lat = new ArrayList<String>();
    ArrayList<String> lng = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        place_list = findViewById(R.id.place_list);

        //used shared preference for data storage
        addresses.clear();
        lat.clear();
        lng.clear();
        points.clear();
        sharedPreferences = this.getSharedPreferences("com.example.memorableplace", Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit(); // used if error occurs in shared preference


        try {
            addresses = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("addresses",ObjectSerializer.serialize(new ArrayList<String>())));

            lat = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lat",ObjectSerializer.serialize(new ArrayList<String>())));
            lng = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lng",ObjectSerializer.serialize(new ArrayList<String>())));

            if (lat.size()>0 && lng.size()>0 && addresses.size()>0) {
                if(lat.size() == lng.size()) {
                    for (int i = 0; i < lat.size(); i++) {

                        points.add(new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(lng.get(i))));
                    }
                }
            } else {
                addresses.add("Add your place by tapping here.");
            }



        } catch (IOException e) {
            e.printStackTrace();
        }




        arrayAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,addresses);
        place_list.setAdapter(arrayAdapter);

        place_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("list_number",i);
                startActivity(intent);
            }
        });

    }
}