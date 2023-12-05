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

        try {
            getItems();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        adapter = new ItemAdapter(items, this);
        lstItems.setAdapter(adapter);

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

    public void getItems() throws JSONException {
        Intent intent = this.getIntent();
        userID = intent.getStringExtra("userID");
        Log.d("userID", userID);
        queue = Volley.newRequestQueue(this.getApplicationContext());

        JSONObject j = new JSONObject();

        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, URL, j, response -> {
            try{
                JSONArray listOfItems = response.getJSONArray("listOfItems");
                for(int i=0;i<listOfItems.length();i++){
                    JSONObject item = (JSONObject) listOfItems.get(i);
                    if(!item.getString("owner").equals(userID)){
                        Item newItem = new Item(item.getString("name"), item.getDouble("price"), item.getString("description"));
                        items.add(newItem);
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

