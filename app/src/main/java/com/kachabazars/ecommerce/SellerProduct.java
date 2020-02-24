package com.kachabazars.ecommerce;

import android.graphics.Bitmap;

public class SellerProduct {
    private String name, price, sellerName;
    private Bitmap image;

    public SellerProduct(String name, String price, Bitmap image, String sellerName) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.sellerName = sellerName;
    }

    public SellerProduct() {

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

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
}
