package com.example.weighttrackerjasperconneway;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
public class RegisterActivity extends AppCompatActivity {
    private EditText mUsername;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mNumber;
    private Button RegisterBtn;
    private Button LoginBtn;
    private final String URL = "http://10.0.0.88/weight_tracker_api/register.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find reference to view for manipulation
        mUsername = findViewById(R.id.usernameEditText);
        mPassword = findViewById(R.id.passwordEditText);
        mConfirmPassword = findViewById(R.id.confirmPasswordEditText);
        mNumber = findViewById(R.id.smsEditText);
        RegisterBtn = findViewById(R.id.registerButton);
        LoginBtn = findViewById(R.id.loginButton);
        ImageView scaleImg = findViewById(R.id.iconImageView);

        // Listen for Login or Register buttons
        clickRegisterButton();
        clickLoginButton();
    }

    // When the Login Button is clicked, send user to the Login page. End Register activity.
    public void clickLoginButton() {
        LoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // When the Register Button is clicked, get each value entered and attempt to create account.
    public void clickRegisterButton() {

        // Gather user input
        RegisterBtn.setOnClickListener(v -> {
            String username = mUsername.getText().toString();
            String password = mPassword.getText().toString();
            String confirmPassword = mConfirmPassword.getText().toString();
            String number = mNumber.getText().toString();

            // All variables require user input to continue
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || number.isEmpty()) {
                Toast.makeText(RegisterActivity.this,
                        "Please enter all fields to continue.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ensure password is confirmed
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this,
                        "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Communicate with the sql database to register new account
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        // Handle response from the server
                        switch (response) {
                            case "success": // If account was created, send user to Login screen. End Register activity.
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case "failure":
                                // Handle response from the server (login failure)
                                Toast.makeText(RegisterActivity.this, "Something went wrong! Try again later.", Toast.LENGTH_SHORT).show();
                                break;
                            case "User account exists":
                                // Handle response from the server (login failure)
                                Toast.makeText(RegisterActivity.this, "Already have an account. Please log in.", Toast.LENGTH_SHORT).show();
                                break;
                            case "Username exists":
                                // Handle response from the server (login failure)
                                Toast.makeText(RegisterActivity.this, "Username is taken.", Toast.LENGTH_SHORT).show();
                                break;
                            case "Number exists":
                                // Handle response from the server (login failure)
                                Toast.makeText(RegisterActivity.this, "Number is taken.", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    },
                    error -> {
                        // Handle additional errors
                        Toast.makeText(RegisterActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() { // provide username and password input to the server
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    params.put("number", number);
                    return params;
                }
            };

            // Add and send the server request
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

        });
    }
}
