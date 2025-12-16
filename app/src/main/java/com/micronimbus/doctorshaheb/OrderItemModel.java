package com.micronimbus.doctorshaheb;

public class OrderItemModel {
    public String name = "";
    public String price = "";
    public int quantity = 0;

    public OrderItemModel() {}

    public OrderItemModel(String name, String price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
