package com.example.treasurefinder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class UserItems extends AppCompatActivity {
    Button btnViewSales, btnViewItems, btnMyProfile, btnAddItems;
    ListView lstItems;
    ArrayList<Item> items;
    TextView txtNoItems;
    String saleID;
    RequestQueue queue;
    String URL = "https://treasurefinderbackend.onrender.com/user/items";
    UserItemAdapter adapter;
    //result launcher to get added item to list view
    ActivityResultLauncher resultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_items);

        Intent intent = this.getIntent();
        saleID = intent.getStringExtra("saleID");
        Log.d("saleID", saleID);

        queue = Volley.newRequestQueue(this.getApplicationContext());
        items = new ArrayList<>();

        btnViewSales  = findViewById(R.id.btnViewSales);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnAddItems = findViewById(R.id.btnAddItems);
        txtNoItems = findViewById(R.id.txtNoItems);
        lstItems = findViewById(R.id.lstUserItems);
        //get items and add adapter to list view
        getItems();

        adapter = new UserItemAdapter(items, this.getApplicationContext());
        lstItems.setAdapter(adapter);

        //launch activity depending on button pressed
        btnViewSales.setOnClickListener(v->{
            Intent salesIntent = new Intent(UserItems.this, SalesActivity.class);
            startActivity(salesIntent);
        });

        btnViewItems.setOnClickListener(v->{
            Intent itemsIntent = new Intent(UserItems.this, ItemsActivity.class);
            startActivity(itemsIntent);
        });

        btnMyProfile.setOnClickListener(v->{
            Intent profileIntent = new Intent(UserItems.this, UserSales.class);
            startActivity(profileIntent);
        });

        //result launcher when item is added
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result-> {
            Log.d("result", "Activity finished");
            //check for new items and update adapter
            getItems();
            adapter = new UserItemAdapter(items, this.getApplicationContext());
            lstItems.setAdapter(adapter);
        });
        //button to add an item
        btnAddItems.setOnClickListener(e-> {
            //pass garage sale ID and launch using result launcher
            Intent addItemIntent = new Intent(this, AddNewItem.class);
            addItemIntent.putExtra("saleID", saleID);
            setResult(222, addItemIntent);
            resultLauncher.launch(addItemIntent);
        });




    }
    //get items for adapter
    public void getItems() {
        items = new ArrayList<>();
        JSONObject j = new JSONObject();
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, URL, j, response -> {
            try {
                Log.d("Items", saleID);
                //parse json for item array
                JSONObject user = (JSONObject) response.get("users");
                JSONArray itemsJSON = user.getJSONArray("Item");
                for(int i=0; i<itemsJSON.length(); i++) {
                    //create item from array if the garage sale IDs match
                    JSONObject itemJSON = itemsJSON.getJSONObject(i);
                    if(itemJSON.getString("saleId").equals(saleID)) {
                        String id = itemJSON.getString("_id");
                        String name = itemJSON.getString("name");
                        Double price = itemJSON.getDouble("price");
                        String description = itemJSON.getString("description");
                        items.add(new Item(name, price, description, id));
                        adapter.notifyDataSetChanged();
                    }
                }
                //check if items array is empty, and show text view indicating no items if necessary
                if(items.isEmpty())
                    txtNoItems.setVisibility(View.VISIBLE);
                else
                    txtNoItems.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Log.d("Error", error.toString());
        });
        queue.add(r);
    }


}