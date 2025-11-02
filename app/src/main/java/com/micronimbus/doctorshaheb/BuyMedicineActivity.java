package com.micronimbus.doctorshaheb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class BuyMedicineActivity extends AppCompatActivity {

    private RecyclerView recyclerView, recentRecycler;
    private EditText searchMedicine;
    private Button btnSearch, btnCart;
    private MedicineAdapter adapter;
    private RecentAdapter recentAdapter;
    private List<MedicineModel> medicineList;
    private List<String> recentSearches;
    private TextView cartCountText;
    private int cartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine);

        recyclerView = findViewById(R.id.medicineRecycler);
        searchMedicine = findViewById(R.id.searchMedicine);
        btnSearch = findViewById(R.id.btnSearch);
        btnCart = findViewById(R.id.btnCart);
        recentRecycler = findViewById(R.id.recentRecycler);
        cartCountText = findViewById(R.id.cartCount);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recentRecycler.setLayoutManager(new GridLayoutManager(this, 1));

        medicineList = new ArrayList<>();
        recentSearches = new ArrayList<>();

        // Hardcoded medicines
        medicineList.add(new MedicineModel("Napa Tablet", "25", R.drawable.medicine1));
        medicineList.add(new MedicineModel("Fexo Tablet", "40", R.drawable.medicine2));
        medicineList.add(new MedicineModel("Monas", "60", R.drawable.medicine3));
        medicineList.add(new MedicineModel("Ace", "30", R.drawable.medicine4));
        medicineList.add(new MedicineModel("Histacin", "20", R.drawable.medicine5));

        adapter = new MedicineAdapter(this, medicineList, medicine -> {
            CartManager.addToCart(medicine);
            updateCartCount();
            Toast.makeText(this, medicine.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        recentAdapter = new RecentAdapter(recentSearches, text -> {
            searchMedicine.setText(text);
            filterMedicines(text);
        });
        recentRecycler.setAdapter(recentAdapter);

        btnSearch.setOnClickListener(v -> {
            String query = searchMedicine.getText().toString().trim();
            if (!query.isEmpty() && !recentSearches.contains(query)) {
                recentSearches.add(0, query);
                recentAdapter.notifyDataSetChanged();
            }
            filterMedicines(query);
        });

        searchMedicine.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMedicines(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnCart.setOnClickListener(v -> {
            if (CartManager.getCartItems().isEmpty()) {
                Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(BuyMedicineActivity.this, CartActivity.class));
            }
        });
    }

    private void updateCartCount() {
        cartCount = CartManager.getCartItems().size();
        cartCountText.setText(String.valueOf(cartCount));
    }

    private void filterMedicines(String query) {
        List<MedicineModel> filtered = new ArrayList<>();
        for (MedicineModel m : medicineList) {
            if (m.getName().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(m);
            }
        }
        adapter.updateList(filtered);
    }
}
