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

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private Context context;
    private List<MedicineModel> medicineList;
    private OnAddToCartListener listener;

    public MedicineAdapter(Context context, List<MedicineModel> medicineList, OnAddToCartListener listener) {
        this.context = context;
        this.medicineList = medicineList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        MedicineModel medicine = medicineList.get(position);
        holder.name.setText(medicine.getName());
        holder.price.setText("৳" + medicine.getPrice());
        holder.image.setImageResource(medicine.getImage());

        holder.btnAdd.setOnClickListener(v -> listener.onAddToCart(medicine));
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public void updateList(List<MedicineModel> filtered) {
        medicineList = filtered;
        notifyDataSetChanged();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;
        Button btnAdd;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.medName);
            price = itemView.findViewById(R.id.medPrice);
            image = itemView.findViewById(R.id.medImage);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
