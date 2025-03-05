package com.example.ecotrack.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecotrack.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class OtpVerificationActivity extends AppCompatActivity {

    private TextInputEditText etOtp;
    private MaterialButton btnVerifyOtp;
    private String companyName, warehouseLocation, email, mobile, password;
    private int receivedOtp;

    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_verification);

        // Initialize Views
        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("EcoTrack").child("AdminUsers");

        // Retrieve data from Intent
        Intent intent = getIntent();
        companyName = intent.getStringExtra("companyName");
        warehouseLocation = intent.getStringExtra("warehouseLocation");
        email = intent.getStringExtra("email");
        mobile = intent.getStringExtra("mobile");
        password = intent.getStringExtra("password");
        receivedOtp = intent.getIntExtra("otp", 0);

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Admin Signup");
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        // Verify OTP Button Click
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());
    }

    private void verifyOtp() {
        String enteredOtp = etOtp.getText().toString().trim();

        if (enteredOtp.isEmpty() || enteredOtp.length() != 4) {
            etOtp.setError("Enter a valid 4-digit OTP");
            return;
        }

        if (Integer.parseInt(enteredOtp) == receivedOtp) {
            progressDialog.show();
            storeAdminDetails();
        } else {
            Toast.makeText(this, "Invalid OTP. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeAdminDetails() {
        // Store data under "AdminUsers/{mobile}"
        HashMap<String, String> adminData = new HashMap<>();
        adminData.put("companyName", companyName);
        adminData.put("warehouseLocation", warehouseLocation);
        adminData.put("email", email);
        adminData.put("mobile", mobile);
        adminData.put("password", password);

        databaseReference.child(mobile).setValue(adminData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(OtpVerificationActivity.this, "Admin Registered Successfully!", Toast.LENGTH_LONG).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(OtpVerificationActivity.this, "Failed to register admin!", Toast.LENGTH_SHORT).show();
                });
    }
}
