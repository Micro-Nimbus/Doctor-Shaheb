package com.micronimbus.doctorshaheb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalPriceText;
    private Button checkoutBtn, confirmOrderBtn;
    private CartAdapter adapter;
    private List<MedicineModel> cartItems;

    private DatabaseReference rootRef, userRef, orderRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartRecycler);
        totalPriceText = findViewById(R.id.totalPrice);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        confirmOrderBtn = findViewById(R.id.confirmOrderBtn);

        auth = FirebaseAuth.getInstance();

        // Firebase references
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
        orderRef = rootRef.child("Order"); // beside Blood, Feedbacks, Users

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

        confirmOrderBtn.setOnClickListener(v -> showAddressDialog());
    }

    private void updateTotalPrice() {
        totalPriceText.setText("Total: ৳" + (int) CartManager.getTotalPrice());
    }

    // Show popup dialog to enter address
    private void showAddressDialog() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to confirm your order!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Enter Delivery Address");

        final EditText input = new EditText(this);
        input.setHint("Enter full address");
        input.setSingleLine(false);
        input.setLines(3);  // multiline
        input.setMaxLines(5);
        input.setVerticalScrollBarEnabled(true);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String address = input.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadOrder(address);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Upload order to Firebase under user's UID
    private void uploadOrder(String address) {
        String currentUserId = auth.getCurrentUser().getUid();

        userRef.child(currentUserId).child("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String userName = task.getResult().getValue(String.class);

                // Create order ID under user's UID
                String orderId = orderRef.child(currentUserId).push().getKey();
                if (orderId == null) {
                    Toast.makeText(this, "Failed to create order ID!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("userId", currentUserId);
                orderMap.put("userName", userName);
                orderMap.put("totalPrice", CartManager.getTotalPrice());
                orderMap.put("orderTime", currentTime);
                orderMap.put("address", address); // add address

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

                orderRef.child(currentUserId).child(orderId).setValue(orderMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "✅ Order Placed Successfully!", Toast.LENGTH_SHORT).show();
                            CartManager.clearCart();
                            adapter.notifyDataSetChanged();
                            updateTotalPrice();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "❌ Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(this, "Failed to fetch user name!", Toast.LENGTH_SHORT).show();
                //utsho

            }
        });
    }
}
