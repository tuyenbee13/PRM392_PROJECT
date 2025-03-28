package com.example.electronics_store.retrofit;


import java.util.List;

public class OrderListResponse {
    private String message;
    private boolean success;
    private Integer errorCode;
    private List<OrderResponse> data;  // Danh sách đơn hàng trong "data"

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public List<OrderResponse> getData() {  // Phải có "getData()" để Retrofit parse JSON
        return data;
    }
}
