package com.example.cabn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.Locale;


public class MyRideAdapter extends RecyclerView.Adapter<MyRideAdapter.MyViewHolder> {

    private List<RideRequestsDriver> myrides;

    public MyRideAdapter(List<RideRequestsDriver> myrides) {
        this.myrides = myrides;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_rides, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RideRequestsDriver driver = myrides.get(position);
        Context context = holder.itemView.getContext();

        holder.stp.setText(driver.getStPlace());
        holder.edp.setText(driver.getEdPlace());

        holder.l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                int myid = prefs.getInt("myid", -1); // Retrieve driver ID from shared preferences

                if (myid != -1) {
                    new AlertDialog.Builder(context)
                            .setTitle("Confirm")
                            .setMessage(" Your Accepting Ride..")
                            .setPositiveButton("Accept", (dialog, which) -> {

                                // Perform database operation in a background thread
                                new Thread(() -> {
                                    boolean ac = DBConnection.AcceptRide(driver.getRideId(), myid, driver.getAcptid());

                                    // Update UI on the main thread
                                    ((Activity) context).runOnUiThread(() -> {
                                        if (ac) {
                                            Toast.makeText(context, "Successfully Ride Accepted...", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(context, Userhist.class);
                                            context.startActivity(i);
                                            ((Activity) context).finish();
                                        } else {
                                            Toast.makeText(context, "Already in Ride.\tTry Again...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }).start();

                            })
                            .setNegativeButton("SeeMap", (dialog, which) -> {

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("stPlacee", driver.getStart());
                                editor.putString("edPlacee", driver.getEnd());
                                editor.apply();

                                Intent i = new Intent(context, SeeMap.class);
                                context.startActivity(i);
                                ((Activity)context).finish();

                            })
                            .show();
                } else {
                    Toast.makeText(context, "Error: Your ID not found", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return myrides.size();
    }

    public void updateList(List<RideRequestsDriver> newList) {
        myrides.clear();
        myrides.addAll(newList);
        notifyDataSetChanged();
    }



    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView stp, edp;
        CardView l1;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            l1 = itemView.findViewById(R.id.acceptt);

            stp = itemView.findViewById(R.id.stPlace);
            edp = itemView.findViewById(R.id.endPlace);

        }
    }

}
