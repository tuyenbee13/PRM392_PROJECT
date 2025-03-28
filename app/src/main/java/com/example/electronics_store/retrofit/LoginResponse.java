package com.example.electronics_store.retrofit;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("data")
    private LoginData data;

    public String getToken() {
        return data != null ? data.getToken() : null;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public LoginData getData() {
        return data;
    }

}
