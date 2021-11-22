package com.example.restaurante.model;

public class FoodModel {

    private String name;
    private String description;
    private String price;
    private String hasGluten;
    private String calories;
    private String imgUrl;
    private byte[] pictureBlob;

    public FoodModel() {

    }

    public FoodModel(String name, String description, String price, String hasGluten, String calories, String imgUrl, byte[] pictureBlob) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.hasGluten = hasGluten;
        this.calories = calories;
        this.imgUrl = imgUrl;
        this.pictureBlob = pictureBlob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHasGluten() {
        return hasGluten;
    }

    public void setHasGluten(String hasGluten) {
        this.hasGluten = hasGluten;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public byte[] getPictureBlob() {
        return pictureBlob;
    }

    public void setPictureBlob(byte[] pictureBlob) {
        this.pictureBlob = pictureBlob;
    }
}
