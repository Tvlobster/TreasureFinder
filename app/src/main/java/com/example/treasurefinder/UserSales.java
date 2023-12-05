package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSales extends AppCompatActivity {
    Button btnViewSales, btnViewItems, btnMyProfile, btnAddSale;
    ListView lstMySales;
    String userID;
    RequestQueue queue;
    String URL = "https://treasurefinderbackend.onrender.com/user/garageSales";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sales);

        btnViewSales = findViewById(R.id.btnViewSales4);
        btnViewItems = findViewById(R.id.btnViewItems3);
        btnMyProfile = findViewById(R.id.btnMyProfile3);
        btnAddSale = findViewById(R.id.btnAddSale);
        lstMySales = findViewById(R.id.lstSales);

        try {
            getSales();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        btnViewSales.setOnClickListener(v->{
            Intent salesIntent = new Intent(UserSales.this, SalesActivity.class);
            startActivity(salesIntent);
        });

        btnViewItems.setOnClickListener(v->{
            Intent itemsIntent = new Intent(UserSales.this, ItemsActivity.class);
            startActivity(itemsIntent);
        });

        btnMyProfile.setOnClickListener(v->{
            Intent profileIntent = new Intent(UserSales.this, UserSales.class);
            startActivity(profileIntent);
        });

        btnAddSale.setOnClickListener(v->{
            Intent addSaleIntent = new Intent(UserSales.this, AddNewSale.class);
            startActivity(addSaleIntent);
        });

    }

    public void getSales() throws JSONException {
        Intent intent = this.getIntent();
        userID = intent.getStringExtra("userID");
        queue = Volley.newRequestQueue(this.getApplicationContext());

        JSONObject j = new JSONObject();
//        j.put("userID", userID);
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, URL, j, response -> {
            Log.d("User", response.toString());
        }, error -> {
            Log.d("User", error.toString());
        });
        queue.add(r);
    }
}