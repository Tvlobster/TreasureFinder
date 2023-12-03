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

public class MainActivity extends AppCompatActivity {

    //TextView for username entry
    TextView txtUsername;

    //TextView for password entry
    TextView txtPassword;

    //Queue for sending JSON requests
    RequestQueue queue;

    //URL for login
    String URL = "https://treasurefinderbackend.onrender.com/login";

    //String for server response during login
    String serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //sets username and password textviews to their respective views
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);

        //Instantiates queue
        queue = Volley.newRequestQueue(this);
    }

    public void login(View v) throws JSONException {

        //Creates a new string from the username entered into the username text view
        String username = txtUsername.getText().toString();

        //Creates a new string from the password entered into the password text view
        String password = txtPassword.getText().toString();

        //Creates a new JSON Object
        JSONObject j = new JSONObject();

        //Adds the username and password into the object
        j.put("username", username);
        j.put("password", password);

        //Creates a new JSON request to send the username and password over to the server for authentication
        JsonObjectRequest r = new JsonObjectRequest(Request.Method.GET, URL, j, response -> {
            try {
                //serverResponse string is set to the respone held in the ID tag of the JSON response
                serverResponse = response.get("id").toString();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            //If statement checks to see if the serverResponse says error
            if (serverResponse.equals("ERROR")) {

                //If so, show toast stating login info was wrong
                Toast.makeText(this, "ERROR: LOGIN INFORMATION WRONG", Toast.LENGTH_SHORT).show();
            }

            //If not
            else {

                //Make a new intent for the salesActivity, add the id information and launch the intent
                Intent i = new Intent(this, SalesActivity.class);
                i.putExtra("ID", serverResponse);
                startActivity(i);
            }

        }, error -> {
            //If theres a JSON error, show toast stating server error
            Toast.makeText(this, "ERROR: SERVER ERROR", Toast.LENGTH_SHORT).show();
        });

        //Add JSON request to queue
        queue.add(r);
    }

    public void signUp(View v) {
        //Launch SignUp activity
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }
}