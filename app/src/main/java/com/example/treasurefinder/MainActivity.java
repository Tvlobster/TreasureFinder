package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    TextView txtUsername;
    TextView txtPassword;

    RequestQueue queue;

    String URL = "https://treasurefinderbackend.onrender.com/login";
    String serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        //Allows the use of cookies
        //This is for user authentication
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);



        queue = Volley.newRequestQueue(this.getApplicationContext());
    }

    public void login(View v) throws JSONException {

        //Send to server here
        //Send hashed username, password, and salt used for hashing

        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        JSONObject j = new JSONObject();
        j.put("username", username);
        j.put("password", password);

        JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, j, response -> {
            //Add code for response here, in theory server should respond with an ID if login is valid
            try {
                serverResponse = response.get("id").toString();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            if (serverResponse.equals("ERROR")) {
                Toast.makeText(this, "ERROR: LOGIN INFORMATION WRONG", Toast.LENGTH_SHORT).show();
            }

            else {
                Intent i = new Intent(this, SalesActivity.class);
                i.putExtra("ID", serverResponse);
                startActivity(i);
            }

        }, error -> {
        });

        queue.add(r);
    }

    public void signUp(View v) {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }
}