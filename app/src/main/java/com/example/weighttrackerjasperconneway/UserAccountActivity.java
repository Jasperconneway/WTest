package com.example.weighttrackerjasperconneway;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
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
public class UserAccountActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button weightCalenderBtn;
    private Button newWeightBtn;
    private TextView currentWeightTextView;
    private String username;
    private final String URL = "http://10.0.0.88/weight_tracker_api/fetchData.php?action=currentUserWeight";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useraccount);

        // Find reference to view for manipulation
        weightCalenderBtn = findViewById(R.id.weightCalenderButton);
        newWeightBtn = findViewById(R.id.newWeightButton);
        currentWeightTextView = findViewById(R.id.currentWeightTextView);

        // Gather intent from previous activity
        username = getIntent().getStringExtra("username");

        // Ensure username is not empty
        if (username == null || username.isEmpty()) {
            // handle null exception and empty username
            Toast.makeText(UserAccountActivity.this, "Error: Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("DEBUG", "The username from intent call: " + username);

        // Listen for Weight Calendar or New Weight buttons.
        clickWeightCalenderButton();
        clickNewWeightButton();

        // Put the current weight on the screen
        getCurrentUserWeight();


        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with push notifications
            initializePushNotifications();
        }


    }

    // Get the user's current weight
    public void getCurrentUserWeight() {

        // Communicate with the sql database to get user's most recent weight
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    // Handle response from the server (no current weight located)
                    if (response.equals("-1")) {
                        currentWeightTextView.setText("N/A");
                    } else {
                        // Handle response from the server (current weight located)
                        currentWeightTextView.setText(response);
                    }
                },
                error -> {
                    // Handle errors
                    Toast.makeText(UserAccountActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() { // provide the username to the server
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        // Add and send the server request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // When the Weight Calendar button is clicked, send user to the Weight Calendar screen. End home activity
    public void clickWeightCalenderButton() {
        weightCalenderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserAccountActivity.this, WeightCalenderActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
    }

    // When the New Weight button is clicked, send user to the New Weight screen. End home activity
    public void clickNewWeightButton() {
        newWeightBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserAccountActivity.this, NewWeightActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
    }


    private void initializePushNotifications() {
        // Your code to initialize push notifications
        Toast.makeText(this, "Push Notifications Initialized", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with push notifications
                initializePushNotifications();

            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission Denied. Push Notifications cannot be enabled.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
