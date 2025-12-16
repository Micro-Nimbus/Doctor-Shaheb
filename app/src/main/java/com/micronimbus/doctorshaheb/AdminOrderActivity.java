package com.micronimbus.doctorshaheb;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminOrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdminOrderAdapter adapter;
    ArrayList<OrderModel> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        recyclerView = findViewById(R.id.adminOrderRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new AdminOrderAdapter(orderList);
        recyclerView.setAdapter(adapter);

        fetchAllOrders();
    }

    private void fetchAllOrders() {

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                orderList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    for (DataSnapshot orderSnap : userSnap.getChildren()) {

                        String orderId = orderSnap.getKey();
                        String userId = orderSnap.child("userId").getValue(String.class);
                        String userName = orderSnap.child("userName").getValue(String.class);
                        String address = orderSnap.child("address").getValue(String.class);
                        String orderTime = orderSnap.child("orderTime").getValue(String.class);
                        Integer totalPrice = orderSnap.child("totalPrice").getValue(Integer.class);

                        OrderModel order = new OrderModel(
                                orderId,
                                userId,
                                userName,
                                address,
                                orderTime,
                                totalPrice != null ? totalPrice : 0
                        );

                        // Fetch items safely
                        order.items = new ArrayList<>();
                        DataSnapshot itemsSnap = orderSnap.child("items");
                        if (itemsSnap.exists()) {
                            for (DataSnapshot itemNode : itemsSnap.getChildren()) {
                                Map<String, Object> itemMap = new HashMap<>();
                                for (DataSnapshot field : itemNode.getChildren()) {
                                    itemMap.put(field.getKey(), field.getValue());
                                }
                                order.items.add(itemMap);
                            }
                        }

                        orderList.add(order);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminOrderActivity.this,
                        "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
