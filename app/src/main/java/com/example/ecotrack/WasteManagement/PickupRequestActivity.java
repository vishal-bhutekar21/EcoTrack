package com.example.ecotrack.WasteManagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecotrack.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PickupRequestActivity extends AppCompatActivity {

    // UI Components
    private ImageView backButton;
    private Spinner storeLocationSpinner;
    private RadioGroup wasteTypeRadioGroup;
    private EditText weightEditText;
    private TextView pickupDateText;
    private TextView pickupTimeText;
    private EditText notesEditText;
    private MaterialButton submitButton;
    private ImageView calendarIcon;
    private ImageView clockIcon;

    // Firebase
    private DatabaseReference mDatabase;

    // Data
    private Calendar selectedDate = Calendar.getInstance();
    private String selectedWasteType = "";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pickup_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        initViews();
        setupListeners();
        setupSpinner();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        storeLocationSpinner = findViewById(R.id.storeLocationSpinner);
        wasteTypeRadioGroup = findViewById(R.id.wasteTypeRadioGroup);
        weightEditText = findViewById(R.id.weightEditText);
        pickupDateText = findViewById(R.id.pickupDateText);
        pickupTimeText = findViewById(R.id.pickupTimeText);
        notesEditText = findViewById(R.id.notesEditText);
        submitButton = findViewById(R.id.submitButton);
        calendarIcon = findViewById(R.id.calendarIcon);
        clockIcon = findViewById(R.id.clockIcon);
    }

    private void setupListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Waste type radio group listener
        wasteTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            if (radioButton != null) {
                selectedWasteType = radioButton.getText().toString();
            }
        });

        // Date picker listener
        View.OnClickListener datePickerListener = v -> showDatePicker();
        pickupDateText.setOnClickListener(datePickerListener);
        calendarIcon.setOnClickListener(datePickerListener);

        // Time picker listener
        View.OnClickListener timePickerListener = v -> showTimePicker();
        pickupTimeText.setOnClickListener(timePickerListener);
        clockIcon.setOnClickListener(timePickerListener);

        // Submit button listener
        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupSpinner() {
        // Sample store locations - replace with your actual data source
        List<String> locations = new ArrayList<>();
        locations.add("Select a location");
        locations.add("Main Street Store");
        locations.add("Downtown Branch");
        locations.add("Westside Location");
        locations.add("North Plaza");
        locations.add("Eastside Mall");

        // Create adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                locations
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeLocationSpinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar currentDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    pickupDateText.setText(dateFormatter.format(selectedDate.getTime()));
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
        );

        // Set min date to today
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());

        // Set max date to 2 weeks from now
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 14);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar currentTime = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minute);

                    pickupTimeText.setText(timeFormatter.format(selectedDate.getTime()));
                },
                currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                false
        );

        timePickerDialog.show();
    }

    private void validateAndSubmit() {
        // Validate form fields
        String location = storeLocationSpinner.getSelectedItem().toString();
        if (location.equals("Select a location")) {
            showError("Please select a store location");
            return;
        }

        if (selectedWasteType.isEmpty()) {
            showError("Please select a waste type");
            return;
        }

        String weightStr = weightEditText.getText().toString().trim();
        if (weightStr.isEmpty()) {
            showError("Please enter the estimated weight");
            return;
        }

        float weight;
        try {
            weight = Float.parseFloat(weightStr);
            if (weight <= 0) {
                showError("Weight must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid weight");
            return;
        }

        if (pickupDateText.getText().toString().equals("Select date")) {
            showError("Please select a pickup date");
            return;
        }

        if (pickupTimeText.getText().toString().equals("Select time")) {
            showError("Please select a pickup time");
            return;
        }

        // All validations passed, save to Firebase
        savePickupRequestToFirebase(location, selectedWasteType, weight);
    }

    private void showError(String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void savePickupRequestToFirebase(String location, String wasteType, float weight) {
        // Show loading state
        submitButton.setEnabled(false);
        submitButton.setText("Submitting...");

        // Create pickup request object
        String requestId = UUID.randomUUID().toString();

        // You can get the user ID from your custom authentication system
        // For now, using a placeholder or you can pass this from the previous activity via Intent
        String userId = "user123"; // Replace with your actual user ID retrieval method
        String notes = notesEditText.getText().toString().trim();

        Map<String, Object> pickupRequest = new HashMap<>();
        pickupRequest.put("requestId", requestId);
        pickupRequest.put("userId", userId);
        pickupRequest.put("location", location);
        pickupRequest.put("wasteType", wasteType);
        pickupRequest.put("weight", weight);
        pickupRequest.put("pickupDate", dateFormatter.format(selectedDate.getTime()));
        pickupRequest.put("pickupTime", timeFormatter.format(selectedDate.getTime()));
        pickupRequest.put("notes", notes);
        pickupRequest.put("status", "Pending");
        pickupRequest.put("createdAt", System.currentTimeMillis());

        // Save to Firebase
        mDatabase.child("pickupRequests").child(requestId).setValue(pickupRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PickupRequestActivity.this, "Pickup request submitted successfully", Toast.LENGTH_LONG).show();
                    finish(); // Close activity and go back
                })
                .addOnFailureListener(e -> {
                    // Enable the submit button again
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit Request");

                    // Show error message
                    showError("Failed to submit request: " + e.getMessage());
                });
    }
}