package com.example.electronics_store.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.CategoryResponse;
import com.example.electronics_store.retrofit.ProductRequest;
import com.example.electronics_store.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    private EditText etName, etDescription, etPrice, etStock, etCategoryId, etImageUrl;
    private Spinner spinnerCategory;
    private Button btnSubmit;
    private ApiService apiService;
    private List<CategoryResponse> categoryList = new ArrayList<>();
    private int selectedCategoryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etImageUrl = findViewById(R.id.etImageUrl);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSubmit = findViewById(R.id.btnSubmit);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadCategories();

        btnSubmit.setOnClickListener(v -> submitProduct());
    }

    private void loadCategories() {
        Call<List<CategoryResponse>> call = apiService.getCategories();
        call.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    for (CategoryResponse category : categoryList) {
                        categoryNames.add(category.getName());
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProductActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, categoryNames);
                    spinnerCategory.setAdapter(adapter);


                    spinnerCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                            selectedCategoryId = categoryList.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                            selectedCategoryId = -1;
                        }
                    });
                } else {
                    Toast.makeText(AddProductActivity.this, "Lỗi khi tải danh mục!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitProduct() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Kiểm tra các trường không được để trống
        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng giá
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                Toast.makeText(this, "Giá sản phẩm phải lớn hơn 0!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số lượng sản phẩm
        int stock;
        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                Toast.makeText(this, "Số lượng sản phẩm phải là số nguyên không âm!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra danh mục đã chọn
        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng URL
        if (!android.util.Patterns.WEB_URL.matcher(imageUrl).matches()) {
            Toast.makeText(this, "URL hình ảnh không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng sản phẩm và gửi request
        ProductRequest product = new ProductRequest(name, description, price, stock, selectedCategoryId, imageUrl);
        Call<Void> call = apiService.addProduct(product);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddProductActivity.this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddProductActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}