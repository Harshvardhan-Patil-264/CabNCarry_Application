package com.example.cabn;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationCheck {
    private static final int REQUEST_LOCATION = 1;
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public interface LocationListener {
        void onLocationReceived(double latitude, double longitude);
    }

    private LocationListener listener;

    public LocationCheck(Context context, LocationListener listener) {
        this.context = context;
        this.listener = listener;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getLocation() {
        if (!isGPSEnabled()) {
            promptGPSActivation();
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            } else {
                Log.e("LocationCheck", "Context is not an Activity, cannot request permissions.");
            }
            return;
        }

        // Create Location Request
        LocationRequest locationRequest = new LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 10000) // Every 10 sec
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(5000) // Minimum 5 sec interval
                .setMaxUpdateDelayMillis(20000) // Max delay 20 sec
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    double lat = locationResult.getLastLocation().getLatitude();
                    double lng = locationResult.getLastLocation().getLongitude();

                    if (listener != null) {
                        listener.onLocationReceived(lat, lng);
                    }
                }
            }
        };

        // Start Location Updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private boolean isGPSEnabled() {
        android.location.LocationManager locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    private void promptGPSActivation() {
        new android.app.AlertDialog.Builder(context)
                .setMessage("Enable GPS for better location accuracy")
                .setCancelable(false)
                .setPositiveButton("Enable", (dialog, which) -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
