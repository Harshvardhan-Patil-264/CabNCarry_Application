package com.example.cabn;

public class User {
    private String name, phone, address, licencenum, vehicle, avilability, rating;

    public User(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public User(String name, String phone, String address, String licencenum, String vehicle, String avilability, String rating) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.licencenum = licencenum;
        this.vehicle = vehicle;
        this.avilability = avilability;
        this.rating = rating;
    }

    // Getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getLicencenum() {
        return licencenum;
    }
    public String getVehicle() {
        return vehicle;
    }
    public String getAvilability() {
        return avilability;
    }
    public String getRating() {
        return rating;
    }
}