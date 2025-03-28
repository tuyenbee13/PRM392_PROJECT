package com.example.electronics_store.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private String authToken;

    public AuthInterceptor(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        Log.d("AuthInterceptor", "Token được cập nhật: " + authToken);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (authToken != null && !authToken.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();
            return chain.proceed(newRequest);
        } else {
            Log.d("AuthInterceptor", "Không có token!");
        }
        return chain.proceed(originalRequest);
    }
}

