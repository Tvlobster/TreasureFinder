package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {
    TextView txtItemsName;
    TextView txtItemsTitle;
    TextView txtItemsAddress;
    TextView txtItemsNum;
    ListView lstItems;
    ItemAdapter adapter;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        items = new ArrayList<>();

        txtItemsAddress = findViewById(R.id.txtItemsAddress);
        txtItemsName = findViewById(R.id.txtItemsName);
        txtItemsNum = findViewById(R.id.txtIitemsNum);
        txtItemsTitle = findViewById(R.id.txtItemsTitle);
        lstItems = findViewById(R.id.lstItems);

        items.add(new Item("Lamp", 25, "Needs new bulb"));
        items.add(new Item("Sofa", 200, "Slight use but mostly new"));
        items.add(new Item("Desk", 70, "New"));

        adapter = new ItemAdapter(items, this);
        lstItems.setAdapter(adapter);
    }
}