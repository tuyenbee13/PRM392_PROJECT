package com.example.electronics_store.retrofit;

import com.example.electronics_store.model.User;

public class ReviewResponse {
    private int id;
    private User user;
    private String rating;
    private String comment;
    private String createdAt;

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
