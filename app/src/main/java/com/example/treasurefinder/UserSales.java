package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class UserSales extends AppCompatActivity {
    Button btnViewSales, btnViewItems, btnMyProfile, btnAddSale;
    ListView lstMySales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sales);

        btnViewSales = findViewById(R.id.btnViewSales);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnAddSale = findViewById(R.id.btnAddSale);

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
}