package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AddNewItem extends AppCompatActivity {
    EditText etAddItemName, etAddItemPrice, etAddItemDescription;
    Button btnSave, btnCancel;
    RequestQueue queue;
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

        queue = Volley.newRequestQueue(this.getApplicationContext());
        //button to add an item
        btnSave.setOnClickListener(e-> {
            //check if all fields are filled
            if (etAddItemDescription.getText().toString().equals("") || etAddItemName.getText().toString().equals("") || etAddItemPrice.getText().toString().equals(""))
                Toast.makeText(this, "Please fill in each field. No fields can be left blank...", Toast.LENGTH_SHORT).show();
            else {
                //get sale id
                Intent intent = this.getIntent();
                String saleID = intent.getStringExtra("saleID");
                JSONObject j = new JSONObject();
                try {
                    //add all variables to a JSON object
                    j.put("name", etAddItemName.getText());
                    j.put("description", etAddItemDescription.getText());
                    j.put("price", etAddItemPrice.getText());
                    j.put("saleId", saleID);
                    //use the url to make the item, and finish with the result launcher code
                    JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, j, response -> {
                        Toast.makeText(this, "Item created successfully!", Toast.LENGTH_SHORT).show();
                        Intent it = new Intent();
                        setResult(222, it);
                        finish();
                    }, error -> {
                        Toast.makeText(this, "Item could not be added...", Toast.LENGTH_SHORT).show();
                        finish();
                    });

                    queue.add(r);
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        //if cancelled return to profile page
        btnCancel.setOnClickListener(e-> {
            finish();
        });

    }
}