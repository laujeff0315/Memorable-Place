package com.example.memorableplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inspector.StaticInspectionCompanionProvider;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView place_list;
    static ArrayList<LatLng> points = new ArrayList<LatLng>();
    static ArrayList<String> addresses= new ArrayList<String>();
    static ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        place_list = findViewById(R.id.place_list);

        addresses = new ArrayList<String>();
        addresses.add("Add your place by tapping here.");

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