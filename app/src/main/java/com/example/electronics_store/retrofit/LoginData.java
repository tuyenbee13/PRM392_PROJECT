package com.example.electronics_store.retrofit;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }
}
