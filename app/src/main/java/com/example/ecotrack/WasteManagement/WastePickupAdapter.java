package com.example.ecotrack.WasteManagement;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecotrack.R;
import com.example.ecotrack.WasteManagement.WastePickupRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WastePickupAdapter extends RecyclerView.Adapter<WastePickupAdapter.ViewHolder> {

    private Context context;
    private List<WastePickupRequest> pickupList;
    private DatabaseReference databaseReference;

    public WastePickupAdapter(Context context, List<WastePickupRequest> pickupList) {
        this.context = context;
        this.pickupList = pickupList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("wasteDisposalTeamRequests");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waste_pickup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WastePickupRequest request = pickupList.get(position);

        holder.txtWasteType.setText("Type: " + request.getWasteType());
        holder.txtLocation.setText("Location: " + request.getLocation());
        holder.txtStatus.setText("Status: " + request.getStatus());
        holder.txtAmount.setText("Amount: " + request.getAmount() + " kg");
        holder.txtTimestamp.setText("Date: " + formatDate(request.getTimestamp()));

        holder.btnTakeAction.setOnClickListener(v -> showUpdateDialog(holder.itemView, request));
    }

    @Override
    public int getItemCount() {
        return pickupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtWasteType, txtLocation, txtStatus, txtAmount, txtTimestamp;
        Button btnTakeAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWasteType = itemView.findViewById(R.id.txtWasteType);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
            btnTakeAction = itemView.findViewById(R.id.btnTakeAction);
        }
    }

    private void showUpdateDialog(View view, WastePickupRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View dialogView = inflater.inflate(R.layout.dialog_pickup_action, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        RadioGroup statusRadioGroup = dialogView.findViewById(R.id.statusRadioGroup);
        EditText commentsEditText = dialogView.findViewById(R.id.commentsEditText);
        Button updateButton = dialogView.findViewById(R.id.updateButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        updateButton.setOnClickListener(v -> {
            int selectedId = statusRadioGroup.getCheckedRadioButtonId();
            String newStatus = "";

            if (selectedId == R.id.radioInProgress) {
                newStatus = "IN_PROGRESS";
            } else if (selectedId == R.id.radioCompleted) {
                newStatus = "COMPLETED";
            } else if (selectedId == R.id.radioCancelled) {
                newStatus = "CANCELLED";
            }

            String comments = commentsEditText.getText().toString().trim();

            if (!newStatus.isEmpty()) {
                updateStatus(request.getId(), newStatus, comments,request.getWasteType());
                dialog.dismiss();
            } else {
                Toast.makeText(view.getContext(), "Please select a status", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateStatus(String id, String status, String comments, String wasteType) {
        DatabaseReference requestRef = databaseReference.child(id);
        requestRef.child("status").setValue(status);

        if (!comments.isEmpty()) {
            requestRef.child("comments").setValue(comments);
        }

        if ("COMPLETED".equals(status)) {
            deleteMatchingApprovedWasteEntries(wasteType);
        }
    }

    private void deleteMatchingApprovedWasteEntries(String wasteType) {
        DatabaseReference approvedWasteRef = FirebaseDatabase.getInstance().getReference("ApprovedWasteEntries");

        approvedWasteRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String recyclable = snapshot.child("recyclable").getValue(String.class);

                    if (recyclable != null && recyclable.equals(wasteType)) {
                        snapshot.getRef().removeValue(); // Delete the entry
                    }
                }
            }
        });
    }


    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }
}
