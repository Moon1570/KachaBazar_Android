package com.kachabazars.ecommerce;

import android.graphics.Bitmap;

public class Product {
    private String name, price, govtPrice;
    private Bitmap image;

    public Product(String name, String price, String govtPrice, Bitmap image) {
        this.setName(name);
        this.setGovtPrice(govtPrice);
        this.setPrice(price);
        this.setImage(image);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGovtPrice() {
        return govtPrice;
    }

    public void setGovtPrice(String description) {
        this.govtPrice = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
