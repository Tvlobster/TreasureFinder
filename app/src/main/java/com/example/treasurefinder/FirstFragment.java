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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //retrieve sale info string from SaleDetail Activity
        SaleDetail saleDetailActivity = (SaleDetail) getActivity();
        saleInfoString = saleDetailActivity.getSaleInfo();
        itemInfoString = saleDetailActivity.getItemInfo();

        //parse the string into an array of data
        saleInfoData = saleInfoString.split(";");
        Log.d("TESTARRAY", saleInfoData[0]);
        Log.d("TESTARRAY", saleInfoData[1]); //address
        Log.d("TESTARRAY", saleInfoData[2]); //owner
        Log.d("TESTARRAY", saleInfoData[3]); //hours of operation

        //check to see if this sale has items posted
        //if they do not
        if(itemInfoString.equals(""))
            itemsFlag=false;
            //if they do, display them
        else{
            itemInfoData = itemInfoString.split(";");
            Log.d("TEST," ,itemInfoString);
            Log.d("TESTITEMS", itemInfoData[0]);
            Log.d("TESTITEMS", itemInfoData[1]);
            Log.d("TESTITEMS", itemInfoData[2]);

            //call the method to make a JSON request to get item details
            // requestItemInfo();


            //make the second request to get Item details
            //url to the server
            String url = "https://treasurefinderbackend.onrender.com/items";
            //create a new request queue
            queue = Volley.newRequestQueue(this.getContext());

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

                        JSONObject saleItem = listOfItems.getJSONObject(i);
                        String ID = saleItem.getString("_id");
                        String name = saleItem.getString("name");
                        String price = saleItem.getString("price");
                        Double priceDbl = Double.parseDouble(price);
                        String imgUrl = saleItem.getString("image");
                        String description = saleItem.getString("description");
                        for(int x=0;x<itemInfoData.length;x++){
                            if(ID.equals(itemInfoData)){
                                Item newItem = new Item(name,priceDbl, description);
                            }
                        }

                    }

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

      //  Item newItem = new Item("john",23, "test");

        TextView tvOwner = view.findViewById(R.id.tvOwner);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvHours = view.findViewById(R.id.tvHours);
        TextView tvFeaturedItems = view.findViewById(R.id.tvFeaturedItems);
        ListView lstItems = view.findViewById(R.id.lstItems);


        tvOwner.setText("Hosted By: "+ saleInfoData[2]);
        tvAddress.setText("Address: "+saleInfoData[1]);
        tvHours.setText(saleInfoData[3]);
        if(itemsFlag== false){
            tvFeaturedItems.setText("Currently No Featured Items");
        }
        else{

        }

        };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}