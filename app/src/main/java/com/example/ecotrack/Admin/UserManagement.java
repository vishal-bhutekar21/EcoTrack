package com.example.ecotrack.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.ecotrack.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserManagement extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_management);

        databaseReference = FirebaseDatabase.getInstance().getReference("EcoTrack");

        CardView addStaffUser = findViewById(R.id.cardAddStaff);
        CardView addManager = findViewById(R.id.cardAddManager);
        CardView addWasteManagementUser = findViewById(R.id.cardAddWasteManagement);

        addStaffUser.setOnClickListener(v -> showAddUserDialog("StaffUsers"));
        addManager.setOnClickListener(v -> showAddUserDialog("ManagerUsers"));
        addWasteManagementUser.setOnClickListener(v -> showAddUserDialog("WasteManagement"));
    }

    private void showAddUserDialog(String userType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        TextInputEditText etMobile = dialogView.findViewById(R.id.etMobile);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etPassword = dialogView.findViewById(R.id.etPassword);
        MaterialButton btnAddUser = dialogView.findViewById(R.id.btnAddUser);

        AlertDialog dialog = builder.create();
        btnAddUser.setOnClickListener(v -> {
            String mobile = etMobile.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (mobile.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            storeUserData(userType, mobile, email, password);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void storeUserData(String userType, String mobile, String email, String password) {
        DatabaseReference userRef = databaseReference.child(userType).child(mobile);

        Map<String, Object> userData = new HashMap<>();
        userData.put("mobile", mobile);
        userData.put("email", email);
        userData.put("password", password);

        userRef.setValue(userData).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
        );
    }
}
