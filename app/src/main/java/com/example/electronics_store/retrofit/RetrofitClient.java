package com.example.electronics_store.retrofit;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://techstore.32mine.net:8080/";
    private static Retrofit retrofit = null;
    private static final AuthInterceptor authInterceptor = new AuthInterceptor(null);

    private static final int DEFAULT_CONNECT_TIMEOUT = 60;
    private static final int DEFAULT_READ_TIMEOUT = 60;
    private static final int DEFAULT_WRITE_TIMEOUT = 60;

    public static Retrofit getClient() {
        return getClient(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    public static Retrofit getClient(int connectTimeout, int readTimeout, int writeTimeout) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                    .addInterceptor(authInterceptor)
                    .retryOnConnectionFailure(true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void setAuthToken(String token) {
        authInterceptor.setAuthToken(token);
        resetClient();
        getClient();
    }

    public static void resetClient() {
        retrofit = null;
    }
}