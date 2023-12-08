package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {
    String userID;
    ListView lstItems;
    ItemAdapter adapter;
    ArrayList<Item> items;
    RequestQueue queue;
    Button btnViewSales, btnViewItems, btnMyProfile;
    String URL = "https://treasurefinderbackend.onrender.com/items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);


        items = new ArrayList<>();

        lstItems = findViewById(R.id.lstItems);
        btnViewItems = findViewById(R.id.btnItemsActivity4);
        btnViewSales = findViewById(R.id.btnSalesActivity3);
        btnMyProfile = findViewById(R.id.btnProfileActivity3);
        //get items, set adapter and show in list view
        try {
            getItems();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        adapter = new ItemAdapter(items, this);
        lstItems.setAdapter(adapter);
        //buttons for nav bar to launch different activity
        btnViewSales.setOnClickListener(v->{
            Intent salesIntent = new Intent(ItemsActivity.this, SalesActivity.class);
            salesIntent.putExtra("userID", userID);
            startActivity(salesIntent);
        });

        btnMyProfile.setOnClickListener(v->{
            Intent profileIntent = new Intent(ItemsActivity.this, UserSales.class);
            profileIntent.putExtra("userID", userID);
            startActivity(profileIntent);
        });

    }
    //get all items not from the user
    public void getItems() throws JSONException {
        //get user ID
        Intent intent = this.getIntent();
        userID = intent.getStringExtra("userID");
        queue = Volley.newRequestQueue(this.getApplicationContext());
        //get url for all items
        JSONObject j = new JSONObject();
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, URL, j, response -> {
            try{
                JSONArray listOfItems = response.getJSONArray("listOfItems");
                //for each item
                for(int i=0;i<listOfItems.length();i++){
                    JSONObject item = (JSONObject) listOfItems.get(i);
                    //check if item matches the current user
                    if(!item.getString("owner").equals(userID)){
                        //if not belonging to the user, create an item object and add to array list
                        Item newItem = new Item(item.getString("name"), item.getDouble("price"), item.getString("description"), item.getString("_id"));
                        items.add(newItem);
                        //notify that item was added
                        adapter.notifyDataSetChanged();
                    }
                    Log.d("Item", item.toString());
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Log.d("User", error.toString());
        });

        queue.add(r);
    }
}

