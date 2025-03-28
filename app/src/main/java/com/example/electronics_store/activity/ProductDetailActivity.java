package com.example.electronics_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.electronics_store.R;
import com.example.electronics_store.adapter.ReviewAdapter;
import com.example.electronics_store.model.Review;
import com.example.electronics_store.model.User;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.CartUtils;
import com.example.electronics_store.retrofit.ProductResponse;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.example.electronics_store.retrofit.ReviewRequest;
import com.example.electronics_store.retrofit.CartUtils;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextView productName, productPrice, productDescription;
    private Button btnAddToCart, btnSubmitReview;
    private EditText edtReview;
    private RatingBar ratingBar;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        setupRecyclerView();
        loadProductData();
    }

    private void initViews() {
        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        edtReview = findViewById(R.id.edtReview);
        ratingBar = findViewById(R.id.ratingBar);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
    }

    private void setupRecyclerView() {
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadProductData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product")) {
            ProductResponse product = intent.getParcelableExtra("product");
            if (product != null) {
                displayProductInfo(product);
                setupButtons(product);
                productId = product.getId();
                loadReviews();
            }
        }
    }

    private void displayProductInfo(ProductResponse product) {
        productName.setText(product.getName() != null ? product.getName() : "Không có tên");
        productPrice.setText(formatPrice(product.getPrice()) + " VND");
        productDescription.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả");

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this).load(product.getImageUrl()).into(productImage);
        } else {
            productImage.setImageResource(R.drawable.bug_report);
        }
        // Kiểm tra tồn kho và cập nhật giao diện
        if (product.getStock() == 0) {
            btnAddToCart.setEnabled(false);
            btnAddToCart.setText("Hết hàng");
        } else {
            btnAddToCart.setEnabled(true);
            btnAddToCart.setText("Thêm vào giỏ hàng");
        }
    }

    private void setupButtons(ProductResponse product) {
        btnAddToCart.setOnClickListener(v -> {
            addToCart(product);
            Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        });

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void loadReviews() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getReviewsByProductId(productId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reviewAdapter = new ReviewAdapter(response.body());
                    recyclerViewReviews.setAdapter(reviewAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi khi tải đánh giá: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReview() {
        String comment = edtReview.getText().toString().trim();
        float ratingValue = ratingBar.getRating();

        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung đánh giá!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ratingValue == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá!", Toast.LENGTH_SHORT).show();
            return;
        }

        String ratingString = convertRatingToString(ratingValue);

        // Use ReviewRequest class instead of directly using Review
        ReviewRequest reviewRequest = new ReviewRequest(comment, ratingString, productId);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.postReview(reviewRequest).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailActivity.this, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    edtReview.setText("");
                    ratingBar.setRating(0);
                    loadReviews(); // Reload reviews after submitting
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi khi gửi đánh giá: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertRatingToString(float rating) {
        if (rating == 1.0f) return "ONE";
        if (rating == 2.0f) return "TWO";
        if (rating == 3.0f) return "THREE";
        if (rating == 4.0f) return "FOUR";
        if (rating == 5.0f) return "FIVE";
        return "ONE"; // Default to ONE instead of UNKNOWN
    }

    private void addToCart(ProductResponse product) {
        CartUtils.addToCart(this, product);
    }

    private String formatPrice(double price) {
        return String.format("%,.0f", price);
    }
}
