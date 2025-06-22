package com.example.weighttrackerjasperconneway;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

/*
* Jasper Conneway
* CS 499 Category Three: Databases enhancement
* Original project from CS 360
* Completed: 06/18/2025
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button LoginBtn;
    private Button RegisterBtn;
    private final String URL = "http://10.0.0.88/weight_tracker_api/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find reference to view for manipulation
        mUsername = findViewById(R.id.usernameEditText);
        mPassword = findViewById(R.id.passwordEditText);
        LoginBtn = findViewById(R.id.loginButton);
        RegisterBtn = findViewById(R.id.registerButton);
        ImageView scaleImg = findViewById(R.id.iconImageView);

        // Listen for Login or Register buttons
        clickLoginButton();
        clickRegisterButton();
    }

    // When Register Button is clicked - send user to the register screen and end the Login activity
    public void clickRegisterButton() {
        RegisterBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }


    // When the Login Button is clicked, get each value entered and verify login. Then send to user home page.
    public void clickLoginButton() {
        // Gather user input
        LoginBtn.setOnClickListener(v -> {
            String username = mUsername.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            // Ensure input is not empty
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Please enter username and password to continue.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check sql database for user login
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        // Handle response from the server (login success)
                        if (response.equals("success")) {
                            Intent intent = new Intent(LoginActivity.this, UserAccountActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("password", password);
                            startActivity(intent);
                            finish();
                        } else if (response.equals("failure")) {
                            // Handle response from the server (login failure)
                            Toast.makeText(LoginActivity.this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                    // Handle errors
                        Toast.makeText(LoginActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                    ) {
                    @Override
                    protected Map<String, String> getParams() { // provide username and password input to the server
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        return params;
                    }
            };

            // Add and send the server request
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

        });
    }

}
