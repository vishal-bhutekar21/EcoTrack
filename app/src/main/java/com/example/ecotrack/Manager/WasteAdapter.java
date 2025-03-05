package com.example.ecotrack.Manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecotrack.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WasteAdapter extends RecyclerView.Adapter<WasteAdapter.ViewHolder> {
    private Context context;
    private List<WasteEntry> wasteList;
    private DatabaseReference approvedDatabaseRef;
    private DatabaseReference wasteDatabaseRef;

    public WasteAdapter(Context context, List<WasteEntry> wasteList) {
        this.context = context;
        this.wasteList = wasteList;
        approvedDatabaseRef = FirebaseDatabase.getInstance().getReference("ApprovedWasteEntries");
        wasteDatabaseRef = FirebaseDatabase.getInstance().getReference("WasteEntries"); // Original database reference
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waste, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WasteEntry wasteEntry = wasteList.get(position);
        holder.amount.setText("Amount: " + wasteEntry.getAmount());
        holder.date.setText("Date: " + wasteEntry.getDate());
        holder.recyclable.setText("Recyclable: " + wasteEntry.getRecyclable());
        holder.subType.setText("SubType: " + wasteEntry.getSubType());
        holder.wasteType.setText("WasteType: " + wasteEntry.getWasteType());

        // Load image using Glide
        Glide.with(context)
                .load(wasteEntry.getImageUrl())
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(holder.imageView);

        // Approve Button Click Event
        holder.approveButton.setOnClickListener(v -> {
            String id = approvedDatabaseRef.push().getKey();

            if (id != null) {
                approvedDatabaseRef.child(id).setValue(wasteEntry)
                        .addOnSuccessListener(aVoid -> {
                            DatabaseReference wasteDatabaseRef = FirebaseDatabase.getInstance().getReference("WasteEntries");
                            wasteDatabaseRef.child(wasteEntry.getKey()).removeValue()
                                    .addOnSuccessListener(aVoid1 -> {
                                        if (position < wasteList.size()) { // Prevent IndexOutOfBounds
                                            wasteList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, wasteList.size());
                                        }
                                        Toast.makeText(context, "Approved & Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to delete from WasteEntries!", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Approval Failed!", Toast.LENGTH_SHORT).show();
                        });
            }
        });



    }

    @Override
    public int getItemCount() {
        return wasteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount, date, recyclable, subType, wasteType;
        ImageView imageView;
        Button approveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount);
            date = itemView.findViewById(R.id.date);
            recyclable = itemView.findViewById(R.id.recyclable);
            subType = itemView.findViewById(R.id.subType);
            wasteType = itemView.findViewById(R.id.wasteType);
            imageView = itemView.findViewById(R.id.imageView);
            approveButton = itemView.findViewById(R.id.approveButton);
        }
    }
}
