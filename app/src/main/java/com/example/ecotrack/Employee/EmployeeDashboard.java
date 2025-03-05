package com.example.ecotrack.Employee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecotrack.Authentication.Login;
import com.example.ecotrack.R;
import com.example.ecotrack.StaffWorkers.ManualWasteEnteries;
import com.example.ecotrack.StaffWorkers.StaffBarcodScan;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class EmployeeDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvEmail;
    private MaterialCardView taskAssignments, collectionTracking, routeOptimization,
            smartBinMonitoring, manualwasteentries, reportFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_dashboard);

        // Toolbar Setup
        ImageView toolbar = findViewById(R.id.menuIcon);
        toolbar.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Navigation Drawer Setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Toggle Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set User Email in Navigation Header
        View headerView = navigationView.getHeaderView(0);
        tvEmail = headerView.findViewById(R.id.tvEmail);

        SharedPreferences sharedPreferences = getSharedPreferences("EcoTrackPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "user@example.com");
        tvEmail.setText(userEmail);

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });

        // Initialize MaterialCardViews

        collectionTracking = findViewById(R.id.collectionTracking);
        routeOptimization = findViewById(R.id.routeOptimization);
        smartBinMonitoring = findViewById(R.id.smartBinMonitoring);
        manualwasteentries = findViewById(R.id.safetyChecklists);

        // Set Click Listeners
       // taskAssignments.setOnClickListener(view -> openActivity(TaskAssignmentsActivity.class));
        collectionTracking.setOnClickListener(view -> openActivity(StaffBarcodScan.class));
        routeOptimization.setOnClickListener(view -> openActivity(PickupRequestsActivity.class));
        smartBinMonitoring.setOnClickListener(view -> openActivity(BinStatusActivity.class));
        manualwasteentries.setOnClickListener(view -> openActivity(ManualWasteEnteries.class));
        //reportFeedback.setOnClickListener(view -> openActivity(WasteReportsActivity.class));
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(EmployeeDashboard.this, activityClass);
        startActivity(intent);
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
            startActivity(new Intent(EmployeeDashboard.this, Login.class));
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