package com.example.treasurefinder;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.treasurefinder.databinding.FragmentFirstBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

String saleInfoString, itemInfoString;
String[] saleInfoData, itemInfoData;
RequestQueue queue;
Boolean itemsFlag = true;
    ItemAdapter adapter;


    ArrayList<Item> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //create an array list of items for this sale
        items = new ArrayList<>();


        //retrieve sale info string from SaleDetail Activity
        //could only figure out how to send data over a string, so I concantenated it in the getSaleInfo method and will re-split it here
        SaleDetail saleDetailActivity = (SaleDetail) getActivity();
        saleInfoString = saleDetailActivity.getSaleInfo();
        itemInfoString = saleDetailActivity.getItemInfo();

        //parse the string into an array of data
        saleInfoData = saleInfoString.split(";");
        //Log.d("TESTARRAY", saleInfoData[0]);
        //Log.d("TESTARRAY", saleInfoData[1]); //address
        //Log.d("TESTARRAY", saleInfoData[2]); //owner
        //Log.d("TESTARRAY", saleInfoData[3]); //hours of operation



        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            //initialize the list view
        ListView lvSaleItems = view.findViewById(R.id.lvSaleItems);

        //if there are no items in the item info string, the sale has no items
        if(itemInfoString.equals(""))
            itemsFlag=false;
            //if they do, display them
        else{
            //split the string into separate array values to get each items ID
            itemInfoData = itemInfoString.split(";");

            Log.d("TEST," ,itemInfoString);



            //make the second request to get Item details
            //url to the server
            String url = "https://treasurefinderbackend.onrender.com/items";
            //create a new request queue
            queue = Volley.newRequestQueue(this.getContext().getApplicationContext());
            //this route will return all item objects
            //iterate through them to find matches and get related intormation
            //create a JSON object to use for the request
            JSONObject j = new JSONObject();
            //create the JSON object request
            JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, url, j, response -> {
                String jsonResponse = response.toString(); // Convert the response to a string
                Log.d("MyLog", response.toString());

                try {
                    //extract the object and array from the response
                    JSONObject myObject = new JSONObject(jsonResponse);
                    JSONArray listOfItems = myObject.getJSONArray("listOfItems");
                    for(int i=0;i<listOfItems.length();i++){
                        //store all the data for the current ITEM
                        JSONObject saleItem = listOfItems.getJSONObject(i);
                        String ID = saleItem.getString("_id");
                        String name = saleItem.getString("name");
                        String price = saleItem.getString("price");
                        Double priceDbl = Double.parseDouble(price);
                        String description = saleItem.getString("description");
                        //iterate through this sale's item IDs to find a match
                        for(int x=0;x<itemInfoData.length;x++){
                            //if a match happens, create a new item object and push it to the array
                            if(ID.equals(itemInfoData[x])){
                                Item newItem = new Item(name,priceDbl, description,ID);
                                items.add(newItem);
                            }
                        }

                    }
                    //create a new adapter with all the sales and set the adapter for the list view
                    adapter = new ItemAdapter(items, getContext());
                 // Log.d("TEST", items.toString());
                    lvSaleItems.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }, error -> {
                Log.d("MyLog", error.toString() + " ");
                error.printStackTrace();
            });
            //add the request to the queue
            queue.add(r);


        }

      //  initialize the views on the fragment
        TextView tvOwner = view.findViewById(R.id.tvOwner);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvHours = view.findViewById(R.id.tvHours);
        TextView tvFeaturedItems = view.findViewById(R.id.tvFeaturedItems);

    // set the sale values that were recieved by the sales activity intent
        tvOwner.setText("Hosted By: "+ saleInfoData[2]);
        tvAddress.setText("Address: "+saleInfoData[1]);
        tvHours.setText(saleInfoData[3]);
        //if there were no items in the sale
        if(itemsFlag== false){
            tvFeaturedItems.setText("Currently No Featured Items");
            itemsFlag =true; //reset flag
        }


        };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}