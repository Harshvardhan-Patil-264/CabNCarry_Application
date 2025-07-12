package com.example.cabn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Registration extends AppCompatActivity {
    private EditText usrname, phone, email, addr;
    Button rgstr;
    TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Total 9 fields
        usrname = findViewById(R.id.name); //name
        phone = findViewById(R.id.phone); //phone no.
        email = findViewById(R.id.mail); //email
        addr = findViewById(R.id.addresss); //addr

        log = findViewById(R.id.login); //logintext
        rgstr = findViewById(R.id.reg_next_btn); //registration button

        //login text redirect
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registration.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


        //registreation Button
        rgstr.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         String unm = usrname.getText().toString().trim();
                                         String ml = email.getText().toString().trim();
                                         String phoneInput = phone.getText().toString().trim();
                                         String adr = addr.getText().toString().trim();

                                         // Validate fields
                                         if (unm.isEmpty() || ml.isEmpty() || phoneInput.isEmpty() || adr.isEmpty()) {
                                             Toast.makeText(Registration.this, "All fields are required", Toast.LENGTH_SHORT).show();
                                             return;
                                         }

                                         // Validate email format
                                         if (!android.util.Patterns.EMAIL_ADDRESS.matcher(ml).matches()) {
                                             Toast.makeText(Registration.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                                             return;
                                         }

                                         // Validate phone number (should be exactly 10 digits)
                                         if (!phoneInput.matches("\\d{10}")) {
                                             Toast.makeText(Registration.this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                                             return;
                                         }

                                         new Thread(() -> {
                                             // Proceed with registration
                                             boolean isreg = DBConnection.RegisterUser(unm, phoneInput, ml, adr);

                                             runOnUiThread(() -> {
                                                 if (isreg) {
                                                     Toast.makeText(Registration.this, "Successfully Registered...", Toast.LENGTH_SHORT).show();
                                                     Intent i = new Intent(Registration.this, LoginActivity.class);
                                                     startActivity(i);
                                                     finish();
                                                 } else {
                                                     Toast.makeText(Registration.this, "Try Again...", Toast.LENGTH_SHORT).show();
                                                 }
                                             });
                                         }).start();  // Start the Thread
                                     }
                                 }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}