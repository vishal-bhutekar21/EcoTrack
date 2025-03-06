package com.example.ecotrack.StaffWorkers;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecotrack.Admin.Product1;
import com.example.ecotrack.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class ProductAdapter1 extends RecyclerView.Adapter<ProductAdapter1.ViewHolder> {
    private Context context;
    private List<Product1> productList;
    private DatabaseReference databaseRef;

    public ProductAdapter1(Context context, List<Product1> productList) {
        this.context = context;
        this.productList = productList;
        this.databaseRef = FirebaseDatabase.getInstance().getReference("EcoTrack").child("YetToBeExpired");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product1 product = productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.batchNo.setText("Batch No: " + product.getBatchNo());
        holder.expiryDate.setText("Expiry: " + product.getExpiryDate().toString());
        holder.remainingDays.setText("Days Left: " + product.getRemainingDays());

        DatabaseReference productRef = FirebaseDatabase.getInstance()
                .getReference("EcoTrack")
                .child("YetToBeExpired")
                .child(product.getProductId());  // Fetch product by its ID

        productRef.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && "Dispatched".equals(snapshot.getValue(String.class))) {
                    holder.confirmDispatch.setBackgroundColor(Color.GREEN);
                    holder.confirmDispatch.setText("Dispatched");
                    holder.confirmDispatch.setEnabled(false);
                } else {
                    holder.confirmDispatch.setOnClickListener(v -> showConfirmDialog(holder, product));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmDialog(ViewHolder holder, Product1 product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Dispatch")
                .setMessage("Are you sure you want to mark this product as dispatched?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseReference productRef = FirebaseDatabase.getInstance()
                            .getReference("EcoTrack")
                            .child("YetToBeExpired")
                            .child(product.getProductId());

                    productRef.child("status").setValue("Dispatched")  // Mark as dispatched
                            .addOnSuccessListener(aVoid -> {
                                holder.confirmDispatch.setBackgroundColor(Color.GREEN);
                                holder.confirmDispatch.setText("Dispatched");
                                holder.confirmDispatch.setEnabled(false);
                                Toast.makeText(context, "Product Dispatched", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to dispatch product", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, batchNo, expiryDate, remainingDays;
        Button confirmDispatch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            batchNo = itemView.findViewById(R.id.batchNo);
            expiryDate = itemView.findViewById(R.id.expiryDate);
            remainingDays = itemView.findViewById(R.id.remainingDays);
            confirmDispatch = itemView.findViewById(R.id.confirmDispatch);
        }
    }
}
