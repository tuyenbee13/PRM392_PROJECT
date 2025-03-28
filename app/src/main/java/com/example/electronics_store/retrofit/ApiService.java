package com.example.electronics_store.retrofit;

import com.example.electronics_store.adapter.OrderListResponse;
import com.example.electronics_store.model.Review;
import com.example.electronics_store.model.UserDetail;

import java.util.List;

import retrofit2.*;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @PATCH("auth/update-profile")
    Call<Void> updateProfile(@Body UpdateProfileRequest updateProfileRequest);

    @GET("admin/accounts/{id}")
    Call<UserDetail> getUserDetail(@Path("id") int userId);

    @GET("admin/accounts")
    Call<List<UserResponse>> getAllUsers();

    @GET("admin/products")
    Call<List<ProductResponse>> getProducts();

    @DELETE("admin/products/{id}")
    Call<Void> deleteProduct(@Path("id") int productId);

    @POST("admin/products")
    Call<Void> addProduct(@Body ProductRequest product);

    @GET("admin/categories")
    Call<List<CategoryResponse>> getCategories();

    @POST("admin/categories")
    Call<Void> addCategory(@Body CategoryRequest category);

    @PUT("admin/categories/{id}")
    Call<Void> updateCategory(@Path("id") int id, @Body CategoryRequest category);

    @DELETE("admin/categories/{id}")
    Call<Void> deleteCategory(@Path("id") int id);

    @GET("admin/products/{id}")
    Call<ProductRequest> getProductById(@Path("id") int id);

    @PUT("admin/products/{id}")
    Call<Void> updateProduct(@Path("id") int id, @Body ProductRequest productRequest);

    @PATCH("/admin/accounts/{id}")
    Call<Void> updateUser(@Path("id") int userId, @Body UserUpdateRequest userUpdateRequest);

    @GET("admin/orders")
    Call<List<OrderResponse>> getAllOrders();
    @GET("admin/orders/{id}")
    Call<OrderResponse> getOrderById(@Path("id") int id);

    @PUT("admin/accounts/{id}/ban")
    Call<Void> banUser(@Path("id") int userId);

    @PUT("admin/accounts/{id}/unban")
    Call<Void> unbanUser(@Path("id") int userId);

    @GET("api/products")
    Call<List<ProductResponse>> getAllProducts(
            @Query("sortBy") String sortBy,
            @Query("direction") String direction,
            @Query("categoryId") Integer categoryId,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("search") String search,
            @Query("minStock") Integer minStock,
            @Query("maxStock") Integer maxStock
    );
    @GET("api/products/{id}")
    Call<ProductResponse> getProductDetail(@Path("id") int id);

    // Changed to accept the ReviewRequest type
    @POST("/api/reviews")
    Call<Review> postReview(@Body ReviewRequest reviewRequest);

    @GET("/api/reviews/product/{productId}")
    Call<List<Review>> getReviewsByProductId(@Path("productId") int productId);

    @GET("/orders/me")
    Call<OrderListResponse> getUserOrders(@Header("Authorization") String token);

    @POST("orders")
    Call<OrderResponse> createOrder(@Body OrderRequest orderRequest);

    @PUT("api/account/change-password")
    Call<Void> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest request
    );
    @PATCH("/auth/update-profile")
    Call<UpdateProfileResponse> updateProfile(
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );


}
