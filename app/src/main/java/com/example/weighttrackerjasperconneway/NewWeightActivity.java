package com.example.weighttrackerjasperconneway;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
public class NewWeightActivity extends AppCompatActivity {
    private EditText newWeight;
    private EditText dateEditText;
    private Button saveBtn;
    private String username;
    private Button homeBtn;
    private final String URL = "http://10.0.0.88/weight_tracker_api/fetchData.php?action=addWeight";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newweight);

        // Find reference to view for manipulation
        TextView addMessage = findViewById(R.id.addMessageTextView);
        newWeight = findViewById(R.id.newWeightEditText);
        dateEditText = findViewById(R.id.dateEditText);
        saveBtn = findViewById(R.id.saveButton);
        homeBtn = findViewById(R.id.homeButton);


        // Gather intent from previous activity
        username = getIntent().getStringExtra("username");

        // Ensure username is not empty
        if (username == null || username.isEmpty()) {
            // handle null exception and empty username
            Toast.makeText(NewWeightActivity.this, "Error: Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("DEBUG", "The username from intent call: " + username);

        // Listen for Save or Home buttons
        clickSaveButton();
        clickHomeButton();

    }

    // When the save button is clicked, get each value entered and add the new weight.
    public void clickSaveButton() {
        saveBtn.setOnClickListener(v -> {
            String date = dateEditText.getText().toString().trim();
            String weight = newWeight.getText().toString().trim();

            // Ensure input is not empty
            if (date.isEmpty() || weight.isEmpty()) {
                // handle null exception and empty username
                Toast.makeText(NewWeightActivity.this, "Error: Must enter new weight and date to continue.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Communicate with sql database to add a new weight
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    response -> {
                        // Handle response from the server (login success)
                        if (response.equals("success")) {
                            Intent intent = new Intent(NewWeightActivity.this, UserAccountActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        } else if (response.equals("failure")) {
                            // Handle response from the server (login failure)
                            Toast.makeText(NewWeightActivity.this, "Failed to add new weight.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        // Handle errors
                        Toast.makeText(NewWeightActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                protected Map<String, String> getParams() { // provide username and password input to the server
                    Map<String, String> params = new HashMap<>();
                    params.put("date", date);
                    params.put("weight", weight);
                    params.put("username", username);
                    return params;
                }
            };

            // Add and send the server request
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

        });
    }

    // When Home Button is clicked, send user to their home page. End new weight activity
    public void clickHomeButton() {
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(NewWeightActivity.this, UserAccountActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
    }

}
