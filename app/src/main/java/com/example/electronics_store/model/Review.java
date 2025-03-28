package com.example.electronics_store.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    private int id;
    private String comment;
    private String rating;
    private int productId;
    private User user;
    private String createdAt;
    private String updatedAt;

    // Empty constructor for Gson
    public Review() {}

    public Review(String comment, String rating, int productId, User user, String createdAt) {
        this.comment = comment;
        this.rating = rating;
        this.productId = productId;
        this.user = user;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}