package com.example.treasurefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserSaleAdapter extends BaseAdapter {
    ArrayList<garageSale> sales;
    Context context;

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
        txtSaleTitle.setText(sale.title);
        txtItemCount.setText("0 Items");
        txtAddress.setText(sale.address);

        return view;
    }

    public UserSaleAdapter(ArrayList<garageSale> sales, Context context) {
        this.sales = sales;
        this.context = context;
    }
}
