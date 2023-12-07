package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;


//for logging out we are going to need a method that contains this code

//if (cookieManager.getCookieStore().getCookies().size() > 0) {
//    cookieManager.getCookieStore().removeAll();
//}




public class MainActivity extends AppCompatActivity {

    //TextView for username
    TextView txtUsername;

    //TextView for password
    TextView txtPassword;

    //Queue for sending JSON requests
    RequestQueue queue;

    //URL for login
    String URL = "https://treasurefinderbackend.onrender.com/login";

    //serverResponse for storing response from server for login
    String serverResponse;

    //Shared preference to pass user ID
    SharedPreferences sharedPref;

    //Editor to put userID into shared preferences
    SharedPreferences.Editor prefEditor;

    //Debug tag
    public static final String TAG = "NotifServiceTag";

    //Notification request code
    public static final int NOTIFICATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //sets username and password textviews to their respective views
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        //Allows the use of cookies
        //This is for user authentication
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        //Instantiates Queue
        queue = Volley.newRequestQueue(this.getApplicationContext());

        //Create a shared preference to pass userID between views
        sharedPref = this.getSharedPreferences("user", Context.MODE_PRIVATE);

        //Instantiate preference editor
        prefEditor = sharedPref.edit();

        //Call checkPermissions method
        checkPermissions();

        //If statement checks to see if sharedPreference contains id tag
        if(sharedPref.contains("id")) {

            //If so, create a new string using get string on the id tag in shared preference
            String s = sharedPref.getString("id", "0");

            //Check to make sure that string does not read 0
            //(0 denotes id does not exist)
            if(s.equals("0") == false) {

                //If string does not read 0, launch salesActivity intent
                Intent i = new Intent(this, SalesActivity.class);
                startActivity(i);
            }
        }
    }

    public void login(View v) throws JSONException {

        //Check to see if text views are left empty
        if (txtUsername.getText().equals("") || txtPassword.getText().equals("")) {
            //Show toast stating enter username and password
            Toast.makeText(this, "ERROR: PLEASE ENTER USERNAME AND PASSWORD", Toast.LENGTH_SHORT).show();
        }

        else {
            //sets username and password text views to their respective views
            String username = txtUsername.getText().toString();
            String password = txtPassword.getText().toString();

            //Create new JSON object
            JSONObject j = new JSONObject();

            //Add username and password to the object
            j.put("username", username);
            j.put("password", password);

            //Creates a new JSON request to send the username and password over to the server for login
            JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, j, response -> {
                try {

                    //If statement checks to see if the server response contains id
                    if(response.has("id")) {
                        //If so,

                        //set serverResponse to string held under id tag in server response
                        serverResponse = response.get("id").toString();

                        //Create new intent for SalesActivity, put serverResponse in intent, launch intent
                        Intent i = new Intent(this, SalesActivity.class);

                        prefEditor.putString("id", serverResponse);
                        prefEditor.apply();

                        startActivity(i);
                    }

                    else {
                        //Show toast stating login info was wrong
                        Toast.makeText(this, "ERROR: LOGIN INFORMATION WRONG", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }, error -> {
                //If theres a JSON error, show toast stating server error
                Toast.makeText(this, "ERROR: SERVER ERROR", Toast.LENGTH_SHORT).show();
            });

            //Add JSON request to queue
            queue.add(r);
        }
    }

    public void signUp(View v) {
        //Create and launch intent for sign up
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }

    public void checkPermissions() {
        //If statement to check permissions for location use and to post notifications
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions NOT granted, requesting....");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_FINE_LOCATION}, NOTIFICATION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Permissions already granted");
        }
    }
}