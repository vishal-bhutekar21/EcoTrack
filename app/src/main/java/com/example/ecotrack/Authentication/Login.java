package com.example.ecotrack.Authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecotrack.Admin.AdminDashboard;
import com.example.ecotrack.Employee.EmployeeDashboard;
import com.example.ecotrack.Manager.ManagerDashboard;
import com.example.ecotrack.WasteManagement.WasteManagementDashboard;
import com.example.ecotrack.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private Spinner spinnerUserType;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnAdminSignup;
    private DatabaseReference databaseReference;
    private String selectedUserType = "Admin";  // Default Selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinnerUserType = findViewById(R.id.spinnerUserType);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnAdminSignup = findViewById(R.id.btnAdminSignup);

        // Spinner selection listener
        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedUserType = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedUserType = "Admin";
            }
        });

        // Login Button Click
        btnLogin.setOnClickListener(v -> loginUser());

        // Admin Signup Intent
        btnAdminSignup.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, AdminSignup.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get Firebase path based on user type
        String userPath = "";
        switch (selectedUserType) {
            case "Admin":
                userPath = "EcoTrack/AdminUsers";
                break;
            case "Manager":
                userPath = "EcoTrack/ManagerUsers";
                break;
            case "Waste Management":
                userPath = "EcoTrack/WasteManagement";
                break;
            case "Staff Workers":
                userPath = "EcoTrack/StaffUsers";
                break;
        }

        // Fetch user details from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference(userPath);
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                boolean userFound = false;
                for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    if (dbEmail != null && dbEmail.equals(email)) {
                        userFound = true;
                        if (dbPassword.equals(password)) {
                            // Save Login State
                            SharedPreferences sharedPreferences = getSharedPreferences("EcoTrackPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("userType", selectedUserType);
                            editor.apply();

                            // Redirect User to Dashboard
                            switch (selectedUserType) {
                                case "Admin":
                                    startActivity(new Intent(Login.this, AdminDashboard.class));
                                    break;
                                case "Manager":
                                    startActivity(new Intent(Login.this, ManagerDashboard.class));
                                    break;
                                case "Waste Management":
                                    startActivity(new Intent(Login.this, WasteManagementDashboard.class));
                                    break;
                                case "Staff Workers":
                                    startActivity(new Intent(Login.this, EmployeeDashboard.class));
                                    break;
                            }
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                if (!userFound) {
                    Toast.makeText(Login.this, "User Not Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, "Error connecting to database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
