package com.example.cabn;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RideRequestsDriver {
    private int rideId; //used to delete after accept
    private int userId; //used to find user details like user name,
    private int acptid; //find id in userride and update driverid with accepted driver id
    private String stPlace;
    private String edPlace;
    private String vehicle;
    private float price;
    private int ridecode;
    private Context context;

    public RideRequestsDriver(Context context, int rideId, int userId, int acptid, String stPlace, String edPlace, String vehicle, float price, int ridecode) {
        this.context = context;
        this.rideId = rideId;
        this.userId = userId;
        this.acptid = acptid;
        this.stPlace = stPlace;
        this.edPlace = edPlace;
        this.vehicle = vehicle;
        this.price = price;
        this.ridecode = ridecode;
    }

    public int getRideId() {
        return rideId;
    }

    public int getUserId() {
        return userId;
    }

    public int getAcptid() {
        return acptid;
    }

    public String getStPlace() {
        return getAddressFromLatLng(stPlace);
    }
    public String getStart() {
        return stPlace;
    }

    public String getEdPlace() {
        return getAddressFromLatLng(edPlace);
    }
    public String getEnd() {
        return edPlace;
    }

    public String getVehicle() {
        return vehicle;
    }

    public float getPrice() {
        return price;
    }

    public int getRidecode() {
        return ridecode;
    }

    private String getAddressFromLatLng(String latLngStr) {
        if (latLngStr == null || !latLngStr.contains(",")) {
            return "Invalid Coordinates"; // Ensure correct format
        }

        String[] latLng = latLngStr.split(",");
        if (latLng.length != 2) {
            return "Invalid Coordinates";
        }

        try {
            double latitude = Double.parseDouble(latLng[0].trim());
            double longitude = Double.parseDouble(latLng[1].trim());

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Extract only "bargewadi, maharashtra, 416211"
                StringBuilder addressString = new StringBuilder();
                if (address.getSubLocality() != null) {
                    addressString.append(address.getSubLocality()).append(", ");
                }
                if (address.getLocality() != null) {
                    addressString.append(address.getLocality()).append(", ");
                }
                if (address.getPostalCode() != null) {
                    addressString.append(address.getPostalCode());
                }

                return addressString.toString().trim();
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location"; // Fallback if failed
    }


}
