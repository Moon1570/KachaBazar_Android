package com.kachabazars.ecommerce;

import android.graphics.Bitmap;

public class Category {

    private String categoryName, categoryDesc;
    private Bitmap categoryImage;

    public Category(String categoryName, String categoryDesc, Bitmap categoryImage) {
        this.setCategoryName(categoryName);
        this.setCategoryDesc(categoryDesc);
        this.setCategoryImage(categoryImage);
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public Bitmap getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(Bitmap categoryImage) {
        this.categoryImage = categoryImage;
    }
}
