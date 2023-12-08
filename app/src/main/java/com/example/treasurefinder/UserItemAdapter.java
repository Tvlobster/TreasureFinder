package com.example.treasurefinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UserItemAdapter extends BaseAdapter {
    ArrayList<Item> items;
    Context context;
    RequestQueue adapterQueue;

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
        //format price to 2 decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        view = LayoutInflater.from(context).inflate(R.layout.your_sale_items_layout, parent, false);
        Item item = items.get(i);
        //get views and set values
        TextView txtItemName = view.findViewById(R.id.txtYourSaleItem);
        TextView txtPrice = view.findViewById(R.id.txtYourItemPrice);
        TextView txtDescription = view.findViewById(R.id.txtYourItemDescription);
        Button btnDelete = view.findViewById(R.id.btnYourItemDelete);
        txtItemName.setText(item.name);
        txtPrice.setText("$" + df.format(item.price));
        txtDescription.setText(item.description);
        //delete button
        btnDelete.setOnClickListener(e-> {
            //launch delete url and remove item from array list
            adapterQueue = Volley.newRequestQueue(this.context.getApplicationContext());
            String URL = "https://treasurefinderbackend.onrender.com/seller/deleteItem";
            URL += "/" + item.id;
            items.remove(i);
            JsonObjectRequest r = new JsonObjectRequest(Request.Method.DELETE, URL, null, response -> {
                //check if any sales remain, and display text view indicating no sales are present if necessary
                Log.d("Delete", response.toString());
                notifyDataSetChanged();
                TextView txtNoSales = parent.getRootView().findViewById(R.id.txtNoItems);
                if(items.isEmpty())
                    txtNoSales.setVisibility(View.VISIBLE);
                else
                    txtNoSales.setVisibility(View.INVISIBLE);
            }, error -> {
                Log.d("Delete", error.toString());
            });
            adapterQueue.add(r);
            notifyDataSetChanged();

        });

        return view;
    }
    //constructor for adapter
    public UserItemAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }
}
