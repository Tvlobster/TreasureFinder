package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsActivity extends AppCompatActivity {
    TextView txtName;
    TextView txtTitle;
    TextView txtAddress;
    TextView txtNum;
    ListView lstItems;
    ItemAdapter adapter;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        items = new ArrayList<>();

        txtAddress = findViewById(R.id.txtAddress);
        txtName = findViewById(R.id.txtName);
        txtNum = findViewById(R.id.txtNum);
        txtTitle = findViewById(R.id.txtTitle);
        lstItems = findViewById(R.id.lstItems);

        items.add(new Item("Lamp", 25, "Needs new bulb"));
        items.add(new Item("Sofa", 200, "Slight use but mostly new"));
        items.add(new Item("Desk", 70, "New"));

        adapter = new ItemAdapter(items, this);
        lstItems.setAdapter(adapter);
    }
}