package com.example.ecotrack.Employee;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecotrack.Admin.Product1;
import com.example.ecotrack.R;
import com.example.ecotrack.StaffWorkers.ProductAdapter1;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PickupRequestsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter1 adapter;
    private List<Product1> productList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_requests);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        adapter = new ProductAdapter1(this, productList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("EcoTrack").child("YetToBeExpired");

        fetchProducts();
    }

    private void fetchProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product1 product = dataSnapshot.getValue(Product1.class);
                    if (product != null) {
                        product.setProductId(dataSnapshot.getKey()); // Ensure product ID is set
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PickupRequestsActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}