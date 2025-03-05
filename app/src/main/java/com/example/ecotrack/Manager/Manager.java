package com.example.ecotrack.Manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecotrack.R;
import com.google.android.material.card.MaterialCardView;

public class Manager extends AppCompatActivity {

    private MaterialCardView wasteMonitoring, collectionScheduling, wasteAlerts,
            aiSuggestions, sustainabilityMetrics, recyclingReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Finding views by ID
        wasteMonitoring = findViewById(R.id.wasteMonitoring);
        collectionScheduling = findViewById(R.id.collectionScheduling);
        wasteAlerts = findViewById(R.id.wasteAlerts);
        aiSuggestions = findViewById(R.id.aiSuggestions);
        sustainabilityMetrics = findViewById(R.id.sustainabilityMetrics);
        recyclingReports = findViewById(R.id.recyclingReports);

        // Setting click listeners
        wasteMonitoring.setOnClickListener(view -> openActivity(WasteMonitoringActivity.class));
        collectionScheduling.setOnClickListener(view -> openActivity(CollectionSchedulingActivity.class));
        wasteAlerts.setOnClickListener(view -> openActivity(WasteAlertsActivity.class));
        aiSuggestions.setOnClickListener(view -> openActivity(AISuggestionsActivity.class));
        sustainabilityMetrics.setOnClickListener(view -> openActivity(SustainabilityMetricsActivity.class));
        recyclingReports.setOnClickListener(view -> openActivity(RecyclingReportsActivity.class));
    }

    // Method to open an activity
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(Manager.this, activityClass);
        startActivity(intent);
    }
}
