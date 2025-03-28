package com.example.electronics_store.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.ProductRequest;
import com.example.electronics_store.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity {
    private ImageView ivProductImage;
    private EditText etProductName, etProductDescription, etProductPrice, etProductStock, etProductImage;
    private Button btnSave;
    private int productId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductStock = findViewById(R.id.etProductStock);
        etProductImage = findViewById(R.id.etProductImage);
        ivProductImage = findViewById(R.id.ivProductImage);

        etProductImage.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String imageUrl = etProductImage.getText().toString().trim();
                if (!imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(ivProductImage);
                }
            }
        });
        btnSave = findViewById(R.id.btnSave);

        productId = getIntent().getIntExtra("product_id", -1);

        if (productId == -1) {
            Toast.makeText(this, "Lỗi! Không tìm thấy sản phẩm.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProductDetails(productId);


        btnSave.setOnClickListener(v -> updateProduct());
    }

    private void loadProductDetails(int productId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ProductRequest> call = apiService.getProductById(productId);

        call.enqueue(new Callback<ProductRequest>() {
            @Override
            public void onResponse(Call<ProductRequest> call, Response<ProductRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductRequest product = response.body();
                    etProductName.setText(product.getName());
                    etProductDescription.setText(product.getDescription());
                    etProductPrice.setText(String.valueOf(product.getPrice()));
                    etProductStock.setText(String.valueOf(product.getStock()));
                    etProductImage.setText(product.getImageUrl());

                    if (!product.getImageUrl().isEmpty()) {
                        Glide.with(EditProductActivity.this)
                                .load(product.getImageUrl())
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.bug_report)
                                .into(ivProductImage);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductRequest> call, Throwable t) {
                Toast.makeText(EditProductActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct() {
        String name = etProductName.getText().toString();
        String description = etProductDescription.getText().toString();
        double price = Double.parseDouble(etProductPrice.getText().toString());
        int stock = Integer.parseInt(etProductStock.getText().toString());
        String imageUrl = etProductImage.getText().toString();

        ProductRequest productRequest = new ProductRequest(name, description, price, stock, 2, imageUrl); // Giả sử categoryId = 2

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.updateProduct(productId, productRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProductActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProductActivity.this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProductActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}