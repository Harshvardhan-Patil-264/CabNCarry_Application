package com.example.cabn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.List;

// This activity handles the driver's main interface, showing ride requests and logout functionality
public class DriverMainActity extends AppCompatActivity {
    SharedPreferences prefs;
    private RecyclerView recyclerView;
    private String userOrDriver, vhcl, avl;
    private int id;
    private ProgressBar progressBar;
    private MaterialButton logout1;
    private TextView nfnd;
    private MyRideAdapter requestsDriver;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        id = prefs.getInt("myid", -1);
        String driverEmail = prefs.getString("email", "");
        userOrDriver = prefs.getString("userordriver", "");

        if (!isLoggedIn || driverEmail.isEmpty() || userOrDriver.isEmpty() || !userOrDriver.equals("drivers") || id == -1) {
            // Redirect to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_driver_main_acitity); // Ensure layout file exists

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressd);
        nfnd = findViewById(R.id.nofnd);
        nfnd.setVisibility(View.GONE);
        logout1 = findViewById(R.id.logoutButton);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // Showing the ride requests to drivers
        recyclerView = findViewById(R.id.rcntrequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load initial data
        Starting();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            FetchRides();
        });

        logout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void Starting() {
        new Thread(() -> {
            User user = DBConnection.getUserById(userOrDriver, id);

            runOnUiThread(() -> {
                if (user != null) {
                    vhcl = user.getVehicle();
                    avl = user.getAvilability();
                    if (vhcl != null && avl != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        FetchRides();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    showErrorDialog();
                }
            });
        }).start();
    }

    private void FetchRides() {
        new Thread(() -> {
            List<RideRequestsDriver> fetchedRides = DBConnection.getRequests(this, vhcl);

            runOnUiThread(() -> {
                swipeRefreshLayout.setRefreshing(false); // Stop SwipeRefreshLayout animation
                progressBar.setVisibility(View.GONE);

                if (fetchedRides.isEmpty()) {
                    nfnd.setVisibility(View.VISIBLE);
                    Toast.makeText(DriverMainActity.this, "No rides found", Toast.LENGTH_SHORT).show();
                } else {
                    nfnd.setVisibility(View.GONE);
                }

                if (requestsDriver == null) {
                    requestsDriver = new MyRideAdapter(fetchedRides);
                    recyclerView.setAdapter(requestsDriver);
                } else {
                    requestsDriver.updateList(fetchedRides);
                }
            });
        }).start();
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet...")
                .setMessage("Check the Internet Connection for Process....")
                .setPositiveButton("Retry", (dialog, which) -> Starting())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Logout method
    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putInt("myid", -1);
        editor.putString("userordriver", "");
        editor.apply();

        Intent intent = new Intent(DriverMainActity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usermenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Menu Actions
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.prof) {
            Intent intent = new Intent(DriverMainActity.this, Userprof.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.hist) {
            Intent intent = new Intent(DriverMainActity.this, Userhist.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }
}
