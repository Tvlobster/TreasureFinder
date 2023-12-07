package com.example.treasurefinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter  {
    ArrayList<Item> items;
    Context context;
    RequestQueue queue;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        Item item = items.get(i);
        TextView txtItemName = view.findViewById(R.id.txtItemName);
        TextView txtPrice = view.findViewById(R.id.txtPrice);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        Button btnRequest = view.findViewById(R.id.btnRequest);
        txtItemName.setText(item.name);
        txtPrice.setText("$" + item.price);
        txtDescription.setText(item.description);
        context = context.getApplicationContext();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send a request method
                //   /request/new/:id
                //url to the server
                //url to the server
                String url = "https://treasurefinderbackend.onrender.com/request/new/"+item.id;
                //create a new request queue
                queue = Volley.newRequestQueue(context);

                //create a JSON object to use for the request
                JSONObject j = new JSONObject();
                //create the JSON object request
                JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, url, j, response -> {
                    String jsonResponse = response.toString(); // Convert the response to a string
                    Log.d("MyLog", response.toString());
                    try {
                        //extract the object and array from the response
                        JSONObject myObject = new JSONObject(jsonResponse);



                    } catch (JSONException e) {

                    }

                }, error -> {
                    Log.d("MyLog", error.toString() + " ");
                    error.printStackTrace();
                });
                //add the request to the queue
                queue.add(r);

            }
        });

        return view;
    }

    public ItemAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }
}
