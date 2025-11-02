package com.micronimbus.doctorshaheb;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final List<MedicineModel> cartItems = new ArrayList<>();

    public static void addToCart(MedicineModel medicine) {
        // If medicine already in cart, increase quantity
        for (MedicineModel m : cartItems) {
            if (m.getName().equals(medicine.getName())) {
                m.setQuantity(m.getQuantity() + 1);
                return;
            }
        }
        // Else, add new medicine with quantity = 1
        medicine.setQuantity(1);
        cartItems.add(medicine);
    }

    public static List<MedicineModel> getCartItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
    }

    public static double getTotalPrice() {
        double total = 0;
        for (MedicineModel m : cartItems) {
            total += Double.parseDouble(m.getPrice()) * m.getQuantity();
        }
        return total;
    }
}
