package com.example.treasurefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    TextView txtUsername;
    TextView txtPassword;

    TextView txtPasswordConfirmation;

    RequestQueue queue;

    String URL = "https://treasurefinderbackend.onrender.com/";

    Boolean login;
    String serverResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtUsername = findViewById(R.id.txtCreateUsername);
        txtPassword = findViewById(R.id.txtCreatePassword);
        txtPasswordConfirmation = findViewById(R.id.txtConfirmPassword);

        queue = Volley.newRequestQueue(this);
    }

    public void createAccount() throws JSONException {
        //Send to server here
        //Send hashed username, password, and salt used for hashing

        String username = (String) txtUsername.getText();
        String password = (String) txtPassword.getText();
        String confirmPassword = (String) txtPasswordConfirmation.getText();

        if(confirmPassword.equals(password)) {
            JSONObject j = new JSONObject();
            j.put("Username", username);
            j.put("Password", password);

            JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, j, response -> {
                //Add code for response here, in theory server should respond with a t/f depending on if login worked
            }, error -> {
            });
        }

        else {
            Toast.makeText(this, "ERROR: PASSWORD CONFIRMATION DOES NOT MATCH", Toast.LENGTH_SHORT).show();
        }

    }
}