package com.example.ecotrack.Authentication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ecotrack.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class AdminSignup extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 101;

    private TextInputEditText etCompanyName, etWarehouseLocation, etEmail, etMobile, etPassword;
    private MaterialButton btnSendOtp;
    private int otp;
    private String companyName, warehouseLocation, email, mobile, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_signup);

        // Initialize Views
        etCompanyName = findViewById(R.id.etCompanyName);
        etWarehouseLocation = findViewById(R.id.etWarehouseLocation);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        // Send OTP Button Click Listener
        btnSendOtp.setOnClickListener(view -> validateAndRequestPermission());
    }

    private void validateAndRequestPermission() {
        // Fetch input values
        companyName = etCompanyName.getText().toString().trim();
        warehouseLocation = etWarehouseLocation.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        mobile = etMobile.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        // Input Validations
        if (companyName.isEmpty()) {
            etCompanyName.setError("Enter Company Name");
            return;
        }
        if (warehouseLocation.isEmpty()) {
            etWarehouseLocation.setError("Enter Warehouse Location");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid Email");
            return;
        }
        if (!Patterns.PHONE.matcher(mobile).matches() || mobile.length() != 10) {
            etMobile.setError("Enter a valid 10-digit Mobile Number");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        // Check SMS Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            sendOtp();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    private void sendOtp() {
        // Generate 4-digit OTP
        otp = new Random().nextInt(9000) + 1000;

        // Send OTP via SMS
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobile, null, "Your EcoTrack OTP is: " + otp, null, null);
            Toast.makeText(this, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show();

            // Pass Data to OtpVerificationActivity
            Intent intent = new Intent(AdminSignup.this, OtpVerificationActivity.class);
            intent.putExtra("companyName", companyName);
            intent.putExtra("warehouseLocation", warehouseLocation);
            intent.putExtra("email", email);
            intent.putExtra("mobile", mobile);
            intent.putExtra("password", password);
            intent.putExtra("otp", otp);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to send OTP. Check permissions!", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle SMS Permission Request Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendOtp();
            } else {
                Toast.makeText(this, "SMS Permission Denied! Cannot send OTP.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
