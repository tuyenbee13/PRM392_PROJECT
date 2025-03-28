package com.example.electronics_store.activity;
import com.example.electronics_store.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electronics_store.adapter.ProductAdapter;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.ProductResponse;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductManagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private Button AddPrdBtn, BtnReload;
    private ProductAdapter productAdapter;
    private EditText edtSearchProduct;
    private Button btnSearchProduct;
    private List<ProductResponse> productList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        AddPrdBtn = findViewById(R.id.btnAddProduct);
        edtSearchProduct = findViewById(R.id.edtSearchProduct);
        btnSearchProduct = findViewById(R.id.btnSearchProduct);
        btnSearchProduct.setOnClickListener(v -> searchProduct());

        AddPrdBtn.setOnClickListener(v-> {
            Intent intent = new Intent(ProductManagementActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh Sách Sản Phẩm");

        BtnReload = findViewById(R.id.btnReload);
        BtnReload.setOnClickListener(v -> fetchProducts());
        drawerLayout = findViewById(R.id.main);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(productAdapter);
 
        fetchProducts();
    }

    private void searchProduct() {
        String query = edtSearchProduct.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            productAdapter.setProductList(productList);
            return;
        }

        List<ProductResponse> filteredList = new ArrayList<>();
        for (ProductResponse product : productList) {
            if (product.getName().toLowerCase().contains(query)) {
                filteredList.add(product);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
        }

        productAdapter.setProductList(filteredList);
    }



    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<ProductResponse>> call = apiService.getProducts();

        call.enqueue(new Callback<List<ProductResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductResponse>> call, @NonNull Response<List<ProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setProductList(response.body());
                    productList = response.body();
                } else {
                    Toast.makeText(ProductManagementActivity.this, "Lỗi khi tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductResponse>> call, @NonNull Throwable t) {
                Toast.makeText(ProductManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_users) {
            startActivity(new Intent(this, UserManagementActivity.class));
        } else if (id == R.id.nav_products) {
            startActivity(new Intent(this, ProductManagementActivity.class));
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(this, CategoryManagementActivity.class));
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(this, OrderManagementActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        RetrofitClient.setAuthToken(null);

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        finish();
    }
}