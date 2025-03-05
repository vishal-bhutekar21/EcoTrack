package com.example.ecotrack.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecotrack.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WasteManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private ChipGroup chipGroup;
    private static final String CSV_FILE_PATH = "dummy_products.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waste_management);

        recyclerView = findViewById(R.id.recyclerView);
        chipGroup = findViewById(R.id.chipGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        fetchCSVFromFirebase();

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipNoAction) {
                filterProducts("No Action Needed");
            } else if (checkedId == R.id.chipYetToExpire) {
                filterProducts("Yet to be Expired");
            } else if (checkedId == R.id.chipExpired) {
                filterProducts("Expired");
            }
        });
    }

    private void fetchCSVFromFirebase() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(CSV_FILE_PATH);
        storageRef.getBytes(1024 * 1024).addOnSuccessListener(bytes -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(bytes)));
                String line;
                reader.readLine(); // Skip header
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date today = new Date();

                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    String batchNo = values[0];
                    String productName = values[1];
                    String productType = values[2];
                    Date expiryDate = sdf.parse(values[4]);
                    int remainingDays = (int) ((expiryDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

                    productList.add(new Product(batchNo, productName, productType, expiryDate, remainingDays));
                }
                productAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("CSV_ERROR", "Error parsing CSV", e);
                Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("Firebase_ERROR", "Error fetching CSV", e);
            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
        });
    }

    private void filterProducts(String category) {
        List<Product> filteredList = new ArrayList<>();
        for (Product p : productList) {
            if ((category.equals("No Action Needed") && p.getRemainingDays() > 10) ||
                    (category.equals("Yet to be Expired") && p.getRemainingDays() <= 10 && p.getRemainingDays() > 0) ||
                    (category.equals("Expired") && p.getRemainingDays() <= 0)) {
                filteredList.add(p);
            }
        }
        productAdapter.updateList(filteredList);
    }
}