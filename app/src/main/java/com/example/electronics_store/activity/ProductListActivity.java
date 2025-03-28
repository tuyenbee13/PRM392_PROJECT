package com.example.electronics_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import androidx.core.view.GravityCompat;

import com.example.electronics_store.R;
import com.example.electronics_store.adapter.BannerAdapter;
import com.example.electronics_store.adapter.UserProductAdapter;
import com.example.electronics_store.retrofit.*;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private UserProductAdapter userProductAdapter;
    private ApiService apiService;
    private SearchView searchView;
    private Spinner spinnerCategory, spinnerSort;
    private EditText minPrice, maxPrice;
    private Button btnFilter;
    private List<CategoryResponse> categoryList;
    private ImageButton btnCart, btnFavorite, btnNotification, btnChat;

    private ViewPager2 bannerViewPager;
    private BannerAdapter bannerAdapter;
    private List<String> bannerList;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Khởi tạo View
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSort = findViewById(R.id.spinnerSort);
        minPrice = findViewById(R.id.minPrice);
        maxPrice = findViewById(R.id.maxPrice);
        btnFilter = findViewById(R.id.btnFilter);
        btnCart = findViewById(R.id.btnCart);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnNotification = findViewById(R.id.btnNotification);
        btnChat = findViewById(R.id.btnChat);
        bannerViewPager = findViewById(R.id.bannerViewPager);

        Toolbar toolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);

        // Ánh xạ Navigation Drawer
        drawerLayout = findViewById(R.id.user_drawer_layout);
        NavigationView navigationView = findViewById(R.id.user_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Tạo nút mở menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Thiết lập RecyclerView với GridLayoutManager (2 cột)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Thiết lập banner
        initializeBanner();

        // Xử lý khi có thông báo từ Intent
        handleNotificationIntent();

        // Cài đặt Spinner và tải danh mục sản phẩm
        setupSortSpinner();
        loadCategories();

        // Lấy danh sách sản phẩm ban đầu
        fetchProducts(null, null, null, "DESC", null);

        // Xử lý tìm kiếm
        setupSearchView();

        // Xử lý nút lọc
        btnFilter.setOnClickListener(view -> applyFilters());

        // Xử lý các nút điều hướng
        setupNavigationButtons();
    }

    private void setupNavigationButtons() {
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }


    private void initializeBanner() {
        bannerList = Arrays.asList(
                "https://th.bing.com/th/id/OIP.ng6GrR09uCxfwX4OTZYY_QHaDC?rs=1&pid=ImgDetMain",
                "https://th.bing.com/th/id/OIP.4fxHY-leAvBN4saDO7ng3AHaCL?w=1200&h=353&rs=1&pid=ImgDetMain",
                "https://cdn.tgdd.vn/2022/03/banner/830-300-830x300-23.png"
        );

        bannerAdapter = new BannerAdapter(this, bannerList);
        bannerViewPager.setAdapter(bannerAdapter);

        // Tự động chuyển banner
        bannerRunnable = () -> {
            int nextItem = (bannerViewPager.getCurrentItem() + 1) % bannerList.size();
            bannerViewPager.setCurrentItem(nextItem, true);
            handler.postDelayed(bannerRunnable, 3000);
        };

        handler.postDelayed(bannerRunnable, 3000);
    }

    private void handleNotificationIntent() {
        Intent notificationIntent = getIntent();
        if (notificationIntent != null && notificationIntent.getData() != null) {
            String action = notificationIntent.getData().getHost();
            if ("payment_success".equals(action)) {
                Toast.makeText(this, "Thanh toán thành công! Quay về trang sản phẩm.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSortSpinner() {
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Giá tăng dần", "Giá giảm dần"});
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private String getSortDirection() {
        if (spinnerSort.getSelectedItem() != null) {
            return spinnerSort.getSelectedItemPosition() == 0 ? "ASC" : "DESC";
        }
        return "DESC"; // Giá trị mặc định nếu spinner chưa được chọn
    }


    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    categoryList.add(0, new CategoryResponse(-1, "Tất cả"));

                    ArrayAdapter<CategoryResponse> adapter = new ArrayAdapter<>(ProductListActivity.this,
                            android.R.layout.simple_spinner_item, categoryList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Lỗi tải danh mục!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        Integer categoryId = null;
        if (spinnerCategory.getSelectedItem() != null) {
            CategoryResponse selectedCategory = (CategoryResponse) spinnerCategory.getSelectedItem();
            if (selectedCategory.getId() != -1) {
                categoryId = selectedCategory.getId();
            }
        }

        Double min = minPrice.getText().toString().isEmpty() ? null : Double.parseDouble(minPrice.getText().toString());
        Double max = maxPrice.getText().toString().isEmpty() ? null : Double.parseDouble(maxPrice.getText().toString());

        fetchProducts(categoryId, min, max, getSortDirection(), searchView.getQuery().toString());
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchProducts(null, null, null, getSortDirection(), query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void fetchProducts(Integer categoryId, Double minPrice, Double maxPrice, String sortDirection, String searchQuery) {
        apiService.getAllProducts("price", sortDirection, categoryId, minPrice, maxPrice, searchQuery, null, null)
                .enqueue(new Callback<List<ProductResponse>>() {
                    @Override
                    public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ProductResponse> products = response.body();
                            if (products.isEmpty()) {
                                Toast.makeText(ProductListActivity.this, "Không tìm thấy sản phẩm nào!", Toast.LENGTH_SHORT).show();
                                recyclerView.setAdapter(null); // Xóa danh sách hiển thị cũ
                            } else {
                                userProductAdapter = new UserProductAdapter(ProductListActivity.this, products, false);
                                recyclerView.setLayoutManager(new GridLayoutManager(ProductListActivity.this, 2)); // Lưới 2 cột
                                recyclerView.setAdapter(userProductAdapter);
                            }
                        } else {
                            Toast.makeText(ProductListActivity.this, "Lỗi khi tải sản phẩm!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
                        Toast.makeText(ProductListActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_user_order_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        } else if (id == R.id.nav_user_update_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_user_change_password) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (id == R.id.nav_user_store_location) {
            startActivity(new Intent(this, InfoActivity.class));
        } else if (id == R.id.nav_user_logout) {
            logout(); // Đóng ứng dụng hoặc xử lý đăng xuất
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
