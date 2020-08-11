package com.example.memorableplace;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnown;
    Geocoder geocoder;
    String address = "";
    int list_number;
    int move_count = 0;




    //update the location in the location listener
    public void updateLocationListener() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateLocationListener();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent intent = getIntent();
        list_number = intent.getIntExtra("list_number",0);

        //set up the geocoder
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        //set up long press function on map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //get the address
                try {
                    address = "";
                    List<Address> address_list = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    if (address_list!= null && address_list.size()>0) {
                        Log.i("location", address_list.get(0).toString());
                        if (address_list.get(0).getFeatureName()!= null) {
                            address+= address_list.get(0).getFeatureName()+ ", ";
                        }
                        if (address_list.get(0).getThoroughfare()!= null) {
                            address+= address_list.get(0).getThoroughfare()+ ", ";
                        }
                        if (address_list.get(0).getSubAdminArea()!= null) {
                            address+= address_list.get(0).getSubAdminArea()+ ", ";
                        }
                        if (address_list.get(0).getAdminArea()!= null) {
                            address+= address_list.get(0).getAdminArea();
                        }
                    }
                    if (address.equals("")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        address += sdf.format(new Date());
                    }
                    //add the marker
                    mMap.addMarker(new MarkerOptions().position(latLng).title(address));

                    //add the Latlng and the address into the array
                    MainActivity.points.add(latLng);
                    MainActivity.addresses.add(address);

                    //add the address into List View
                    MainActivity.arrayAdapter.notifyDataSetChanged();

                    //add the new array into the shared preference
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.memorableplace", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("addresses",ObjectSerializer.serialize(MainActivity.addresses)).apply();
                    ArrayList<String> latitudes = new ArrayList<String>();
                    ArrayList<String> longitudes = new ArrayList<String>();
                    for (LatLng coord: MainActivity.points) {
                        latitudes.add(Double.toString(coord.latitude));
                        longitudes.add(Double.toString((coord.longitude)));
                    }
                    sharedPreferences.edit().putString("lat",ObjectSerializer.serialize(latitudes)).apply();
                    sharedPreferences.edit().putString("lng",ObjectSerializer.serialize(longitudes)).apply();

                    //toast the message
                    Toast.makeText(MapsActivity.this, "New location added.", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });



        if (list_number == 0) {

            mMap.clear();
            //set up the location manager and listener
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {


                    // Add a marker and move the camera
                    LatLng currentloc = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentloc).title("Current Location"));
                    if (move_count == 0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentloc,15));
                        move_count++;
                        //to avoid the camera from centering on the current location frequently
                    }

                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
                @Override
                public void onProviderEnabled(@NonNull String provider) {

                }
                @Override
                public void onProviderDisabled(@NonNull String provider) {

                }

            };
            // if ''add place here'' is selected, we would update user's current location
            updateLocationListener();

        } else {
            // Add a marker and move the camera to the selected location
            LatLng tappedloc = MainActivity.points.get(list_number-1);
            mMap.addMarker(new MarkerOptions().position(tappedloc).title(MainActivity.addresses.get(list_number)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tappedloc,15));

        }


    }
}