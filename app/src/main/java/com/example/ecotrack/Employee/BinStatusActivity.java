package com.example.ecotrack.Employee;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ecotrack.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinStatusActivity extends AppCompatActivity {

    private PieChart pieChartRecyclable, pieChartNonRecyclable, pieChartDisposable;
    private DatabaseReference databaseReference;

    private ProgressBar progressBarRecyclable, progressBarNonRecyclable, progressBarDisposable;
    private TextView txtRecyclable, txtNonRecyclable, txtDisposable;

    private static final int MAX_CAPACITY = 100; // Set max bin capacity for percentage calculation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin_status);

        // Initialize Pie Charts
        pieChartRecyclable = findViewById(R.id.pieChartRecyclable);
        pieChartNonRecyclable = findViewById(R.id.pieChartNonRecyclable);
        pieChartDisposable = findViewById(R.id.pieChartDisposable);

        // Initialize Progress Bars
        progressBarRecyclable = findViewById(R.id.progressBarRecyclable);
        progressBarNonRecyclable = findViewById(R.id.progressBarNonRecyclable);
        progressBarDisposable = findViewById(R.id.progressBarDisaposable);

        // Initialize TextViews
        txtRecyclable = findViewById(R.id.binStatusRecyclable);
        txtNonRecyclable = findViewById(R.id.binStatusNonRecyclable);
        txtDisposable = findViewById(R.id.binStatusDisaposable);

        databaseReference = FirebaseDatabase.getInstance().getReference("ApprovedWasteEntries");

        fetchWasteData();
    }

    private void fetchWasteData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> recyclableMap = new HashMap<>();
                Map<String, Integer> nonRecyclableMap = new HashMap<>();
                Map<String, Integer> disposableMap = new HashMap<>();

                int totalRecyclable = 0, totalNonRecyclable = 0, totalDisposable = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    String recyclable = data.child("recyclable").getValue(String.class);
                    String subType = data.child("subType").getValue(String.class);
                    int amount = Integer.parseInt(data.child("amount").getValue(String.class));

                    if (recyclable == null || subType == null) continue;

                    switch (recyclable) {
                        case "Recyclable":
                            recyclableMap.put(subType, recyclableMap.getOrDefault(subType, 0) + amount);
                            totalRecyclable += amount;
                            checkWasteAmountAndNotify(totalRecyclable, "Recyclable");

                            break;
                        case "Non-Recyclable":
                            nonRecyclableMap.put(subType, nonRecyclableMap.getOrDefault(subType, 0) + amount);
                            totalNonRecyclable += amount;
                            checkWasteAmountAndNotify(totalNonRecyclable, "Non-Recyclable");

                            break;
                        case "Disposable":
                            disposableMap.put(subType, disposableMap.getOrDefault(subType, 0) + amount);
                            totalDisposable += amount;
                            checkWasteAmountAndNotify(totalDisposable, "Disposable");
                            break;
                    }
                }

                // Update Pie Charts
                displayPieChart(pieChartRecyclable, recyclableMap, "Recyclable Waste");
                displayPieChart(pieChartNonRecyclable, nonRecyclableMap, "Non-Recyclable Waste");
                displayPieChart(pieChartDisposable, disposableMap, "Disposable Waste");

                // Update Progress Bars and TextViews
                updateProgress(progressBarRecyclable, txtRecyclable, totalRecyclable);
                updateProgress(progressBarNonRecyclable, txtNonRecyclable, totalNonRecyclable);
                updateProgress(progressBarDisposable, txtDisposable, totalDisposable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to read waste data", error.toException());
            }
        });
    }

    private void displayPieChart(PieChart pieChart, Map<String, Integer> wasteData, String chartLabel) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wasteData.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, chartLabel);
        dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh the chart

        // Customize PieChart appearance
        Description description = new Description();
        description.setText(chartLabel);
        pieChart.setDescription(description);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
    }

    private void updateProgress(ProgressBar progressBar, TextView textView, int totalAmount) {
        // Ensure progress does not exceed 100% by scaling it to MAX_CAPACITY
        int progress = Math.min((totalAmount * 100) / MAX_CAPACITY, 100);

        progressBar.setProgress(progress);
        textView.setText(totalAmount + " kg (" + progress + "%)");
    }
    private void checkWasteAmountAndNotify(int totalAmount, String wasteType) {
        if (totalAmount > 100) {
            showWarningDialog(wasteType, totalAmount);
            sendNotificationToWasteManagement();
            storeWasteDisposalRequest(wasteType, totalAmount);
        }
    }
    private void showWarningDialog(String wasteType, int totalAmount) {
        new AlertDialog.Builder(this)
                .setTitle("Warning: Excess Waste")
                .setMessage("The total waste amount has exceeded 100 kg (" + totalAmount + " kg). A request has been sent to the waste management team.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void sendNotificationToWasteManagement() {
        // Simulating sending notification
        Log.d("Notification", "Notification sent to Waste Management Team.");
    }

    private void storeWasteDisposalRequest(String wasteType, int totalAmount) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("wasteDisposalTeamRequests");

        String requestId = ref.push().getKey();
        if (requestId == null) {
            Log.e("Firebase", "Generated requestId is null!");
            return;
        }

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("wasteType", wasteType);
        requestData.put("amount", totalAmount);
        requestData.put("timestamp", System.currentTimeMillis());

        ref.child(requestId).setValue(requestData)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Request stored successfully"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to store request", e));
    }







}
