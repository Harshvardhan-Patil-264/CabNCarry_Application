package com.example.cabn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

//userprof, user(fetch user data), DBConnection

public class Userprof extends AppCompatActivity {

    private SharedPreferences prefs;
    private String userordriver, userEmail;
    private int id;
    private String available = null, vehicle = null;
    private MaterialButton logout1;
    private TextInputLayout drvrlcn1, drvrvhcl1, drvravlbl1, drvrrting1;
    private TextView name, mail, ph, addr, drvrlcn, drvrvhcl, drvravlbl, drvrrting;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        id = prefs.getInt("myid", -1);
        userEmail = prefs.getString("email", "");
        userordriver = prefs.getString("userordriver", "");

        if (!isLoggedIn || userEmail.isEmpty() || userordriver.isEmpty() || id == -1) {
            // Redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_userprof);

        // Initialize Views
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        ph = findViewById(R.id.phone);
        addr = findViewById(R.id.addr);

        // Driver details
        drvrlcn = findViewById(R.id.drvrlicence);
        drvrvhcl = findViewById(R.id.drvrvehicle);
        drvravlbl = findViewById(R.id.drvravlblty);
        drvrrting = findViewById(R.id.drvrrating);

        logout1 = findViewById(R.id.logoutButton);

        logout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Initialize ProgressBar
        progressBar = findViewById(R.id.progressu);
        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        fetchUserData();
    }

    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userordriver", "");
        editor.putInt("myid", -1);
        editor.putBoolean("isLoggedIn", false);

        editor.apply();

        Intent intent = new Intent(Userprof.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchUserData() {
        new Thread(() -> {
            User user = DBConnection.getUserById(userordriver, id);  // Fetch from DB only once

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE); // Hide progress bar after fetching data

                if (user != null) {
                    // Save user details in SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("name", user.getName());
                    editor.putString("phone", user.getPhone()); // Fixed incorrect call
                    editor.putString("address", user.getAddress());
                    editor.apply();

                    name.setText("Name - " + user.getName());
                    mail.setText("Email - " + userEmail);
                    ph.setText("Phone - " + user.getPhone());
                    addr.setText("Address - " + user.getAddress());

                    if (userordriver.equals("drivers")) {

                        drvrlcn1 = findViewById(R.id.driver_license_layout);
                        drvrvhcl1 = findViewById(R.id.driver_vehicle_layout);
                        drvravlbl1 = findViewById(R.id.driver_availability_layout);
                        drvrrting1 = findViewById(R.id.driver_rating_layout);


                        available = user.getAvilability();
                        vehicle = user.getVehicle();

                        drvrlcn1.setVisibility(View.VISIBLE);
                        drvrvhcl1.setVisibility(View.VISIBLE);
                        drvravlbl1.setVisibility(View.VISIBLE);
                        drvrrting1.setVisibility(View.VISIBLE);

                        drvrlcn.setText("Licence - " + user.getLicencenum());
                        drvrvhcl.setText("Vehicle - " + vehicle);
                        drvravlbl.setText("Available - " + available);
                        drvrrting.setText("Rating - " + user.getRating());

                    }

                } else {
                    // Show alert dialog if user data is not found
                    try {

                        new AlertDialog.Builder(this)
                                .setTitle("User Data Not Found")
                                .setMessage("Check your Internet Connection.")
                                .setPositiveButton("Retry", (dialog, which) -> {
                                    progressBar.setVisibility(View.VISIBLE); // Show ProgressBar before retry
                                    fetchUserData();
                                })
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Try after Some time...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (userordriver.equals("users")) {
            Intent intent = new Intent(this, MainActivity.class); // Change MainActivity to your desired previous activity
            startActivity(intent);
            finish();
        } else if (userordriver.equals("drivers")) {
            Intent intent = new Intent(this, DriverMainActity.class); // Change MainActivity to your desired previous activity
            startActivity(intent);
            finish();
        }
    }


}