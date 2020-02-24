package com.kachabazars.ecommerce;

public class CartView {
    private String sl, name, price, stotal;

    public CartView(String sl, String name, String price, String stotal) {
        this.sl = sl;
        this.name = name;
        this.price = price;
        this.stotal = stotal;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
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

    public String getStotal() {
        return stotal;
    }

    public void setStotal(String stotal) {
        this.stotal = stotal;
    }
}
