package com.example.cabn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

//userhist, rideadapterrecycling(show all previous rides through ride_data.xml), rides(fetch ride details), DBConnection

public class Userhist extends AppCompatActivity {

    private SharedPreferences prefs;
    private Chip a, p, cmplt, cncl;
    private RecyclerView recyclerView;
    private TextView nfnd;
    private MaterialButton logout1;
    private ProgressBar progressBar;
    private String userordriver;
    private RideAdapterRecycling rideAdapterRecycling;
    private List<Rides> rides = new ArrayList<>();
    private List<Rides> filteredList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        int id = prefs.getInt("myid", -1);
        String userEmail = prefs.getString("email", "");
        userordriver = prefs.getString("userordriver", "");

        if (!isLoggedIn || userEmail.isEmpty() || userordriver == null || userordriver.isEmpty() || id == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_userhist);

        a = findViewById(R.id.all);
        p = findViewById(R.id.pend);
        cmplt = findViewById(R.id.cmplt);
        cncl = findViewById(R.id.cncl);
        nfnd = findViewById(R.id.nofnd);
        progressBar = findViewById(R.id.progressh);
        logout1 = findViewById(R.id.logoutButton);

        recyclerView = findViewById(R.id.recyclerViewRides);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show progress bar while loading rides
        progressBar.setVisibility(View.VISIBLE);
        nfnd.setVisibility(View.GONE);

        if (userordriver.equals("drivers")) {
            p.setText("Accepted");
        } else {
            p.setText("Pending");
        }

        // Fetch all rides
        fetchAllRides(id);

        // Button Click Listeners
        a.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            fetchAllRides(id);
        });

        p.setOnClickListener(v -> {
            if (userordriver.equals("users")) {
                filterRides("Pending", "Ongoing", "Accept");
            } else {
                filterRides("Accept", "Ongoing");
            }
        });

        cmplt.setOnClickListener(v -> filterRides("Complete"));
        cncl.setOnClickListener(v -> filterRides("Cancelled"));

        logout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putInt("myid", -1);
        editor.putString("userordriver", "");
        editor.apply();

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void filterRides(String status) {
        filteredList.clear();
        for (Rides ride : rides) {
            if (ride.getStatus().equalsIgnoreCase(status.trim())) {
                filteredList.add(ride);
            }
        }

        if (rideAdapterRecycling != null) {
            rideAdapterRecycling.updateList(filteredList);
        }

        // Show "No rides found" when filtered list is empty
        nfnd.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }


    private void filterRides(String status, String status1) {
        filteredList.clear();
        for (Rides ride : rides) {
            if (ride.getStatus().equalsIgnoreCase(status.trim()) || ride.getStatus().equalsIgnoreCase(status1.trim())) {
                filteredList.add(ride);
            }
        }

        if (rideAdapterRecycling != null) {
            rideAdapterRecycling.updateList(filteredList);
        }

        // Show "No rides found" when filtered list is empty
        nfnd.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void filterRides(String status, String status1, String status2) {
        filteredList.clear();
        for (Rides ride : rides) {
            if (ride.getStatus().equalsIgnoreCase(status.trim()) || ride.getStatus().equalsIgnoreCase(status1.trim()) || ride.getStatus().equalsIgnoreCase(status2.trim())) {
                filteredList.add(ride);
            }
        }

        if (rideAdapterRecycling != null) {
            rideAdapterRecycling.updateList(filteredList);
        }

        // Show "No rides found" when filtered list is empty
        nfnd.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void fetchAllRides(int userId) {
        new Thread(() -> {

            List<Rides> fetchedRides = DBConnection.getUserRide(this, userordriver, userId);


            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);

                if (fetchedRides.isEmpty()) {
                    nfnd.setVisibility(View.VISIBLE);
                    Toast.makeText(Userhist.this, "No rides found", Toast.LENGTH_SHORT).show();
                } else {
                    nfnd.setVisibility(View.GONE);
                }

                rides.clear();
                rides.addAll(fetchedRides);
                filteredList.clear();
                filteredList.addAll(fetchedRides);

                if (rideAdapterRecycling == null) {
                    rideAdapterRecycling = new RideAdapterRecycling(filteredList);
                    recyclerView.setAdapter(rideAdapterRecycling);
                } else {
                    rideAdapterRecycling.updateList(filteredList);
                }
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent intent;
            if (userordriver.equals("users")) {
                intent = new Intent(this, MainActivity.class);
            } else {
                intent = new Intent(this, DriverMainActity.class);
            }
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("Userhist", "Error handling back press", e);
        }
    }
}
