package com.example.ecotrack.Manager;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecotrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WasteRequest extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WasteAdapter adapter;
    private List<WasteEntry> wasteList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_request);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wasteList = new ArrayList<>();
        adapter = new WasteAdapter(this, wasteList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("WasteEntries");
        fetchWasteEntries();
    }

    private void fetchWasteEntries() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wasteList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    WasteEntry wasteEntry = dataSnapshot.getValue(WasteEntry.class);
                    wasteList.add(wasteEntry);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WasteRequest.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
