package com.micronimbus.doctorshaheb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    private List<String> recentList;
    private OnRecentClickListener listener;

    public interface OnRecentClickListener {
        void onRecentClick(String text);
    }

    public RecentAdapter(List<String> recentList, OnRecentClickListener listener) {
        this.recentList = recentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        String text = recentList.get(position);
        holder.recentText.setText(text);
        holder.recentText.setOnClickListener(v -> listener.onRecentClick(text));
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    static class RecentViewHolder extends RecyclerView.ViewHolder {
        TextView recentText;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            recentText = itemView.findViewById(R.id.recentText);
        }
    }
}
