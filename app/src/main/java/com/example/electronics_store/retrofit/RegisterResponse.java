package com.example.electronics_store.retrofit;

public class RegisterResponse {
    private String message;
    private boolean success;
    private int errorCode;
    private Object data;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }
}
