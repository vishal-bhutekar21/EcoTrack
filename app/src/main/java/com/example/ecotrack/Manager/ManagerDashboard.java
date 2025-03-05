package com.example.ecotrack.Manager;

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
import com.example.ecotrack.Authentication.Login;
import com.example.ecotrack.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class ManagerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvEmail;
    ImageView menuIcon;

    private MaterialCardView wasteMonitoring, collectionScheduling, wasteAlerts,
            aiSuggestions, sustainabilityMetrics, recyclingReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_dashboard);

        // Toolbar
        menuIcon = findViewById(R.id.menuIcon);

        // Set click listener for menu icon to open drawer
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        // Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Toggle for Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set User Email in Header
        View headerView = navigationView.getHeaderView(0);
        tvEmail = headerView.findViewById(R.id.tvEmail);

        SharedPreferences sharedPreferences = getSharedPreferences("EcoTrackPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "user@example.com");
        tvEmail.setText(userEmail);

        // Initialize UI components
        wasteMonitoring = findViewById(R.id.wasteMonitoring);
        collectionScheduling = findViewById(R.id.collectionScheduling);
        wasteAlerts = findViewById(R.id.wasteAlerts);
        aiSuggestions = findViewById(R.id.aiSuggestions);
        sustainabilityMetrics = findViewById(R.id.sustainabilityMetrics);
        recyclingReports = findViewById(R.id.recyclingReports);

        // Set click listeners
        wasteMonitoring.setOnClickListener(view -> openActivity(WasteRequest.class));
        collectionScheduling.setOnClickListener(view -> openActivity(CollectionSchedulingActivity.class));
        wasteAlerts.setOnClickListener(view -> openActivity(WasteAlertsActivity.class));
        aiSuggestions.setOnClickListener(view -> openActivity(AISuggestionsActivity.class));
        sustainabilityMetrics.setOnClickListener(view -> openActivity(SustainabilityMetricsActivity.class));
        recyclingReports.setOnClickListener(view -> openActivity(RecyclingReportsActivity.class));
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(ManagerDashboard.this, activityClass);
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
            startActivity(new Intent(ManagerDashboard.this, Login.class));
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
