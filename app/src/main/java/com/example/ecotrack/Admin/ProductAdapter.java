package com.example.ecotrack.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecotrack.R;
import com.google.android.material.chip.Chip;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getProductName());

        // Assigning status based on product type
        String status = getProductStatus(product.getProductType(), product.getRemainingDays());
        holder.remainingDays.setText(status);


        // Update chip color based on status
        updateChipAppearance(holder.chipStatus, status);

        // Set product image
        int imageRes = getProductImage(product.getProductType());
        holder.productImage.setImageResource(imageRes);
    }

    // Assign status dynamically based on product type
    private String getProductStatus(String type, int remainingDays) {
        switch (type) {
            case "Grocery":
            case "Medicine":
            case "Cosmetics":
                if (remainingDays == 0) return "Expired";
                else if (remainingDays <= 5) return "Expiring Soon";
                else return "Good Condition";

            case "Electronic":
            case "Cloths":
                return "No Expiry";

            default:
                return "Unknown";
        }
    }

    // Update chip color dynamically
    private void updateChipAppearance(Chip chip, String status) {
        switch (status) {
            case "Expired":
                chip.setChipBackgroundColorResource(R.color.binStatusFull);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.white));
                break;
            case "Expiring Soon":
                chip.setChipBackgroundColorResource(R.color.binStatusHalf);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.white));
                break;
            case "Good Condition":
                chip.setChipBackgroundColorResource(R.color.green);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.white));
                break;
            case "No Expiry":
                chip.setChipBackgroundColorResource(R.color.blue);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.white));
                break;
            default:
                chip.setChipBackgroundColorResource(R.color.gray);
                chip.setTextColor(chip.getContext().getResources().getColor(R.color.white));
                break;
        }
        chip.setText(status);
    }

    // Get corresponding product image
    private int getProductImage(String type) {
        switch (type) {
            case "Cloths": return R.drawable.brand;
            case "Cosmetics": return R.drawable.cosmetics;
            case "Electronic": return R.drawable.electronics;
            case "Grocery": return R.drawable.grocery;
            case "Medicine": return R.drawable.medicine;
            default: return R.drawable.electronics;
        }
    }

    @Override
    public int getItemCount() { return productList.size(); }

    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, remainingDays,remainingDaysori;
        ImageView productImage;
        Chip chipStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            remainingDaysori=itemView.findViewById(R.id.txtRemainingDaysori);
            productName = itemView.findViewById(R.id.txtProductName);
            remainingDays = itemView.findViewById(R.id.txtRemainingDays);
            productImage = itemView.findViewById(R.id.imgProduct);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
