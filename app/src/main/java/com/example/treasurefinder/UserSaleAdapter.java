package com.example.treasurefinder;

import android.content.Context;
import android.content.Intent;
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

public class UserSaleAdapter extends BaseAdapter {
    ArrayList<garageSale> sales;
    Context context;
    RequestQueue adapterQueue;

    @Override
    public int getCount() {
        return sales.size();
    }

    @Override
    public Object getItem(int position) {
        return sales.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.your_sales_layout, parent, false);
        garageSale sale = sales.get(i);
        TextView txtSaleTitle = view.findViewById(R.id.txtYourSaleTitle);
        TextView txtItemCount = view.findViewById(R.id.txtYourSaleCount);
        TextView txtAddress = view.findViewById(R.id.txtYourSaleAddress);
        Button btnDelete = view.findViewById(R.id.btnYourSaleDelete);
        Button btnView = view.findViewById(R.id.btnYourSaleView);
        txtSaleTitle.setText(sale.title);
        txtItemCount.setText(sale.items.length + " items");
        txtAddress.setText(sale.address);

        btnDelete.setOnClickListener(e-> {
            adapterQueue = Volley.newRequestQueue(this.context.getApplicationContext());
            String URL = "https://treasurefinderbackend.onrender.com/seller/deleteGarageSale";

            JSONObject j = new JSONObject();
            URL += "/" + sale.TUID;
            sales.remove(i);
            JsonObjectRequest r = new JsonObjectRequest(Request.Method.DELETE, URL, null, response -> {
                Log.d("Delete", response.toString());
                notifyDataSetChanged();
            }, error -> {
                Log.d("Delete", error.toString());
            });
            adapterQueue.add(r);
            notifyDataSetChanged();
        });

        btnView.setOnClickListener(e-> {
            Intent intent = new Intent(this.context, AddNewItem.class);
            intent.putExtra("saleID", sale.TUID);
            context.startActivity(intent);
        });

        return view;


    }

    public UserSaleAdapter(ArrayList<garageSale> sales, Context context) {
        this.sales = sales;
        this.context = context;
    }
}
