package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddNewItem extends AppCompatActivity {
    EditText etAddItemName, etAddItemPrice, etAddItemDescription;
    Button btnSave, btnCancel;
    String URL = "https://treasurefinderbackend.onrender.com/seller/newItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);

        etAddItemDescription = findViewById(R.id.etAddItemDesc);
        etAddItemName = findViewById(R.id.etAddItemName);
        etAddItemPrice = findViewById(R.id.etAddPrice);
        btnCancel = findViewById(R.id.btnAddItemCancel);
        btnSave = findViewById(R.id.btnAddItemSave);

        btnSave.setOnClickListener(e-> {
            if (etAddItemDescription.equals("") || etAddItemName.equals("") || etAddItemPrice.equals(""))
                Toast.makeText(this, "Please fill in each field. No fields can be left blank...", Toast.LENGTH_SHORT).show();
            else {
                JSONObject j = new JSONObject();
                try {
                    j.put("name", etAddItemName.getText());
                    j.put("description", etAddItemDescription.getText());
                    j.put("price", etAddItemPrice.getText());
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

    }
}