package com.micronimbus.doctorshaheb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<MedicineModel> cartItems;
    private Runnable onQuantityChanged;

    public CartAdapter(Context context, List<MedicineModel> cartItems, Runnable onQuantityChanged) {
        this.context = context;
        this.cartItems = cartItems;
        this.onQuantityChanged = onQuantityChanged;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        MedicineModel medicine = cartItems.get(position);
        holder.name.setText(medicine.getName());
        holder.price.setText("৳" + medicine.getPrice());
        holder.quantity.setText(String.valueOf(medicine.getQuantity()));
        holder.image.setImageResource(medicine.getImage());

        holder.btnPlus.setOnClickListener(v -> {
            medicine.setQuantity(medicine.getQuantity() + 1);
            holder.quantity.setText(String.valueOf(medicine.getQuantity()));
            onQuantityChanged.run();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (medicine.getQuantity() > 1) {
                medicine.setQuantity(medicine.getQuantity() - 1);
                holder.quantity.setText(String.valueOf(medicine.getQuantity()));
                onQuantityChanged.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, price, quantity;
        Button btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.medImage);
            name = itemView.findViewById(R.id.medName);
            price = itemView.findViewById(R.id.medPrice);
            quantity = itemView.findViewById(R.id.medQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
