package com.example.cabn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SeeMap extends AppCompatActivity {
    private SharedPreferences prefs;
    private WebView webView;
    private String stPlacee, edPlacee, userOrDriver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        int id = prefs.getInt("myid", -1);
        String userEmail = prefs.getString("email", "");
        userOrDriver = prefs.getString("userordriver", "");
        stPlacee = prefs.getString("stPlacee", "");
        edPlacee = prefs.getString("edPlacee", "");

        if (!isLoggedIn || userEmail.isEmpty() || userOrDriver == null || userOrDriver.isEmpty() || id == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_see_map);


        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        try {
            webView = findViewById(R.id.mapp1);

            if (stPlacee.isEmpty() || edPlacee.isEmpty()) {
                Toast.makeText(this, "No Fetched....", Toast.LENGTH_SHORT).show();
            } else {
                // Split by comma
                String[] startCoords = stPlacee.split(",");
                String[] endCoords = edPlacee.split(",");

                // Convert to double
                double startLat = Double.parseDouble(startCoords[0]);
                double startLng = Double.parseDouble(startCoords[1]);
                double endLat = Double.parseDouble(endCoords[0]);
                double endLng = Double.parseDouble(endCoords[1]);
                configureWebView(startLat, startLng, endLat, endLng);

            }
        } catch (Exception e) {
            Toast.makeText(this, " " + e, Toast.LENGTH_SHORT).show();
        }

    }


    private void configureWebView(double startLat, double startLng, double endLat, double endLng) {
        if (webView == null) {
            Toast.makeText(this, "WebView initialization failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        webView.loadUrl("file:///android_asset/mapp1.html");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // JavaScript function call
                String script = String.format("javascript:setRoute(%f, %f, %f, %f)",
                        startLat, startLng, endLat, endLng);

                webView.evaluateJavascript(script, null);
            }
        });
    }


    // WebView Lifecycle Management
    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent intent;
            if (userOrDriver.equals("users")) {
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