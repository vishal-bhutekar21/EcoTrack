package com.example.ecotrack.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.ecotrack.R;
import com.google.android.material.card.MaterialCardView;

public class Admin extends AppCompatActivity {

    private MaterialCardView userRoleManagement, wasteManagement, reportsAnalytics, complianceOversight,
            externalServices, costAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gridLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        userRoleManagement = findViewById(R.id.userRoleManagement);
        wasteManagement = findViewById(R.id.wasteManagement);
        reportsAnalytics = findViewById(R.id.reportsAnalytics);
        complianceOversight = findViewById(R.id.complianceOversight);
        externalServices = findViewById(R.id.externalServices);
        costAnalysis = findViewById(R.id.costAnalysis);

        // Set onClickListeners
        userRoleManagement.setOnClickListener(v -> {
            Toast.makeText(this, "User & Role Management Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin.this, UserManagementActivity.class));
        });

        wasteManagement.setOnClickListener(v -> {
            Toast.makeText(this, "Waste Management Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin.this, WasteManagementActivity.class));
        });

        reportsAnalytics.setOnClickListener(v -> {
            Toast.makeText(this, "Reports & Analytics Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin.this, ReportsActivity.class));
        });

        complianceOversight.setOnClickListener(v -> {
            Toast.makeText(this, "Compliance Oversight Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin.this, ComplianceOversightActivity.class));
        });

        externalServices.setOnClickListener(v -> {
            Toast.makeText(this, "External Services Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin.this, ExternalServicesActivity.class));
        });

        costAnalysis.setOnClickListener(v -> {
            Toast.makeText(this, "Cost Analysis Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Admin.this, CostAnalysisActivity.class));
        });
    }
}
