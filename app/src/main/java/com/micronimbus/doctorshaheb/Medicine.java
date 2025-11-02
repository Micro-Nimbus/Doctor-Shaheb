package com.micronimbus.doctorshaheb;

public class Medicine {
    private String name, description, imageUrl, location;
    private double price;

    public Medicine() {}

    public Medicine(String name, String description, double price, String imageUrl, String location) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.location = location;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getLocation() { return location; }
}
