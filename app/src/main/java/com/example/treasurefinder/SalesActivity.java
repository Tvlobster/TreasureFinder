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

import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;

import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.Toast;
import android.widget.ToggleButton;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


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
    Boolean locationFlag = true;
    Button btnSalesActivity, btnItemsActivity, btnProfileActivity;

    public static final String TAG = "NotifServiceTag";

    public static final int NOTIFICATION_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        //initialize views
        tgView = findViewById(R.id.tgView);
        lstSales = findViewById(R.id.lstSales);
        btnItemsActivity = findViewById(R.id.btnItemsActivity);
        btnSalesActivity = findViewById(R.id.btnSalesActivity);
        btnProfileActivity = findViewById(R.id.btnProfileActivity);

        //recieve the userID of the currently logged in user from main activity
        Intent loginIntent = getIntent();
        String userID = loginIntent.getStringExtra("ID");


        //instantiate a new arrayList of garage sales
        sales = new ArrayList<>();

        //create a listener for the view Items button to launch the view items activity
        btnItemsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send userID to other activities
                Intent itemsIntent = new Intent(SalesActivity.this, ItemsActivity.class);
                itemsIntent.putExtra("userID", userID);
                startActivity(itemsIntent);
            }
        });
        btnProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send userID to to other activities using a button
                Intent profileIntent = new Intent(SalesActivity.this, UserSales.class);
                profileIntent.putExtra("userID", userID);
                startActivity(profileIntent);
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
                if (buttonView.isChecked()) {
                    //hide the map and make the list view of sales appear
                    FragmentManager fm = getFragmentManager();
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .hide(mapFragment)
                            .commit();
                    //bring list into view
                    lstSales.setVisibility(View.VISIBLE);

                } else {
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

        //create an intent for the notification service to begin
        Intent i = new Intent(this, NotificationService.class);
        startForegroundService(i);

        lstSales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //cast the object as a garagesale object to get the data attributes
                garageSale clickedItem = (garageSale) parent.getItemAtPosition(position);

                //if an item is clicked
                if (clickedItem != null) {
                    String text = clickedItem.toString();

                    //Create a new intent to open up a page with the sale details
                    Intent intent = new Intent(SalesActivity.this, SaleDetail.class);
                    intent.putExtra("title", clickedItem.title);
                    intent.putExtra("address", clickedItem.address);
                    intent.putExtra("owner", clickedItem.owner);
                    intent.putExtra("hours", clickedItem.hours);
                    intent.putExtra("TUID", clickedItem.TUID);
                    intent.putExtra("items", clickedItem.items);
                    intent.putExtra("date", clickedItem.date);

                    startActivity(intent);

                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;
        map.setInfoWindowAdapter(new garageSaleInfoWindow());
        //when map is ready, call to get the user's current location and move the camera to that location
        getCurrentLocation();
        //Next, request info from the server to place markers on the map
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

        //on click listener for the info window preview of a garage sale
        //when a user clicks on the window, open up a new detail page about the sale
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                //get tag marker object
                garageSale sale = (garageSale) marker.getTag();


                //Create a new intent to open up a page and pass in the sale details
                Intent intent = new Intent(SalesActivity.this, SaleDetail.class);
                intent.putExtra("title", sale.title);
                intent.putExtra("address", sale.address);
                intent.putExtra("owner", sale.owner);
                intent.putExtra("hours", sale.hours);
                intent.putExtra("TUID", sale.TUID);
                intent.putExtra("items", sale.items);
                intent.putExtra("date", sale.date);

                startActivity(intent);

            }
        });

    }

    //code to create the info window preview
    class garageSaleInfoWindow implements GoogleMap.InfoWindowAdapter {

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            View v = LayoutInflater.from(SalesActivity.this).inflate(R.layout.layout_customwindow, null);
            //initialize the views on the info window and populate the data
            TextView txtTitle = v.findViewById(R.id.txtTitle);
            TextView txtAddress = v.findViewById(R.id.txtAddress);
            garageSale sale = (garageSale) marker.getTag();
            txtTitle.setText(sale.title + "");
            txtAddress.setText(sale.address + "");
            return v;
        }
    }


    //this method makes a request to the server to get garage sale information and creates an array of the information
    public void requestInfo() {
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
            try {
                //extract the object and array from the response
                JSONObject myObject = new JSONObject(jsonResponse);
                JSONArray listOfGarageSales = myObject.getJSONArray("listOfGarageSales");
                //extract information from each sale object in the response and create a new sale object
                for (int i = 0; i < listOfGarageSales.length(); i++) {
                    //extract the data from JSON object fields
                    JSONObject garageSale = listOfGarageSales.getJSONObject(i);
                    String title = garageSale.getString("title");
                    String date = garageSale.getString("date");

                    JSONArray userArray = garageSale.getJSONArray("User");
                    JSONObject userObject = userArray.getJSONObject(0);
                    String username = userObject.getString("username");
                   // Log.d("TAG", username);

                    String startTime = garageSale.getString("startTime");
                    String endTime = garageSale.getString("endTime");
                    String address = garageSale.getString("address");
                    String TUID = garageSale.getString("id");
                    JSONArray JSONitems = garageSale.getJSONArray("items");
                    //get items from JSON  items array array and store in a string array
                    String[] items = new String[JSONitems.length()];
                    for (int x = 0; x < JSONitems.length(); x++) {
                        items[x] = JSONitems.getString(x);
                    }
                    //format the date data
                    date = date.substring(0, 10);
                    String hours = startTime + " - " + endTime;

                    //create a new garage sale object for the array
                    garageSale newSale = new garageSale(title, address, username, date, hours, TUID, items);
                    //push new object to array
                    sales.add(newSale);
                   // Log.d("MYTAG", newSale.toString());
                  //  Log.d("TEST", address);


                    //create a new adapter with all the sales and set the adapter for the list view
                    adapter = new SaleAdapter(this, sales);
                    lstSales.setAdapter(adapter);




                }
                //call this method to add markers based on retrieved sale locations
                addMarkers();

            } catch (JSONException e) {

            }

        }, error -> {
            Log.d("MyLog", error.toString() + " ");
            error.printStackTrace();
        });
        //add the request to the queue
        queue.add(r);

    }

    //this method adds markers on the map for each sale location in the sales array
    public void addMarkers() {
        //iterate through each sale object in the array
        for (int i = 0; i < sales.size(); i++) {
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

                //create a marker on the map for the sale object
                Marker m = map.addMarker(new MarkerOptions().position(area));
               // map.moveCamera(CameraUpdateFactory.newLatLng(area));
                //populate marker with info
                m.setTitle(sales.get(i).title + "");
                m.setSnippet(sales.get(i).address + "");
                m.setTag(sales.get(i));
            } catch (Exception ex) {
                Log.d("TEST", "This is not a valid location, cannot place marker");
            }

        }
        if(locationFlag ==false){
            try{
                    //move camera to first location in DB
                    Geocoder geocoder = new Geocoder(this);
                    List<Address> addresses = geocoder.getFromLocationName(
                            sales.get(0).address, 1);
                    Address firstAddress = addresses.get(0);
                    double latitude = firstAddress.getLatitude();
                    double longitude = firstAddress.getLongitude();
                    //for now move the camera to the area of the sale
                    LatLng area = new LatLng(latitude, longitude);
                    map.moveCamera(CameraUpdateFactory.newLatLng(area));
                    locationFlag = true;

                }
            catch (Exception ex){
                Log.d(TAG, "There are no sales currently");
            }
        }

    }
    //use this method to get the user's current location to see their sales nearby
    public void getCurrentLocation() {
        //create a FLP client
        FusedLocationProviderClient flpClient = LocationServices.getFusedLocationProviderClient(this);
        //if permissions are not granted, check the boolean value to move the camera to an active sale rather than current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location access was denied...");
            locationFlag = false;
            return;
        }
        flpClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(location -> {
       //if successful return of device location
            if(location != null) {
                Log.d(TAG, location.toString());
                Geocoder geocoder = new Geocoder(SalesActivity.this);
                LatLng area = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(area));
                Log.d("Location", area.toString());
            }
        //if location returns null, the device could not retireve location. Flip the flag to move the camera to an active sale.
            //handles the error
            else
                locationFlag = false;

        });

        //the FLP client fails and the boolean flag is flipped to handle the error and  move the camera elsewhere
        flpClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
                locationFlag = false;
            }
        });

    }


}



