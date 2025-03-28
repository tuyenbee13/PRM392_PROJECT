package com.example.electronics_store.model;

import com.example.electronics_store.retrofit.ProductResponse;

public class OrderDetail {
    private ProductResponse product;
    private int quantity;
    private double price;

    public ProductResponse getProduct() {
        return product;
    }
    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}