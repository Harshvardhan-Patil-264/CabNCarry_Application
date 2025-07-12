package com.example.cabn;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Rides {
    private int rideIdd;
    private int userId;
    private int driverId;
    private String userphone;
    private String startPlace;
    private String endPlace;
    private String bookeddaytime;
    private String status;
    private String cartype;
    private String totalPrice;
    private int ridecode;
    private String mode;
    private Context context;

    //fetch and store from db
    public Rides (Context context, int rideIdd, int userId, int driverId, String userphone, String startPlace, String endPlace, String bookeddaytime, String status, String cartype, String totalprice, int ridecode, String mode) {
        this.context = context;
        this.rideIdd = rideIdd;
        this.userId = userId;
        this.driverId = driverId;
        this.userphone = userphone;
        this.startPlace = startPlace;
        this.endPlace = endPlace;
        this.bookeddaytime = bookeddaytime;
        this.status = status;
        this.cartype = cartype;
        this.totalPrice = totalprice;
        this.ridecode = ridecode;
        this.mode = mode;
    }

    // return require where need
    // Alt + insert shortcut key for getter & setter

    public int getRideIdd() {
        return rideIdd;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserphone() {
        return userphone;
    }

    public String getStartPlace() {
        return getAddressFromLatLng(startPlace);
    }

    public String getStart() {
        return startPlace;
    }

    public String getEndPlace() {
        return getAddressFromLatLng(endPlace);
    }
    public String getEnd() {
        return endPlace;
    }

    public String getBookeddaytime() {
        return bookeddaytime;
    }

    public String getStatus() {
        return status;
    }

    public String getCartype() {
        return cartype;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public int getRidecode() {
        return ridecode;
    }
    public String getMode() { return mode; }



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

                //
                StringBuilder addressString = new StringBuilder();
                if (address.getSubLocality() != null) {
                    addressString.append(address.getSubLocality()).append(", "); //village name
                }
                if (address.getLocality() != null) {
                    addressString.append(address.getLocality()).append(", "); //city or state
                }
                if (address.getPostalCode() != null) {
                    addressString.append(address.getPostalCode()); //pincode
                }

                return addressString.toString().trim();
            }
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }


}
