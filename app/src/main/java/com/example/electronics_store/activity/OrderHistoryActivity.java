package com.example.electronics_store.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.R;
import com.example.electronics_store.adapter.OrderHistoryAdapter;
import com.example.electronics_store.adapter.OrderListResponse;
import com.example.electronics_store.retrofit.OrderResponse;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;
    private OrderHistoryAdapter adapter;
    private List<OrderResponse> orderList = new ArrayList<>();
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.oh_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lịch Sử Đơn Hàng");

        recyclerView = findViewById(R.id.oh_recyclerViewOrders);
        drawerLayout = findViewById(R.id.oh_drawer_layout);
        progressBar = findViewById(R.id.oh_progressBar);
        tvEmptyMessage = findViewById(R.id.oh_tvEmptyMessage);
        NavigationView navigationView = findViewById(R.id.oh_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(orderList);
        recyclerView.setAdapter(adapter);

        loadOrderHistory();
    }
//    public void updateOrderList(List<OrderResponse> newOrderList) {
//        this.orderList.clear(); // Xóa dữ liệu cũ
//        this.orderList.addAll(newOrderList); // Thêm dữ liệu mới
//        adapter.notifyDataSetChanged(); // ✅ Đúng
//
//    }

    private void loadOrderHistory() {
        Log.d("OrderHistory", "Bắt đầu gọi API lấy danh sách đơn hàng");
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = preferences.getString("auth_token", null);

        if (token == null || token.isEmpty()) {
            Log.e("OrderHistory", "❌ Token bị null hoặc rỗng!");
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<OrderListResponse> call = apiService.getUserOrders("Bearer " + token);

        call.enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
                progressBar.setVisibility(View.GONE);
                Log.d("OrderHistory", "API trả về mã: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    orderList = response.body().getData();

                    if (orderList == null || orderList.isEmpty()) {
                        Log.w("OrderHistory", "📭 Không có đơn hàng nào!");
                        tvEmptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        Log.d("OrderHistory", "📦 Số đơn hàng nhận được: " + orderList.size());
//                        adapter.updateOrderList(orderList);  // Cập nhật dữ liệu cho adapter
                        adapter.setOrderList(orderList);

                        // Đảm bảo gán lại sự kiện click sau khi cập nhật dữ liệu
                        adapter.setOnItemClickListener(order -> {
                            Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
                            intent.putExtra("order_id", order.getId());
                            intent.putExtra("order_status", order.getStatus());
                            intent.putExtra("order_price", order.getTotalPrice());
                            intent.putExtra("order_user_id", order.getUserId());
                            startActivity(intent);
                        });
                        tvEmptyMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("OrderHistory", "API thất bại, mã lỗi: " + response.code());
                    Toast.makeText(OrderHistoryActivity.this, "Lỗi khi tải danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("OrderHistory", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(OrderHistoryActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_user_update_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_user_change_password) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (id == R.id.nav_user_store_location) {
            startActivity(new Intent(this, InfoActivity.class));
        } else if (id == R.id.nav_user_product) {
            startActivity(new Intent(this, ProductListActivity.class));
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
