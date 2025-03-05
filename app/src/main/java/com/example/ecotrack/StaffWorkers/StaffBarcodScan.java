package com.example.ecotrack.StaffWorkers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ecotrack.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class StaffBarcodScan extends AppCompatActivity {

    private TextView tvScannedResult;
    private DatabaseReference database;
    private MaterialCardView btnExpiry,btnDefect;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startBarcodeScanner();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String scannedCode = result.getContents();
                    tvScannedResult.setText("Scanned: " + scannedCode);
                    checkProductInFirebase(scannedCode);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_barcod_scan);

        btnDefect=findViewById(R.id.btnDefected);
        btnExpiry=findViewById(R.id.btnExpiry);

        tvScannedResult = findViewById(R.id.tvScannedResult);

        findViewById(R.id.btnScan).setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startBarcodeScanner();
            } else {
                requestCameraPermission();
            }
        });

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance().getReference("Product");
    }

    // Check Camera Permission
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    // Request Camera Permission
    private void requestCameraPermission() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    // Start Barcode Scanner
    private void startBarcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a barcode or QR code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true); // ✅ Open in Portrait mode
        barcodeLauncher.launch(options);
    }

    // Check if scanned product exists in Firebase
    private void checkProductInFirebase(String scannedBatchNo) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;

                // Iterate through categories like Medicine, Grocery, etc.
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        String batchNo = productSnapshot.child("batchNo").getValue(String.class);
                        if (batchNo != null && batchNo.equals(scannedBatchNo)) {
                            found = true;
                            String productName = productSnapshot.child("productName").getValue(String.class);
                            String mfgDate = productSnapshot.child("mfgDate").getValue(String.class);
                            String expDate = productSnapshot.child("expDate").getValue(String.class);
                            String status = productSnapshot.child("status").getValue(String.class);

                            String result = "✅ Product Found:\n\n" +
                                    "🔹 Name: " + productName + "\n" +
                                    "📆 MFG Date: " + mfgDate + "\n" +
                                    "⏳ EXP Date: " + expDate + "\n" +
                                    "📌 Status: " + status;

                            tvScannedResult.setText(result);
                            break; // Stop searching once found
                        }
                    }
                    if (found) break; // Exit category loop if product found
                }

                if (!found) {
                    tvScannedResult.setText("❌ Product Not Found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StaffBarcodScan.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
