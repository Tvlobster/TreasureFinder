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

import org.json.JSONObject;

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
        view = LayoutInflater.from(context).inflate(R.layout.your_sale_items_layout, parent, false);
        Item item = items.get(i);
        TextView txtItemName = view.findViewById(R.id.txtYourSaleItem);
        TextView txtPrice = view.findViewById(R.id.txtYourItemPrice);
        TextView txtDescription = view.findViewById(R.id.txtYourItemDescription);
        Button btnDelete = view.findViewById(R.id.btnYourItemDelete);
        txtItemName.setText(item.name);
        txtPrice.setText("$" + item.price);
        txtDescription.setText(item.description);

//        btnDelete.setOnClickListener(e-> {
//            adapterQueue = Volley.newRequestQueue(this.context.getApplicationContext());
//            String URL = "https://treasurefinderbackend.onrender.com/seller/deleteItem";
//
//            JSONObject j = new JSONObject();
//            URL += "/" + item.;
//            items.remove(i);
//            JsonObjectRequest r = new JsonObjectRequest(Request.Method.DELETE, URL, null, response -> {
//                Log.d("Delete", response.toString());
//                notifyDataSetChanged();
//            }, error -> {
//                Log.d("Delete", error.toString());
//            });
//            adapterQueue.add(r);
//            notifyDataSetChanged();
//        });

        return view;
    }

    public UserItemAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }
}
