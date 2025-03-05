package com.example.ecotrack.Employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecotrack.R;
import com.google.android.material.card.MaterialCardView;

public class EmployeeDashboard extends AppCompatActivity {

    // Declare MaterialCardViews
    MaterialCardView taskAssignments, collectionTracking, routeOptimization, smartBinMonitoring, safetyChecklists, reportFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });

        // Initialize MaterialCardViews with correct IDs
        taskAssignments = findViewById(R.id.taskAssignments);
        collectionTracking = findViewById(R.id.collectionTracking);
        routeOptimization = findViewById(R.id.routeOptimization);
        smartBinMonitoring = findViewById(R.id.smartBinMonitoring);
        safetyChecklists = findViewById(R.id.safetyChecklists);
        reportFeedback = findViewById(R.id.reportFeedback);

        // Set Click Listeners
        taskAssignments.setOnClickListener(view -> openActivity(TaskAssignmentsActivity.class));
        collectionTracking.setOnClickListener(view -> openActivity(CollectionTrackingActivity.class));
        routeOptimization.setOnClickListener(view -> openActivity(PickupRequestsActivity.class));
        smartBinMonitoring.setOnClickListener(view -> openActivity(BinStatusActivity.class));
        safetyChecklists.setOnClickListener(view -> openActivity(SafetyGuideActivity.class));
        reportFeedback.setOnClickListener(view -> openActivity(WasteReportsActivity.class));
    }

    // Helper method to start an activity
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(EmployeeDashboard.this, activityClass);
        startActivity(intent);
    }
}
