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
//testingCommit
    TextView txtUsername;
    TextView txtPassword;

    TextView txtPasswordConfirmation;

    RequestQueue queue;

    String URL = "https://treasurefinderbackend.onrender.com/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtUsername = findViewById(R.id.txtCreateUsername);
        txtPassword = findViewById(R.id.txtCreatePassword);
        txtPasswordConfirmation = findViewById(R.id.txtConfirmPassword);

        queue = Volley.newRequestQueue(this);
    }

    public void createAccount(View v) throws JSONException {
        //Send to server here
        //Send hashed username, password, and salt used for hashing

        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtPasswordConfirmation.getText().toString();

        if(confirmPassword.equals(password)) {
            JSONObject j = new JSONObject();
            j.put("username", username);
            j.put("password", password);

            JsonObjectRequest r = new JsonObjectRequest(Request.Method.POST, URL, j, response -> {
                Toast.makeText(this, "ACCOUNT MADE!", Toast.LENGTH_SHORT).show();
                finish();
            }, error -> {
            });

            queue.add(r);
        }

        else {
            Toast.makeText(this, "ERROR: PASSWORD CONFIRMATION DOES NOT MATCH", Toast.LENGTH_SHORT).show();
        }

    }
}