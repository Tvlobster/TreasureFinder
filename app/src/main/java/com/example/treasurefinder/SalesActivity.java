package com.example.treasurefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class SalesActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    ToggleButton tgView;
    ListView lstSales;

    ArrayList<garageSale> sales;

    SaleAdapter adapter;
    RequestQueue queue;

    public static final String TAG = "NotifServiceTag";

    public static final int NOTIFICATION_REQUEST_CODE = 1;

SeekBar seekRange;
TextView txtRange;

String url = "";

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

        queue = Volley.newRequestQueue(this);


        //query the DB for sales and add to data structure
        //create a marker for each sale
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

        checkPermissions();
        Intent i = new Intent(this, NotificationService.class);
        startForegroundService(i);
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(new PersonInfoWindow());
        LatLng nyc = new LatLng(40.7443679675679, -73.98867886292477);
        map.moveCamera(CameraUpdateFactory.newLatLng(nyc));

        String url = "https://treasurefinderbackend.onrender.com/seller/allGarageSales";
        queue = Volley.newRequestQueue(this.getApplicationContext());

        JSONObject j = new JSONObject();

        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, url, j, response -> {
            String jsonResponse = response.toString(); // Convert the response to a string
            Log.d("MyLog", response.toString());
        }, error -> {
            Log.d("MyLog", error.toString() +" ");
            error.printStackTrace();
        });

        queue.add(r);

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

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions NOT granted, requesting....");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Permissions already granted");
        }
    }

}



