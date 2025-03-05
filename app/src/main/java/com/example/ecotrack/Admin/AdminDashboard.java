package com.example.ecotrack.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.ecotrack.Authentication.Login;
import com.example.ecotrack.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class AdminDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvEmail;
    private ImageView menuIcon;  // Added ImageView variable

    private MaterialCardView userRoleManagement, wasteManagement, reportsAnalytics, complianceOversight,
            externalServices, costAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);



        // Initialize Drawer Layout and Navigation View
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize and setup the menu icon
        menuIcon = findViewById(R.id.menuIcon);

        // Set click listener for menu icon to open drawer
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set User Email in Header
        View headerView = navigationView.getHeaderView(0);
        tvEmail = headerView.findViewById(R.id.tvEmail);

        SharedPreferences sharedPreferences = getSharedPreferences("EcoTrackPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "user@example.com");
        tvEmail.setText(userEmail);

        // Initialize UI components
        userRoleManagement = findViewById(R.id.userRoleManagement);
        wasteManagement = findViewById(R.id.wasteManagement);
        reportsAnalytics = findViewById(R.id.reportsAnalytics);
        complianceOversight = findViewById(R.id.complianceOversight);
        externalServices = findViewById(R.id.externalServices);
        costAnalysis = findViewById(R.id.costAnalysis);

        // Set onClickListeners
        userRoleManagement.setOnClickListener(v -> navigateTo(UserManagement.class, "User & Role Management Clicked"));
        wasteManagement.setOnClickListener(v -> navigateTo(WasteManagementActivity.class, "Waste Management Clicked"));
        reportsAnalytics.setOnClickListener(v -> navigateTo(ReportsActivity.class, "Reports & Analytics Clicked"));
        complianceOversight.setOnClickListener(v -> navigateTo(ComplianceOversightActivity.class, "Compliance Oversight Clicked"));
        externalServices.setOnClickListener(v -> navigateTo(ExternalServicesActivity.class, "External Services Clicked"));
        costAnalysis.setOnClickListener(v -> navigateTo(CostAnalysisActivity.class, "Cost Analysis Clicked"));
    }

    private void navigateTo(Class<?> activity, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AdminDashboard.this, activity));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle Home Click
        } else if (id == R.id.nav_share) {
            // Share App
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out EcoTrack: [App Link]");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else if (id == R.id.nav_feedback) {
            // Open Email for Feedback
            Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO);
            feedbackIntent.setData(Uri.parse("mailto:feedback@ecotrack.com"));
            feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for EcoTrack");
            startActivity(feedbackIntent);
        } else if (id == R.id.nav_logout) {
            // Clear Shared Preferences and Logout
            SharedPreferences sharedPreferences = getSharedPreferences("EcoTrackPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Redirect to Login
            startActivity(new Intent(AdminDashboard.this, Login.class));
            finish();
        }

        // Close Drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}