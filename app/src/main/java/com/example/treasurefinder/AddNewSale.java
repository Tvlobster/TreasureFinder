package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNewSale extends AppCompatActivity {
    Button btnSave, btnCancel;
    EditText etSaleName, etDate, etAddress, etStartTime, etEndTime;
    RequestQueue queue;
    String URL = "https://treasurefinderbackend.onrender.com/seller/newGarageSale";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_sale);

        btnSave = findViewById(R.id.btnAddItemSave);
        btnCancel = findViewById(R.id.btnAddItemCancel);

        etSaleName = findViewById(R.id.etAddItemName);
        etDate = findViewById(R.id.etAddPrice);
        etAddress = findViewById(R.id.etAddItemDesc);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        queue = Volley.newRequestQueue(this.getApplicationContext());

        btnCancel.setOnClickListener(v->{
           Intent profileIntent = new Intent(AddNewSale.this, UserSales.class);
           startActivity(profileIntent);
           finish();
        });

        btnSave.setOnClickListener(v->{
            try {
                saveNewSale();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void saveNewSale() throws JSONException, ParseException {
        String title = etSaleName.getText().toString();
        String address = etAddress.getText().toString();
        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = formatter.parse(etDate.getText().toString());

        if(etDate.getText().equals("") || title.equals("") || address.equals("") || startTime.equals("") || endTime.equals("")){
            Toast.makeText(this, "Please fill in each field. No fields can be left blank...", Toast.LENGTH_SHORT).show();
        }else{
            JSONObject j = new JSONObject();
            j.put("title", title);
            j.put("date", date);
            j.put("address", address);
            j.put("startTime", Integer.parseInt(startTime));
            j.put("endTime", Integer.parseInt(endTime));

            JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, j, response -> {
                Toast.makeText(this, "Sale created successfully!", Toast.LENGTH_SHORT).show();
                Intent it = new Intent();
                setResult(222, it);
                finish();
            }, error -> {
                Toast.makeText(this, "Sale could not be added...", Toast.LENGTH_SHORT).show();
                finish();
            });

            queue.add(r);
        }
    }
}