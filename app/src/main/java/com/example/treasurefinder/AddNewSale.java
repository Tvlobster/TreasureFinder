package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
        //if canceled, return to profile page
        btnCancel.setOnClickListener(v->{
           Intent profileIntent = new Intent(AddNewSale.this, UserSales.class);
           startActivity(profileIntent);
           finish();
        });


        etDate.setOnClickListener(e->{

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewSale.this,
                    (DatePickerDialog.OnDateSetListener) (view, year1, month1, dayOfMonth) -> {
                etDate.setText(month1 + "/" + dayOfMonth + "/" + year1);
                    }, year, month, day);
            datePickerDialog.show();

        });


        etStartTime.setOnClickListener(e->{
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewSale.this,(TimePickerDialog.OnTimeSetListener) (view, hour1,minute1) ->{

                    Time t = new Time(hour1,minute1,0);
                    etStartTime.setText(t.toString());


            },hour,minute,false);
            timePickerDialog.show();

        });


        etEndTime.setOnClickListener(e->{
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewSale.this,(TimePickerDialog.OnTimeSetListener) (view, hour1,minute1) ->{

                    Time t = new Time(hour1, minute1, 0);
                    etEndTime.setText(t.toString());

            },hour,minute,false);
            timePickerDialog.show();

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
    //add a new sale
    public void saveNewSale() throws JSONException, ParseException {
        //get data
        String title = etSaleName.getText().toString();
        String address = etAddress.getText().toString();
        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();
        //check if all fields were filled
        if(etDate.getText().equals("") || title.equals("") || address.equals("") || startTime.equals("") || endTime.equals("")){
            Toast.makeText(this, "Please fill in each field. No fields can be left blank...", Toast.LENGTH_SHORT).show();
        }else{

            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Date date = formatter.parse(etDate.getText().toString());

            //add each variable to a JSON object
            JSONObject j = new JSONObject();
            j.put("title", title);
            j.put("date", date);
            j.put("address", address);
            j.put("startTime", startTime);
            j.put("endTime", endTime);
            //use the url to create the sale, using the result code to indicate it has returned from the result launcher
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