package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserSales extends AppCompatActivity {
    TextView txtWelcome;
    ArrayList<garageSale> sales;
    UserSaleAdapter adapter;
    Button btnViewSales, btnViewItems, btnMyProfile, btnAddSale, btnLogout;
    ListView lstMySales;
    RequestQueue queue;
    String URL = "https://treasurefinderbackend.onrender.com/user/garageSales";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sales);

        sales = new ArrayList<>();
        queue = Volley.newRequestQueue(this.getApplicationContext());
        btnViewSales = findViewById(R.id.btnViewSales);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnAddSale = findViewById(R.id.btnAddSale);
        lstMySales = findViewById(R.id.lstSales);
        txtWelcome = findViewById(R.id.txtWelcome);
        btnLogout = findViewById(R.id.btnLogout);

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

        btnLogout.setOnClickListener(e-> {
            String URLlogout = "https://treasurefinderbackend.onrender.com/users/logout";
            Intent logout = new Intent(UserSales.this, Login.class);
            JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URLlogout, null, response -> {
                Log.d("Logout", "Successfully logged out");
            },error -> {});
            queue.add(r);
            startActivity(logout);
        });

        getSales();

        adapter = new UserSaleAdapter(sales, this.getApplicationContext());
        lstMySales.setAdapter(adapter);

    }

    public void getSales() {
        JSONObject j = new JSONObject();
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, URL, j, response -> {
            Log.d("Sales", response.toString());
            try {
                JSONObject user = (JSONObject) response.get("users");
                JSONArray jsonSales = user.getJSONArray("GarageSale");
                for(int i=0; i<jsonSales.length(); i++) {
                    Log.d("Sale", jsonSales.get(i).toString());
                    JSONObject sale = (JSONObject) jsonSales.get(i);
                    String title = sale.getString("title");
                    String date = sale.getString("date");
                    String owner = sale.getString("owner");
                    String address = sale.getString("address");
                    String startTime = sale.getString("startTime");
                    String endTime = sale.getString("endTime");
                    String hours = startTime + "-" + endTime;
                    String tuid = sale.getString("_id");
                    JSONArray items = sale.getJSONArray("items");
                    String[] itemsArr= new String[items.length()];
                    for(int k=0; k<items.length();k++)
                        itemsArr[k] = (String) items.get(k);

                    garageSale userSale = new garageSale(title, address, owner, date, hours, tuid, itemsArr);
                    sales.add(userSale);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Log.d("Sales", error.toString());
        });
        queue.add(r);
    }
}