package com.example.ecotrack.StaffWorkers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecotrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualWasteEnteries extends AppCompatActivity {

    private Uri imageUri;
    private ImageView imageView;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private RecyclerView recyclerView;
    private WasteAdapter adapter;
    private List<WasteEntry> wasteList;
    private DatabaseReference databaseReference2;
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    if (imageView != null) {
                        imageView.setImageURI(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_waste_enteries);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Handle back button click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Go back when the button is clicked
            }
        });

        // Initialize Firebase Database and Storage
        databaseReference = FirebaseDatabase.getInstance().getReference("WasteEntries");
        storageReference = FirebaseStorage.getInstance().getReference("WasteImages");

        findViewById(R.id.fab_add_waste).setOnClickListener(v -> showAddWasteDialog());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wasteList = new ArrayList<>();
        adapter = new WasteAdapter(this, wasteList);
        recyclerView.setAdapter(adapter);

        databaseReference2 = FirebaseDatabase.getInstance().getReference("WasteEntries");

        fetchWasteEntries();
    }

    private void showAddWasteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_waste, null);
        builder.setView(dialogView);

        Spinner spinnerWasteType = dialogView.findViewById(R.id.spinner_waste_type);
        Spinner spinnerSubType = dialogView.findViewById(R.id.spinner_sub_type);
        imageView = dialogView.findViewById(R.id.image_view);
        EditText editTextAmount = dialogView.findViewById(R.id.edit_text_amount);
        Spinner spinnerRecyclable = dialogView.findViewById(R.id.spinner_recyclable);
        EditText editTextDate = dialogView.findViewById(R.id.edit_text_date);
        Button buttonUploadImage = dialogView.findViewById(R.id.button_upload_image);
        Button buttonSubmit = dialogView.findViewById(R.id.button_submit);

        // Set up spinners
        ArrayAdapter<CharSequence> wasteTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.waste_types, android.R.layout.simple_spinner_item);
        wasteTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWasteType.setAdapter(wasteTypeAdapter);

        ArrayAdapter<CharSequence> subTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.sub_types, android.R.layout.simple_spinner_item);
        subTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubType.setAdapter(subTypeAdapter);

        ArrayAdapter<CharSequence> recyclableAdapter = ArrayAdapter.createFromResource(this,
                R.array.recyclable_types, android.R.layout.simple_spinner_item);
        recyclableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRecyclable.setAdapter(recyclableAdapter);

        // Set up image upload
        buttonUploadImage.setOnClickListener(v -> openGallery());

        // Set up date picker
        editTextDate.setOnClickListener(v -> showDatePickerDialog(editTextDate));

        AlertDialog dialog = builder.create();
        dialog.show();

        // Submit button logic
        buttonSubmit.setOnClickListener(v -> {
            String wasteType = spinnerWasteType.getSelectedItem().toString();
            String subType = spinnerSubType.getSelectedItem().toString();
            String amount = editTextAmount.getText().toString();
            String recyclable = spinnerRecyclable.getSelectedItem().toString();
            String date = editTextDate.getText().toString();

            if (amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                uploadImageAndSaveData(wasteType, subType, amount, recyclable, date);
            } else {
                saveDataToFirebase(wasteType, subType, amount, recyclable, date, "");
            }
            dialog.dismiss();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void showDatePickerDialog(EditText editTextDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editTextDate.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void uploadImageAndSaveData(String wasteType, String subType, String amount, String recyclable, String date) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            saveDataToFirebase(wasteType, subType, amount, recyclable, date, uri.toString());
        })).addOnFailureListener(e -> Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void fetchWasteEntries() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wasteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    WasteEntry waste = dataSnapshot.getValue(WasteEntry.class);
                    if (waste != null) {
                        wasteList.add(waste);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManualWasteEnteries.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void saveDataToFirebase(String wasteType, String subType, String amount, String recyclable, String date, String imageUrl) {
        String entryId = databaseReference.push().getKey();
        Map<String, Object> wasteData = new HashMap<>();
        wasteData.put("wasteType", wasteType);
        wasteData.put("subType", subType);
        wasteData.put("amount", amount);
        wasteData.put("recyclable", recyclable);
        wasteData.put("date", date);
        wasteData.put("imageUrl", imageUrl);

        if (entryId != null) {
            databaseReference.child(entryId).setValue(wasteData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Waste entry saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
