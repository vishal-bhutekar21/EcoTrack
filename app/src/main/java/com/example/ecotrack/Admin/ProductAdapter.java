package com.example.ecotrack.Admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecotrack.R;
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
        holder.remainingDays.setText("Expires in: " + product.getRemainingDays() + " days");

        int imageRes = getProductImage(product.getProductType());
        holder.productImage.setImageResource(imageRes);
    }

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
        TextView productName, remainingDays;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.txtProductName);
            remainingDays = itemView.findViewById(R.id.txtRemainingDays);
            productImage = itemView.findViewById(R.id.imgProduct);
        }
    }
}
