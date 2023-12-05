package com.example.treasurefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SaleAdapter extends BaseAdapter {
    ArrayList<garageSale> sales;
    Context context;

    public SaleAdapter( Context context,ArrayList<garageSale> sales) {
        this.sales = sales;
        this.context = context;
    }
    @Override
    public int getCount() {
        return sales.size();
    }

    @Override
    public Object getItem(int i) {
        return sales.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view =  LayoutInflater.from(context).inflate(R.layout.layout_sale_item,parent,false);
        garageSale sale = sales.get(i);
        TextView txtSaleAddress = view.findViewById(R.id.txtSaleAddress);
        TextView txtSaleHost = view.findViewById(R.id.txtSaleHost);
        TextView txtSaleHours = view.findViewById(R.id.txtSaleHours);
        TextView txtSaleTitle = view.findViewById(R.id.txtSaleTitle);

        txtSaleAddress.setText("Address: "+sale.address);
       txtSaleHost.setText("Hosted By: "+sale.owner);
        txtSaleTitle.setText(sale.title +"");
        txtSaleHours.setText ("Open: " + sale.date + " from " + sale.hours);

        return view;
    }

}
