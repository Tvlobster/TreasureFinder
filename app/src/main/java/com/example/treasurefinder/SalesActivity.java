package com.example.treasurefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.app.Activity;

import android.Manifest;

import android.app.FragmentManager;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Button;

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


import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;


public class SalesActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    ToggleButton tgView;
    ListView lstSales;

    ArrayList<garageSale> sales;

    SaleAdapter adapter;
    RequestQueue queue;
    Button btnSalesActivity, btnItemsActivity, btnProfileActivity;

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
        btnItemsActivity = findViewById(R.id.btnItemsActivity);
        btnSalesActivity = findViewById(R.id.btnSalesActivity);
        btnProfileActivity = findViewById(R.id.btnProfileActivity);
        //instantiate a new arrayList of garage sales
        sales = new ArrayList<>();

        //create a listener for the view Items button to launch the view items activity
        btnItemsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itemsIntent = new Intent();
                //startActivity();
            }
        });
        btnProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent();
                //startActivity();
            }
        });

        //create a listener to open the detail intent when a user clicks on a specific sale
        lstSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SalesActivity.this, SaleDetail.class);
                startActivity(intent); //launch intent to see more sale details
            }
        });

        //create a google map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //create a toggle view that allows the user to toggle between a the map view and the list view
        tgView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    //hide the map and make the list view of sales appear
                    FragmentManager fm = getFragmentManager();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .hide(mapFragment)
                            .commit();
                    //bring list into view
                    lstSales.setVisibility(View.VISIBLE);

                }
                else{
                    //hide the list view and allow the map view to appear
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


    }


//all logic to establish the map and markers and server request
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setInfoWindowAdapter(new garageSaleInfoWindow());
        //when map is ready, call to get server info. This method calls add markers
        requestInfo();


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Log.d("MyMap", "Map was clicked..");
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {

            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {

                garageSale sale = (garageSale) marker.getTag();


                //Create a new intent to open up a page with the sale details
                Intent intent = new Intent(SalesActivity.this, SaleDetail.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", sale.title);
                bundle.putString("address", sale.address);
                bundle.putString("hours", sale.hours);
                bundle.putString("TUID", sale.TUID);
                bundle.putStringArray("items", sale.items);
                bundle.putString("date", sale.date);




                intent.putExtra("saleObject", bundle);
                startActivity(intent);

            }
        });

    }

    //code to
    class garageSaleInfoWindow implements GoogleMap.InfoWindowAdapter {

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            View v = LayoutInflater.from(SalesActivity.this).inflate(R.layout.layout_customwindow,null);
            TextView txtTitle = v.findViewById(R.id.txtTitle);
            TextView txtAddress = v.findViewById(R.id.txtAddress);
            garageSale sale = (garageSale) marker.getTag();
            txtTitle.setText(sale.title+"");
           txtAddress.setText(sale.address+"");
            return v;
        }
    }


    //this method makes a request to the server to get garage sale information and creates an array of the information
    public void requestInfo(){
        //url to the server
        String url = "https://treasurefinderbackend.onrender.com/seller/allGarageSales";
        //create a new request queue
        queue = Volley.newRequestQueue(this.getApplicationContext());

        //create a JSON object to use for the request
        JSONObject j = new JSONObject();
        //create the JSON object request
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, url, j, response -> {
            String jsonResponse = response.toString(); // Convert the response to a string
            Log.d("MyLog", response.toString());
            try{
                //extract the object and array from the response
                JSONObject myObject = new JSONObject(jsonResponse);
                JSONArray listOfGarageSales = myObject.getJSONArray("listOfGarageSales");
                //extract information from each sale object in the array and create a new sale object
                for(int i = 0;i<listOfGarageSales.length();i++) {
                   // testing  JSONObject firstGarageSale = listOfGarageSales.getJSONObject(0);
                    JSONObject garageSale = listOfGarageSales.getJSONObject(i);
                    String title = garageSale.getString("title");
                    String date = garageSale.getString("date");
                    String startTime = garageSale.getString("startTime");
                    String endTime = garageSale.getString("endTime");
                    String address = garageSale.getString("address");
                    String TUID = garageSale.getString("id");
                    JSONArray JSONitems = garageSale.getJSONArray("items");
                    //get items from JSON array
                    String[] items = new String[JSONitems.length()];
                    for (int x = 0; x < JSONitems.length(); x++) {
                        items[x] = JSONitems.getString(x);
                    }

                    date = date.substring(0, 10);
                    String hours = startTime + " - " + endTime;

                    //create a new garage sale object for the array
                    garageSale newSale = new garageSale(title, address, date, hours, TUID, items);
                    //push new object to array
                    sales.add(newSale);
                    Log.d("MYTAG", newSale.toString());
                    Log.d("TEST", address);


                    //create a new adapter with all the sales and set the adapter for the list view
                    adapter = new SaleAdapter(this, sales);
                    lstSales.setAdapter(adapter);

                    //call this method to add markers based on retrieved sale locations
                    addMarkers();
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {
            Log.d("MyLog", error.toString() +" ");
            error.printStackTrace();
        });
        //add the request to the queue
        queue.add(r);

    }

    //this method adds markers on the map for each sale location in the sales array
    public void addMarkers(){
        //iterate through each sale object in the array
        for(int i=0;i<sales.size();i++){
            //create a geocoder to geocode the address to laditiude and longitude values
            Geocoder geocoder = new Geocoder(this);
            //try to convert if the address is valid
            try {
                List<Address> addresses = geocoder.getFromLocationName(
                        sales.get(i).address, 1);
                Address firstAddress = addresses.get(0);
                double latitude = firstAddress.getLatitude();
                double longitude = firstAddress.getLongitude();
                //for now move the camera to the area of the sale
                LatLng area = new LatLng(latitude, longitude);
                map.moveCamera(CameraUpdateFactory.newLatLng(area));
                //create a marker on the map for the sale object
                Marker m = map.addMarker(new MarkerOptions().position(area));
                //populate marker with info
                m.setTitle(sales.get(i).title + "");
                m.setSnippet(sales.get(i).address+"");
                m.setTag(sales.get(i));
            }
            catch(Exception ex){
                Log.d("TEST", "This is not a valid location, cannot place marker");
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



