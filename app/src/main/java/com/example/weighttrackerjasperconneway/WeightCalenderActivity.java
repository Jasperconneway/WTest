package com.example.weighttrackerjasperconneway;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/*
 * Jasper Conneway
 * CS 499 Category Three: Databases enhancement
 * Original project from CS 360
 * Completed: 06/18/2025
 */
public class WeightCalenderActivity extends AppCompatActivity {
    private String username;
    private TableLayout tableLayout;
    private Button newWeightBtn;
    private Button homeBtn;
    private String URL = "http://10.0.0.88/weight_tracker_api/fetchData.php?action=getUserWeight";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weightcalender);

        // Find reference to view for manipulation
        tableLayout = findViewById(R.id.weightCalender);
        newWeightBtn = findViewById(R.id.newWeightButton);
        homeBtn = findViewById(R.id.homeButton);

        // Gather intent from previous activity
        username = getIntent().getStringExtra("username");

        // Ensure username is not empty
        if (username == null || username.isEmpty()) {
            // handle null exception and empty username
            Toast.makeText(WeightCalenderActivity.this, "Error: Invalid user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Listen for New Weight or Home button. Create the Weight Calendar
        clickNewWeightButton();
        clickHomeButton();
        createWeightCalender();
    }

    // Create the Weight Calendar Table when screen is populated.
    public void createWeightCalender() {

        // Communicate with the sql database to get all the [date, weight]'s for the user
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        // Manually parse response into a jsonarray
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) { // iterate through array
                            // Get each [date, weight]
                            JSONObject jRow = jsonArray.getJSONObject(i);

                            // Put each in String variables
                            String date = jRow.getString("date");
                            String weight = String.valueOf(jRow.get("weight"));

                            // Ensure information is as expected - show in logs
                            Log.d("Entry", "Date: " + date + ", Weight: " + weight);

                            // Handle row display here
                            TableRow row = new TableRow(this);

                            // Set the date view
                            TextView dateTextView = new TextView(this);
                            dateTextView.setText(date);
                            dateTextView.setPadding(8, 8, 8, 8);

                            // Set the weight view
                            TextView weightTextView = new TextView(this);
                            weightTextView.setText(weight);
                            weightTextView.setPadding(8, 8, 8, 8);

                            // Add the date and weight view to the table
                            row.addView(dateTextView);
                            row.addView(weightTextView);

                            // Set edit weight text for user input
                            EditText editWeight = new EditText(WeightCalenderActivity.this);
                            editWeight.setHint("New weight");

                            // Set update button to listen for when button is clicked
                            ImageButton updateButton = new ImageButton(WeightCalenderActivity.this);
                            updateButton.setImageResource(android.R.drawable.ic_menu_edit);
                            updateButton.setBackgroundResource(android.R.color.transparent);
                            updateButton.setOnClickListener(v -> {
                                // If Update button is clicked, get the weight value
                                String updatedWeight = editWeight.getText().toString();

                                // Call Update Weight Button to update the row
                                updateWeightButton(date, weight, username, updatedWeight);

                            });

                            // Set delete button to listen for when button is clicked
                            ImageButton deleteButton = new ImageButton(WeightCalenderActivity.this);
                            deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
                            deleteButton.setBackgroundResource(android.R.color.transparent);
                            deleteButton.setOnClickListener(v -> {

                                // If Delete button is clicked, call Delete Weight Button
                                deleteWeightButton(date, weight, username, success -> {
                                    // Determine if weight was deleted:
                                    if (success) {
                                        // Remove row, if successful
                                        Toast.makeText(this, "Weight was deleted: " + weight, Toast.LENGTH_SHORT).show();
                                        tableLayout.removeView(row);
                                    } else {
                                        // Otherwise, show error message
                                        Toast.makeText(this, "Weight was not deleted " + weight, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            });

                            // Add edit, update, and delete views to the table
                            TableRow.LayoutParams params = new TableRow.LayoutParams();
                            params.span = 2;
                            row.addView(editWeight);
                            row.addView(updateButton);
                            row.addView(deleteButton);
                            tableLayout.addView(row);

                        }
                    } catch (JSONException e) { // catch error exceptions
                        e.printStackTrace();
                    }
                },
                // Handle errors
                error -> {
                    Log.e("Volley", "Error: " + error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() { // provide the username as param
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }

            @Override
            public String getBodyContentType() { // send params as expected
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // Add and send the server request
        Volley.newRequestQueue(this).add(stringRequest);



    }


    // When the New Weight Button is clicked, send user to the New Weight screen. End the Weight Calendar activity.
    public void clickNewWeightButton() {
        newWeightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightCalenderActivity.this, NewWeightActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
    }

    // When the Home Button is clicked, send user to the Home screen. End the Weight Calendar activity.
    public void clickHomeButton() {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightCalenderActivity.this, UserAccountActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
    }

    // When the Weight Button is clicked, get each value to update weight.
    public void updateWeightButton(String date, String weight, String username, String updatedWeight) {
        String tempURL = "http://10.0.0.88/weight_tracker_api/fetchData.php?action=updateWeight";

        // Communicate with sql database to update weight on specific date
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL,
                response -> {
                    // Handle response from the server (weight updated successfully). Then restart the Weight Calendar activity
                    if (response.equals("success")) {
                        Intent intent = new Intent(this, WeightCalenderActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        Toast.makeText(this, "Weight updated: " + updatedWeight, Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (response.equals("failure")) {
                        // Handle response from the server (weight update failed)
                        Toast.makeText(this, "Weight was not updated to: " + updatedWeight, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle errors
                    Toast.makeText(this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() { // provide username and password input to the server
                Map<String, String> params = new HashMap<>();
                params.put("date", date);
                params.put("weight", weight);
                params.put("username", username);
                params.put("updatedWeight", updatedWeight);
                return params;
            }
        };

        // Add and send the server request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void deleteWeightButton(String date, String weight, String username, DeleteCallback callback) {
        String tempURL = "http://10.0.0.88/weight_tracker_api/fetchData.php?action=deleteWeight";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL,
                response -> {
                    Log.d("RAW_RESPONSE", response);
                    callback.onResult(response.equals("success"));
                },
                error -> {
                    Toast.makeText(this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    callback.onResult(false);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("date", date);
                params.put("weight", weight);
                params.put("username", username);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public interface DeleteCallback {
        void onResult(boolean success);
    }



}
