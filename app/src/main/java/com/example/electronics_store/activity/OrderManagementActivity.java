package com.example.electronics_store.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.R;
import com.example.electronics_store.adapter.OrderManagementAdapter;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.OrderResponse;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderManagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewOrders;
    private OrderManagementAdapter orderAdapter;
    private Button btnSearchOrder;
    private EditText edtSearchOrder;
    private List<OrderResponse> allOrders = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đơn Hàng");

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        edtSearchOrder = findViewById(R.id.edtSearchOrder);
        btnSearchOrder = findViewById(R.id.btnSearchOrder);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        orderAdapter = new OrderManagementAdapter(new ArrayList<>());
        recyclerViewOrders.setAdapter(orderAdapter);

        fetchOrders();

//        orderAdapter.setOnItemClickListener(order -> {
//            Intent intent = new Intent(OrderManagementActivity.this, OrderDetailActivity.class);
//            intent.putExtra("order_id", order.getId());
//            intent.putExtra("order_status", order.getStatus());
//            intent.putExtra("order_price", order.getTotalPrice());
//            intent.putExtra("order_user_id", order.getUserId());
//            startActivity(intent);
//        });

        btnSearchOrder.setOnClickListener(v -> {
            String query = edtSearchOrder.getText().toString().trim();
            if(query.isEmpty()){
                Toast.makeText(OrderManagementActivity.this,"Vui lòng nhập Mã Đơn Hàng", Toast.LENGTH_SHORT).show();
            } else {
                filterOrders(query);
            }
        });
    }

    private void fetchOrders() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<OrderResponse>> call = apiService.getAllOrders();

        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderResponse>> call, @NonNull Response<List<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders = response.body();
                    orderAdapter.setOrderList(allOrders);

                    // Đảm bảo gán lại sự kiện click sau khi cập nhật dữ liệu
                    orderAdapter.setOnItemClickListener(order -> {
                        Intent intent = new Intent(OrderManagementActivity.this, OrderDetailActivity.class);
                        intent.putExtra("order_id", order.getId());
                        intent.putExtra("order_status", order.getStatus());
                        intent.putExtra("order_price", order.getTotalPrice());
                        intent.putExtra("order_user_id", order.getUserId());
                        startActivity(intent);
                    });
                } else {
                    Toast.makeText(OrderManagementActivity.this, "Lỗi khi tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderResponse>> call, @NonNull Throwable t) {
                Toast.makeText(OrderManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void filterOrders(String query) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        int orderId = Integer.parseInt(query);

        Call<OrderResponse> call = apiService.getOrderById(orderId);
        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderResponse> filteredOrder = new ArrayList<>();
                    filteredOrder.add(response.body());
                    orderAdapter.setOrderList(filteredOrder);
                } else {
                    Toast.makeText(OrderManagementActivity.this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                Toast.makeText(OrderManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
