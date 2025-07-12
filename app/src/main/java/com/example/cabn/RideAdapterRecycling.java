package com.example.cabn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//ride for users rideshist

public class RideAdapterRecycling extends RecyclerView.Adapter<RideAdapterRecycling.RideViewHolder> {

    private List<Rides> ridesList;

    public RideAdapterRecycling(List<Rides> ridesList) {
        this.ridesList = new ArrayList<>(ridesList);
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ride_data, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Rides ride = ridesList.get(position);
        Context context = holder.itemView.getContext();

        // Get user type from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userOrDriver = prefs.getString("userordriver", "");

        holder.startPlace.setText("From: " + ride.getStartPlace());
        holder.endPlace.setText("To: " + ride.getEndPlace());
        holder.bookedDateTime.setText("Date: " + ride.getBookeddaytime());
        holder.status.setText(ride.getStatus());
        holder.carType.setText("Car: " + ride.getCartype());
        holder.totalPrice.setText("₹" + ride.getTotalPrice() + "\n\nPay Mode - " + ride.getMode());

        if("users".equalsIgnoreCase(userOrDriver)) {
            holder.ridecode.setVisibility(View.VISIBLE);
            holder.ridecode.setTextColor(Color.BLACK);
            holder.ridecode.setText("Ride-Code : \t" + ride.getRidecode());
        } else if ("drivers".equalsIgnoreCase(userOrDriver)) {
            holder.ridecode.setVisibility(View.GONE);
        }

        // Set click listener based on user type and ride status
        if (userOrDriver.equals("users")) {
            if ("complete".equalsIgnoreCase(ride.getStatus()) && "cancelled".equalsIgnoreCase(ride.getStatus())) {
                holder.l11.setOnClickListener(null);
            } else {
                setupRideClickListener(holder.l11, context, ride, userOrDriver);
            }
        } else if (userOrDriver.equals("drivers")) {
            if ("complete".equalsIgnoreCase(ride.getStatus()) && "cancelled".equalsIgnoreCase(ride.getStatus())) {
                holder.l11.setOnClickListener(null);
            } else {
                setupRideClickListener(holder.l11, context, ride, userOrDriver);
            }
        } else {
            holder.l11.setOnClickListener(null);
        }

    }

    private void setupRideClickListener(View view, Context context, Rides ride, String userOrDriver) {
        view.setOnClickListener(v -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int myId = prefs.getInt("myid", -1);

            if (myId == -1 || userOrDriver.isEmpty()) {
                Toast.makeText(context, "Error: User ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userOrDriver.equals("users") && "pending".equalsIgnoreCase(ride.getStatus())) {
                showCancelRideDialog(context, ride);
            } else if (userOrDriver.equals("drivers")) {
                if ("accept".equalsIgnoreCase(ride.getStatus())) {
                    showStartRideDialog(context, ride, myId);
                } else if ("ongoing".equalsIgnoreCase(ride.getStatus())) {
                    showCompleteRideDialog(context, ride, myId);
                }

            }
        });
    }

    private void showCancelRideDialog(Context context, Rides ride) {
        new AlertDialog.Builder(context)
                .setTitle("Cancel the Ride")
                .setMessage("The Ride is currently ' " + ride.getStatus())
                .setPositiveButton("Cancel", (dialog, which) -> {
                    new Thread(() -> {
                        boolean canceled = DBConnection.CancleRide(ride.getRideIdd());
                        ((Activity) context).runOnUiThread(() -> {
                            if (canceled) {
                                Toast.makeText(context, "Ride Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                refreshActivity(context);
                            } else {
                                Toast.makeText(context, "Error: Unable to cancel the ride.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("SeeMap", (dialog, which) -> {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("stPlacee", ride.getStart());
                    editor.putString("edPlacee", ride.getEnd());
                    editor.apply();

                    Intent i = new Intent(context, SeeMap.class);
                    context.startActivity(i);
                    ((Activity)context).finish();
                })
                .show();
    }

    private void showStartRideDialog(Context context, Rides ride, int driverId) {
        EditText ridecodee = new EditText(context);
        ridecodee.setHint("Enter Ride Code...");
        ridecodee.setPadding(50, 30, 50, 30);
        // max length  input 4
        ridecodee.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        new AlertDialog.Builder(context)
                .setTitle("Start Ride")
                .setMessage("The Ride is currently '" + ride.getStatus() + "'. \n\nPlease enter the ride code to start.")
                .setView(ridecodee)
                .setPositiveButton("Start", (dialog, which) -> {
                    String ridecode = ridecodee.getText().toString().trim();
                    String usercode = String.valueOf(ride.getRidecode()); // Ensure this returns the correct type

                    if (ridecode.equals(usercode)) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            boolean completed = DBConnection.StartRide(ride.getRideIdd(), driverId);
                            ((Activity) context).runOnUiThread(() -> {
                                if (completed) {
                                    Toast.makeText(context, "Ride Started Successfully!", Toast.LENGTH_SHORT).show();
                                    refreshActivity(context);
                                } else {
                                    Toast.makeText(context, "Error: Try Again...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    } else {
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Ride-Code is Incorrect...", Toast.LENGTH_SHORT).show()
                        );
                    }
                })
                .setNegativeButton("SeeMap", (dialog, which) -> {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("stPlacee", ride.getStart());
                    editor.putString("edPlacee", ride.getEnd());
                    editor.apply();

                    Intent i = new Intent(context, SeeMap.class);
                    context.startActivity(i);
                    ((Activity)context).finish();

                })
                .show();
    }


    private void showCompleteRideDialog(Context context, Rides ride, int driverId) {
        new AlertDialog.Builder(context)
                .setTitle("Complete Ride")
                .setMessage("The Ride is currently ' " + ride.getStatus())
                .setPositiveButton("Complete", (dialog, which) -> {
                    new Thread(() -> {
                        boolean completed = DBConnection.CompleteRide(ride.getRideIdd(), driverId);
                        ((Activity) context).runOnUiThread(() -> {
                            if (completed) {
                                Toast.makeText(context, "Ride Completed Successfully!", Toast.LENGTH_SHORT).show();
                                refreshActivity(context);
                            } else {
                                Toast.makeText(context, "Error: Try Again...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                })
                .setNegativeButton("SeeMap", (dialog, which) -> {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("stPlacee", ride.getStart());
                    editor.putString("edPlacee", ride.getEnd());
                    editor.apply();

                    Intent i = new Intent(context, SeeMap.class);
                    context.startActivity(i);
                    ((Activity)context).finish();

                })
                .show();
    }

    private void refreshActivity(Context context) {
        Intent intent = new Intent(context, Userhist.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    @Override
    public int getItemCount() {
        return ridesList.size();
    }

    public void updateList(List<Rides> newList) {
        this.ridesList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView startPlace, endPlace, bookedDateTime, status, carType, totalPrice, ridecode;
        CardView l11;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            l11 = itemView.findViewById(R.id.ridedetails);
            startPlace = itemView.findViewById(R.id.txtStart);
            endPlace = itemView.findViewById(R.id.txtEnd);
            bookedDateTime = itemView.findViewById(R.id.txtTime);
            status = itemView.findViewById(R.id.txtStatus);
            carType = itemView.findViewById(R.id.txtCar);
            totalPrice = itemView.findViewById(R.id.txtPrice);
            ridecode = itemView.findViewById(R.id.txtRideCode);
        }
    }
}
