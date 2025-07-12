package com.example.cabn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SharedPreferences prefs;
    private float distancee = 0.0f;
    private String startLat, startLng, endLat, endLng, userordriver;
    private MaterialButton bookk;
    private int id;
    private LocationCheck locationCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login session
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        id = prefs.getInt("myid", -1);
        String userEmail = prefs.getString("email", "");
        userordriver = prefs.getString("userordriver", "");

        if (!isLoggedIn || userEmail.isEmpty() || !userordriver.equals("users") || id == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize UI
        initializeApp();

        // Initialize LocationCheck and fetch location
        locationCheck = new LocationCheck(this, (double latitude, double longitude) -> {
                Toast.makeText(this, latitude + "\n" + longitude, Toast.LENGTH_SHORT).show();
        });

        locationCheck.getLocation();
    }

    private void initializeApp() {
        setContentView(R.layout.activity_main);

        // Initialize UI Components
        bookk = findViewById(R.id.next);
        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        try {
            webView = findViewById(R.id.mapp);
            configureWebView();
        } catch (Exception e) {
            Toast.makeText(this, " " + e, Toast.LENGTH_SHORT).show();
        }

        bookk.setOnClickListener(v -> {

            if (startLat == null || startLng == null || endLat == null || endLng == null || distancee <= 0.0f) {

                Toast.makeText(MainActivity.this, "Please wait for data to load...", Toast.LENGTH_SHORT).show();
            } else {

                new LayoutShow(this).getView(id, "", distancee, startLat, startLng, endLat, endLng);

            }
        });

        MaterialButton logoutButton = findViewById(R.id.logoutButton);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> logout());
        }
    }

    private void configureWebView() {
        if (webView == null) {
            Toast.makeText(this, "WebView initialization failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.addJavascriptInterface(new WebAppInterface(), "AndroidInterface");

        webView.loadUrl("file:///android_asset/map.html");
    }

    public class WebAppInterface {

        @JavascriptInterface
        public void showDistance(String distance) {
            try {
                distancee = Float.parseFloat(distance);
                Log.d("WebView", "Distance: " + distancee);
            } catch (NumberFormatException e) {
                Log.e("WebView", "Invalid distance value: " + distance, e);
                distancee = 0.0f;
            }
        }

        @JavascriptInterface
        public void showLatLng(String stLat, String stLng, String edLat, String edLng) {
            runOnUiThread(() -> {
                startLat = stLat;
                startLng = stLng;
                endLat = edLat;
                endLng = edLng;
                Log.d("WebView", "LatLng: " + startLat + ", " + startLng + " -> " + endLat + ", " + endLng);
            });
        }



    }

    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.putInt("myid", -1);
        editor.putString("userordriver", "");
        editor.apply();

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationCheck != null) {
            locationCheck.stopLocationUpdates();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usermenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.prof) {
            startActivity(new Intent(MainActivity.this, Userprof.class));
            finish();
        } else if (id == R.id.hist) {
            startActivity(new Intent(MainActivity.this, Userhist.class));
            finish();
        } else if (id == R.id.logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }
}
