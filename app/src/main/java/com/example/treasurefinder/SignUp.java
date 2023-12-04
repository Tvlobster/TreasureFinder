package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

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

public class SignUp extends AppCompatActivity {

    //TextView for username entry
    TextView txtUsername;

    //TextView for password entry
    TextView txtPassword;

    //TextView for password confirmation entry
    TextView txtPasswordConfirmation;

    //Queue for sending JSON requests
    RequestQueue queue;

    //URL for register
    String URL = "https://treasurefinderbackend.onrender.com/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //sets username, password, and password confirmation text views to their respective views
        txtUsername = findViewById(R.id.txtCreateUsername);
        txtPassword = findViewById(R.id.txtCreatePassword);
        txtPasswordConfirmation = findViewById(R.id.txtConfirmPassword);

        //Instantiates queue
        queue = Volley.newRequestQueue(this.getApplicationContext());
    }

    public void createAccount(View v) throws JSONException {

        //Check to see if text views are left empty
        if (txtUsername.getText().equals("") || txtPassword.getText().equals("") || txtPasswordConfirmation.getText().equals("")) {
            //Show toast stating enter username and password
            Toast.makeText(this, "ERROR: PLEASE ENTER USERNAME, PASSWORD, AND PASSWORD CONFIRMATION", Toast.LENGTH_SHORT).show();
        }

        else {

            //Creates strings for username, password and password confirmation from the text in their respective text views
            String username = txtUsername.getText().toString();
            String password = txtPassword.getText().toString();
            String confirmPassword = txtPasswordConfirmation.getText().toString();

            //If password in confirm password matches password in password text view
            if (confirmPassword.equals(password)) {

                //Create new JSON object
                JSONObject j = new JSONObject();

                //Add username and password to the object
                j.put("username", username);
                j.put("password", password);

                //Creates a new JSON request to send the username and password over to the server for sign up
                JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, j, response -> {
                    //On response from server, show toast saying their account was made and close activity
                    Toast.makeText(this, "ACCOUNT MADE!", Toast.LENGTH_SHORT).show();
                    finish();
                }, error -> {
                    //If theres a JSON error, show toast stating server error
                    Toast.makeText(this, "ERROR: SERVER ERROR", Toast.LENGTH_SHORT).show();
                });

                //Add JSON request to queue
                queue.add(r);
            } else {
                //If password and password confirmation do not match show toast saying passwords do not match
                Toast.makeText(this, "ERROR: PASSWORD CONFIRMATION DOES NOT MATCH", Toast.LENGTH_SHORT).show();
            }
        }

    }
}