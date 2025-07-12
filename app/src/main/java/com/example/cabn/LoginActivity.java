package com.example.cabn;

import static com.example.cabn.DBConnection.checkLogin;
import static com.example.cabn.DBConnection.sendOTP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, otpInput;
    private MaterialCardView otpp;
    private TextView resendCode, register;
    private RadioGroup radioGroup;
    private String userordriver = "users";
    private Button sendCode, nextBtn;
    SharedPreferences prefs;

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
        } else if (isLoggedIn && userorDRIVER.equals("drivers")) {
            startActivity(new Intent(this, DriverMainActity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        MaterialButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setVisibility(View.GONE);

        emailInput = findViewById(R.id.emaill);
        otpInput = findViewById(R.id.otp_input);
        otpp = findViewById(R.id.opt_layout);
        resendCode = findViewById(R.id.resend_code);
        register = findViewById(R.id.register);
        sendCode = findViewById(R.id.send_code);
        nextBtn = findViewById(R.id.next_btn);

        radioGroup = findViewById(R.id.userrrtypppp);

        otpp.setVisibility(View.GONE);
        resendCode.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup groupch, int checkedId) {
                if (checkedId == R.id.usr) {
                    userordriver = "users";
                } else if (checkedId == R.id.drvr) {
                    userordriver = "drivers";
                }
                Toast.makeText(LoginActivity.this, "Selected: " + userordriver, Toast.LENGTH_SHORT).show();
            }
        });

        //send otp
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                sendresend(userordriver, email);
            }
        });

        //resend code
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE); // Show progress bar
                String email = emailInput.getText().toString().trim();
                sendresend(userordriver, email);
            }
        });

        // Register Link
        register.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, Registration.class));
        });

        //login
        nextBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String otp = otpInput.getText().toString().trim();

            if (TextUtils.isEmpty(otp) || otp.length() != 4) {
                Toast.makeText(LoginActivity.this, "Enter a valid 4-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Run in background to avoid UI freeze
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                int isLogin = checkLogin(userordriver, email, Integer.parseInt(otp));

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isLogin > 0) { // Successful login
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("email", email);
                        editor.putString("userordriver", userordriver);
                        editor.putInt("myid", isLogin);

                        editor.apply();

                        // Delete OTP in a separate thread
                        executor.execute(() -> DBConnection.deleteOTP(email));

                        // Ensure activity transition happens correctly
                        Intent i;
                        if ("drivers".equals(userordriver)) {
                            i = new Intent(LoginActivity.this, DriverMainActity.class);
                            startActivity(i);
                            finish();
                        } else {
                            i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }

                        Toast.makeText(LoginActivity.this, isLoggedIn+email+userordriver+isLogin, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid OTP or Something went Wrong...", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });


    }

    private void sendresend(String userordriver, String email) {
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Sending OTP...");
        progressDialog.setCancelable(false); // Prevent user from dismissing it manually
        progressDialog.show();

        new Thread(() -> {

            boolean isOTPSent = DBConnection.sendOTP(userordriver, email);

            runOnUiThread(() -> {
                // Dismiss ProgressDialog
                progressDialog.dismiss();

                if (isOTPSent) {
                    Toast.makeText(LoginActivity.this, "OTP sent to your email", Toast.LENGTH_LONG).show();
                    otpp.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    resendCode.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(LoginActivity.this, "Email not registered or OTP sending failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

}
