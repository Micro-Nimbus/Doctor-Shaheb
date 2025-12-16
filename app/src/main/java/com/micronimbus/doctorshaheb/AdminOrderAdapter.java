package com.micronimbus.doctorshaheb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

    ArrayList<OrderModel> orderList;

    public AdminOrderAdapter(ArrayList<OrderModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderModel order = orderList.get(position);

        holder.userName.setText("User: " + order.userName);
        holder.address.setText("Address: " + order.address);
        holder.orderTime.setText("Time: " + order.orderTime);
        holder.totalPrice.setText("Total: ৳ " + order.totalPrice);

        // Remove any old item views
        holder.itemsContainer.removeAllViews();

        // Add items dynamically
        if (order.items != null) {
            for (Map<String, Object> item : order.items) {
                String name = item.get("name") != null ? item.get("name").toString() : "";
                String price = item.get("price") != null ? item.get("price").toString() : "0";
                String qty = item.get("quantity") != null ? item.get("quantity").toString() : "0";

                TextView tv = new TextView(holder.itemView.getContext());
                tv.setText(name + " - ৳" + price + " x " + qty);
                holder.itemsContainer.addView(tv);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, address, orderTime, totalPrice;
        LinearLayout itemsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.tvUserName);
            address = itemView.findViewById(R.id.tvAddress);
            orderTime = itemView.findViewById(R.id.tvOrderTime);
            totalPrice = itemView.findViewById(R.id.tvTotalPrice);
            itemsContainer = itemView.findViewById(R.id.itemsContainer);
        }
    }
}
