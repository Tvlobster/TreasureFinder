package com.example.treasurefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class SalesActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    ToggleButton tgView;
    ListView lstSales;

    ArrayList<garageSale> sales;

    SaleAdapter adapter;


SeekBar seekRange;
TextView txtRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        tgView = findViewById(R.id.tgView);
        lstSales = findViewById(R.id.lstSales);



        sales = new ArrayList<>();
        sales.add(new garageSale("Goldie's Oldies", "Goldie Germaine", "9201 Allan Rd.", "10:00 AM - 5:00 PM", 1));
        sales.add(new garageSale("Jim Bobs Fishing", "Jim Bobbo", "9123 Cherry St.", "10:00 AM - 5:00 PM", 2));
        sales.add(new garageSale("Brianna's Oddities", "Brianna Kline", "9201 Winter Rd.", "11:00 AM - 4:00 PM", 3));

        adapter = new SaleAdapter(this,sales);




        lstSales.setAdapter(adapter);
        lstSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SalesActivity.this, SaleDetail.class);
                startActivity(intent);
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        tgView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){

                    FragmentManager fm = getFragmentManager();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .hide(mapFragment)
                            .commit();

                    lstSales.setVisibility(View.VISIBLE);




                }
                else{
                    FragmentManager fm = getFragmentManager();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .show(mapFragment)
                            .commit();
                    lstSales.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(new PersonInfoWindow());
        LatLng nyc = new LatLng(40.7443679675679, -73.98867886292477);
        map.moveCamera(CameraUpdateFactory.newLatLng(nyc));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Log.d("MyMap", "Map was clicked..");
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {

                Marker m = map.addMarker(new MarkerOptions().position(latLng));
                /*
                Person p = new Person("John", 23, 100000);
                m.setTitle("Name: " + p.name);
                m.setSnippet("Age : " + p.age);
                m.setTag(p);

                 */
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
               // Toast.makeText(SalesActivity.this, "Hello", Toast.LENGTH_LONG).show();
                //Create a new intent to open up a page with the sale
                Intent intent = new Intent(SalesActivity.this, SaleDetail.class);
                startActivity(intent);



            }
        });

    }


    class PersonInfoWindow implements GoogleMap.InfoWindowAdapter {

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            View v = LayoutInflater.from(SalesActivity.this).inflate(R.layout.layout_customwindow,null);
           /* TextView txtName = v.findViewById(R.id.txtTitle);
            TextView txtAge = v.findViewById(R.id.txtHours);
            Person p = (Person)marker.getTag();
            txtAge.setText("Age: +"+p.age+"");
            txtName.setText("Name: "+p.name);*/
            return v;
        }
    }


}



