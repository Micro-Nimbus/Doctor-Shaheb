package com.micronimbus.doctorshaheb;

import java.util.ArrayList;
import java.util.Map;

public class OrderModel {

    public String orderId;
    public String userId;
    public String userName;
    public String address;
    public String orderTime;
    public int totalPrice;

    // ADD THIS LINE
    public ArrayList<Map<String, Object>> items = new ArrayList<>();

    public OrderModel() {
        // Required empty constructor
    }

    public OrderModel(String orderId, String userId, String userName,
                      String address, String orderTime, int totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.address = address;
        this.orderTime = orderTime;
        this.totalPrice = totalPrice;
        this.items = new ArrayList<>();
    }
}
