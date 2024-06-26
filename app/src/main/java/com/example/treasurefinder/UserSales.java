package com.example.treasurefinder;

import androidx.activity.result.ActivityResultLauncher;
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

public class UserSales extends AppCompatActivity {
    TextView txtWelcome, txtNoSales;
    ArrayList<garageSale> sales;
    UserSaleAdapter adapter;
    Button btnViewSales, btnViewItems, btnMyProfile, btnAddSale, btnLogout;
    ListView lstMySales;
    RequestQueue queue;
    String URL = "https://treasurefinderbackend.onrender.com/user/garageSales";
    //result launcher to check when returned from adding sale
    ActivityResultLauncher resultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sales);

        sales = new ArrayList<>();
        queue = Volley.newRequestQueue(this.getApplicationContext());
        btnViewSales = findViewById(R.id.btnViewSales);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnAddSale = findViewById(R.id.btnAddItems);
        lstMySales = findViewById(R.id.lstUserItems);
        txtWelcome = findViewById(R.id.txtWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        txtNoSales = findViewById(R.id.txtNoSales);

        //launch activities for nav bar
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

        //result launcher for when sale is added
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result-> {
            Log.d("result", "Activity finished");
            //get update on return and refresh adapter
            getSales();

            adapter = new UserSaleAdapter(sales, this.getApplicationContext());
            lstMySales.setAdapter(adapter);
        });
        //launch result launcher for if a sale was added
        btnAddSale.setOnClickListener(v->{
            Intent addSaleIntent = new Intent(UserSales.this, AddNewSale.class);;
            setResult(222, addSaleIntent);
            resultLauncher.launch(addSaleIntent);
        });

        //logout button
        btnLogout.setOnClickListener(e-> {
            String URLlogout = "https://treasurefinderbackend.onrender.com/users/logout";
            //launch login activity and post for logout
            Intent logout = new Intent(UserSales.this, MainActivity.class);
            JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URLlogout, null, response -> {
                Log.d("Logout", "Successfully logged out");
            },error -> {});
            queue.add(r);
            startActivity(logout);
            finish();
        });

        getSales();

        adapter = new UserSaleAdapter(sales, this.getApplicationContext());
        lstMySales.setAdapter(adapter);



    }
    //get sales for adapter
    public void getSales() {
        sales = new ArrayList<>();
        JSONObject j = new JSONObject();
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, URL, j, response -> {
            Log.d("Sales", response.toString());
            try {
                //parse json for sale information
                JSONObject user = (JSONObject) response.get("users");
                JSONArray jsonSales = user.getJSONArray("GarageSale");
                for(int i=0; i<jsonSales.length(); i++) {
                    //get all variables for sale
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
                    //add item string to array
                    String[] itemsArr = new String[items.length()];
                    for(int k=0; k<itemsArr.length; k++) {
                        itemsArr[k] = items.get(k).toString();
                    }
                    //create sale object and add to array
                    garageSale userSale = new garageSale(title, address, owner, date, hours, tuid, itemsArr);
                    sales.add(userSale);
                    adapter.notifyDataSetChanged();
                }
                if(sales.isEmpty())
                    txtNoSales.setVisibility(View.VISIBLE);
                else
                    txtNoSales.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Log.d("Sales", error.toString());
        });
        //add to queue
        queue.add(r);
    }
}