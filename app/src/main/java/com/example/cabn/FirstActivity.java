package com.example.cabn;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirstActivity extends AppCompatActivity {
    private LinearLayout vieww2;
    private CardView vieww1;
    SharedPreferences prefs;
    private static final int SPLASH_TIME = 3000; // Max wait time (3 sec)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String userorDRIVER = prefs.getString("userordriver", "");

        if (isLoggedIn && userorDRIVER.equals("users")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        } else if (isLoggedIn && userorDRIVER.equals("drivers")) {
            startActivity(new Intent(this, DriverMainActity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_first);

        vieww1 = findViewById(R.id.view1);
        vieww2 = findViewById(R.id.view2);

        // Start database connection in the background
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Connection connection = DBConnection.getConn();

            runOnUiThread(() -> {
                if (connection != null) {
                    Toast.makeText(this, "Connected to Database", Toast.LENGTH_SHORT).show();
                    proceedAfterConnection();
                } else {
                    showRetryDialog();
                }
            });
        });
    }

    private void proceedAfterConnection() {
        // Hide splash screen immediately after a successful connection
        vieww1.setVisibility(View.GONE);
        vieww2.setVisibility(View.VISIBLE);

        // Check if user is already logged in
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String userorDRIVER = prefs.getString("userordriver", "");

        new Handler().postDelayed(() -> {
            if (isLoggedIn && userorDRIVER.equals("users")) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (isLoggedIn && userorDRIVER.equals("drivers")) {
                startActivity(new Intent(this, DriverMainActity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, SPLASH_TIME); // Slight delay for better UX
    }

    private void showRetryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Connection Failed")
                .setMessage("Unable to connect to the database. Do you want to retry?")
                .setPositiveButton("Retry", (dialog, which) -> recreate()) // Restart activity
                .setNegativeButton("Exit", (dialog, which) -> finishAffinity()) // Close app
                .setCancelable(false) // Prevent closing the dialog accidentally
                .show();
    }
}
