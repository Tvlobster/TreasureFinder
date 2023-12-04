package com.example.treasurefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserItemAdapter extends BaseAdapter {
    ArrayList<Item> items;
    Context context;

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
        txtItemName.setText(item.name);
        txtPrice.setText("$" + item.price);
        txtDescription.setText(item.description);

        return view;
    }

    public UserItemAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }
}
