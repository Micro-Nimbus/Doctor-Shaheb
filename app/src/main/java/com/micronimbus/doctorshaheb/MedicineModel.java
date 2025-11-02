package com.micronimbus.doctorshaheb;

public class MedicineModel {
    private String name;
    private String price;
    private int image;
    private int quantity; // Added quantity

    public MedicineModel(String name, String price, int image) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.quantity = 0;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImage() { return image; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
