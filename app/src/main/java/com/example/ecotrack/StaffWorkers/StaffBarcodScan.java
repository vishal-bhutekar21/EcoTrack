package com.example.ecotrack.StaffWorkers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.ecotrack.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class StaffBarcodScan extends AppCompatActivity {
    private TextView tvScannedResult;
    private ChipGroup chipGroup;
    private MaterialButton btnSubmit;
    private TextInputEditText etExpDate,etMfgDate,etProductName;

    private DatabaseReference database;
    private StorageReference storageRef;
    private String scannedBatchNo;

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
                    scannedBatchNo = result.getContents();
                    tvScannedResult.setText("Scanned: " + scannedBatchNo);
                    fetchProductFromCSV(scannedBatchNo);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_barcod_scan);


        tvScannedResult = findViewById(R.id.tvScannedResult);
        chipGroup = findViewById(R.id.chipGroupCategory);
        btnSubmit = findViewById(R.id.btnSubmitEntry);

        etExpDate = findViewById(R.id.etExpDate);
        etMfgDate = findViewById(R.id.etMfgDate);
        etProductName = findViewById(R.id.etProductName);


        findViewById(R.id.btnScan).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnSubmit.setOnClickListener(v -> storeScannedProduct());

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("scanned_products");
        storageRef = FirebaseStorage.getInstance().getReference("dummy_products_updated.csv");
    }

    private void startBarcodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a barcode or QR code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barcodeLauncher.launch(options);
    }



    private void fetchProductFromCSV(String batchNo) {
        Log.d("CSV_FETCH", "Fetching CSV file from Firebase Storage...");

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)))) {
                String line;
                boolean found = false;
                int lineCount = 0; // To track the number of lines read

                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    Log.d("CSV_LINE", "Line " + lineCount + ": " + line); // Log each line read

                    String[] columns = line.split(",");
                    if (columns.length >= 5) { // Ensure the CSV has the expected columns
                        String csvBatchNo = columns[0].trim();
                        Log.d("CSV_COMPARE", "Comparing: Scanned=" + batchNo + " | CSV=" + csvBatchNo);

                        if (csvBatchNo.trim().equals(batchNo.trim())) {
                            found = true;
                            String productName = columns[1].trim();
                            String productType = columns[2].trim();
                            String mfgDate = columns[3].trim();
                            String expDate = columns[4].trim();

                            Log.d("CSV_MATCH", "Product Found: " + productName);

                            String result = "✅ Product Found:\n\n" +
                                    "🔹 Name: " + productName + "\n" +
                                    "🛒 Type: " + productType + "\n" +
                                    "📆 MFG Date: " + mfgDate + "\n" +
                                    "⏳ EXP Date: " + expDate;
                            tvScannedResult.setText(result);
                            etProductName.setText(productName);
                            etMfgDate.setText(mfgDate);
                            etExpDate.setText(expDate);

                            checkExpiry(expDate);
                            break;
                        }
                    } else {
                        Log.e("CSV_ERROR", "Invalid CSV Format on line " + lineCount);
                    }
                }

                if (!found) {
                    Log.w("CSV_RESULT", "Product Not Found!");
                    tvScannedResult.setText("❌ Product Not Found!");
                }
            } catch (Exception e) {
                Log.e("CSV_ERROR", "Error reading CSV file", e);
                Toast.makeText(this, "Error reading CSV file", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("CSV_FETCH", "Failed to fetch CSV from Firebase", e);
            Toast.makeText(this, "Failed to fetch CSV from Firebase", Toast.LENGTH_SHORT).show();
        });
    }


    private void checkExpiry(String expDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date expiry = sdf.parse(expDate);
            Date today = new Date();
            if (expiry != null && expiry.before(today)) {
                ((Chip) findViewById(R.id.chipExpire)).setChecked(true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid expiry date format", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeScannedProduct() {
        int selectedChipId = chipGroup.getCheckedChipId();
        if (selectedChipId == -1) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        String status = "";
        if (selectedChipId == R.id.chipExpire) status = "Expired";
        else if (selectedChipId == R.id.chipRecyclable) status = "Recyclable";
        else if (selectedChipId == R.id.chipNonRecyclable) status = "Non-Recyclable";
        else if (selectedChipId == R.id.chipDisposal) status = "Disposal";

        HashMap<String, Object> productEntry = new HashMap<>();
        productEntry.put("batchNo", scannedBatchNo);
        productEntry.put("status", status);
        productEntry.put("timestamp", System.currentTimeMillis());
        productEntry.put("entryStatus", "Pending");

        database.push().setValue(productEntry)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Entry Submitted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Submission Failed!", Toast.LENGTH_SHORT).show());
    }
}