package com.micronimbus.doctorshaheb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalPriceText;
    private Button checkoutBtn, confirmOrderBtn;
    private CartAdapter adapter;
    private List<MedicineModel> cartItems;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartRecycler);
        totalPriceText = findViewById(R.id.totalPrice);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        confirmOrderBtn = findViewById(R.id.confirmOrderBtn);

        // Firebase reference to "Order" node
        databaseReference = FirebaseDatabase.getInstance().getReference("Order");

        cartItems = CartManager.getCartItems();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CartAdapter(this, cartItems, this::updateTotalPrice);
        recyclerView.setAdapter(adapter);

        updateTotalPrice();

        checkoutBtn.setOnClickListener(v -> {
            CartManager.clearCart();
            adapter.notifyDataSetChanged();
            updateTotalPrice();
            Toast.makeText(this, "Cart Cleared!", Toast.LENGTH_SHORT).show();
        });

        confirmOrderBtn.setOnClickListener(v -> placeOrderInFirebase());
    }

    private void updateTotalPrice() {
        totalPriceText.setText("Total: ৳" + (int) CartManager.getTotalPrice());
    }

    private void placeOrderInFirebase() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = databaseReference.push().getKey();
        if (orderId == null) {
            Toast.makeText(this, "Failed to create order", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalPrice", CartManager.getTotalPrice());

        Map<String, Object> itemsMap = new HashMap<>();
        for (int i = 0; i < cartItems.size(); i++) {
            MedicineModel m = cartItems.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("name", m.getName());
            item.put("price", m.getPrice());
            item.put("quantity", m.getQuantity());
            itemsMap.put("item" + i, item);
        }
        orderMap.put("items", itemsMap);

        databaseReference.child(orderId).setValue(orderMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_SHORT).show();
                    CartManager.clearCart();
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
