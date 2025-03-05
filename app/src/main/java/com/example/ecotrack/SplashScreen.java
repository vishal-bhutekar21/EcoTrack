package com.example.ecotrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecotrack.Admin.AdminDashboard;
import com.example.ecotrack.Authentication.Login;
import com.example.ecotrack.Employee.EmployeeDashboard;
import com.example.ecotrack.Manager.ManagerDashboard;
import com.example.ecotrack.WasteManagement.WasteManagementDashboard;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        new Handler().postDelayed(() -> {
            // Check login status from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("EcoTrackPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            String userType = sharedPreferences.getString("userType", "");

            if (isLoggedIn) {
                // Redirect to the appropriate dashboard
                Intent intent;
                switch (userType) {
                    case "Admin":
                        intent = new Intent(SplashScreen.this, AdminDashboard.class);
                        break;
                    case "Manager":
                        intent = new Intent(SplashScreen.this, ManagerDashboard.class);
                        break;
                    case "Waste Management":
                        intent = new Intent(SplashScreen.this, WasteManagementDashboard.class);
                        break;
                    case "Staff Workers":
                        intent = new Intent(SplashScreen.this, EmployeeDashboard.class);
                        break;
                    default:
                        intent = new Intent(SplashScreen.this, Login.class);
                        break;
                }
                startActivity(intent);
            } else {
                // Redirect to Login screen if not logged in
                startActivity(new Intent(SplashScreen.this, Login.class));
            }
            finish();
        }, 1000);
    }
}
