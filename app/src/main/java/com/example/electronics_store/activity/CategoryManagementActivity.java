package com.example.electronics_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
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

import com.example.electronics_store.R;
import com.example.electronics_store.adapter.CategoryAdapter;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.CategoryRequest;
import com.example.electronics_store.retrofit.CategoryResponse;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryManagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private CategoryAdapter categoryAdapter;
    private Button btnAddCategory, btnReloadCategory;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh Sách Mặt hàng");

        recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnReloadCategory = findViewById(R.id.btnReloadCategory);
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(categoryAdapter);
        drawerLayout = findViewById(R.id.main);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnReloadCategory.setOnClickListener(v -> {
            Toast.makeText(CategoryManagementActivity.this, "Đang tải lại...", Toast.LENGTH_SHORT).show();
            fetchCategories();
        });
        btnAddCategory = findViewById(R.id.btnAddCategory);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnAddCategory.setOnClickListener(v -> addCategory("New Category"));

        fetchCategories();
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setCategoryList(response.body());
                } else {
                    Toast.makeText(CategoryManagementActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable t) {
                Toast.makeText(CategoryManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory(String name) {
        CategoryRequest newCategory = new CategoryRequest(name);
        apiService.addCategory(newCategory).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchCategories();
                } else {
                    Toast.makeText(CategoryManagementActivity.this, "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(CategoryManagementActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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