package com.sanshisoft.findfunny.model;

/**
 * Created by lei on 2014/9/16.
 */
public class Category {
    String lable;
    int image;

    public Category(){}

    public Category(String lable,int image){
        this.lable = lable;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }
}
