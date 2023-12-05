package com.example.treasurefinder;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.treasurefinder.databinding.ActivitySaleDetailBinding;

import java.util.ArrayList;

public class SaleDetail extends AppCompatActivity {


    private AppBarConfiguration appBarConfiguration;
    private ActivitySaleDetailBinding binding;
    String title, hours, date, owner, address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySaleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_sale_detail);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        Intent saleInfoIntent = getIntent();
        title = saleInfoIntent.getStringExtra("title");
        getSupportActionBar().setTitle(title);





        //listener for X button
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //exit out of the pop up screen
               finish();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_sale_detail);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public String getSaleInfo(){
        Intent i = getIntent();
        String hoursOperation = "Open on: "+i.getStringExtra("date")+" from "+ i.getStringExtra("hours");
        String information = title+";"+i.getStringExtra("address")+";"+i.getStringExtra("owner")+";"+hoursOperation;
        return information;
    }
    //this is to concantenate all item ID's that a sale owns
    public String getItemInfo(){
        Intent i = getIntent();
           String[] saleItems = i.getStringArrayExtra("items");
           String itemsString = "";
           for (int j = 0; j < saleItems.length; j++) {
               itemsString += saleItems[j] + ";";
           }
           Log.d("TEST", itemsString);

           return itemsString;


    }

}