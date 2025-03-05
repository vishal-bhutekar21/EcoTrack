package com.example.ecotrack.StaffWorkers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecotrack.R;

import java.util.List;

public class WasteAdapter extends RecyclerView.Adapter<WasteAdapter.ViewHolder> {

    private Context context;
    private List<WasteEntry> wasteList;

    public WasteAdapter(Context context, List<WasteEntry> wasteList) {
        this.context = context;
        this.wasteList = wasteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waste_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WasteEntry wasteEntry = wasteList.get(position);
        holder.textViewWasteType.setText("Waste Type: " + wasteEntry.getWasteType());
        holder.textViewSubType.setText("Sub Type: " + wasteEntry.getSubType());
        holder.textViewAmount.setText("Amount: " + wasteEntry.getAmount());
        holder.textViewRecyclable.setText("Recyclable: " + wasteEntry.getRecyclable());
        holder.textViewDate.setText("Date: " + wasteEntry.getDate());

        Glide.with(context)
                .load(wasteEntry.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageViewWaste);
    }

    @Override
    public int getItemCount() {
        return wasteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWasteType, textViewSubType, textViewAmount, textViewRecyclable, textViewDate;
        ImageView imageViewWaste;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWasteType = itemView.findViewById(R.id.text_waste_type);
            textViewSubType = itemView.findViewById(R.id.text_sub_type);
            textViewAmount = itemView.findViewById(R.id.text_amount);
            textViewRecyclable = itemView.findViewById(R.id.text_recyclable);
            textViewDate = itemView.findViewById(R.id.text_date);
            imageViewWaste = itemView.findViewById(R.id.image_waste);
        }
    }
}

