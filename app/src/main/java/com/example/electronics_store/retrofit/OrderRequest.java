package com.example.electronics_store.retrofit;

import java.util.List;

public class OrderRequest {
    private List<OrderItem> items;
    private String paymentMethod; // Add payment method field

    public OrderRequest(List<OrderItem> items, String paymentMethod) {
        this.items = items;
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public static class OrderItem {
        private int productId;
        private int quantity;

        public OrderItem(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public int getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
