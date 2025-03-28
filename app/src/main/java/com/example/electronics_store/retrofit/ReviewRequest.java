package com.example.electronics_store.retrofit;

public class ReviewRequest {
    private String comment;
    private String rating; // Giá trị: ONE, TWO, THREE, FOUR, FIVE
    private int productId;

    public ReviewRequest(String comment, String rating, int productId) {
        this.comment = comment;
        this.rating = rating;
        this.productId = productId;
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
}