package com.example.electronics_store.adapter;

import com.example.electronics_store.retrofit.OrderResponse;

import java.util.ArrayList;
import java.util.List;

public class OrderListResponse {
    private String message;
    private boolean success;
    private Integer errorCode;
    private List<OrderResponse> data;  // Đây là danh sách đơn hàng

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public List<OrderResponse> getData() {  // Đổi từ getOrders() thành getData()
        return data != null ? data : new ArrayList<>();  // Tránh trả về null
    }
}
