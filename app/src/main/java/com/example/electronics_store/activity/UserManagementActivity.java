package com.example.electronics_store.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.R;
import com.example.electronics_store.adapter.UserAdapter;
import com.example.electronics_store.adapter.UserManagementAdapter;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.example.electronics_store.retrofit.UserResponse;
import com.example.electronics_store.retrofit.UserUpdateRequest;
import com.google.android.material.navigation.NavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private UserManagementAdapter userAdapter;
    private DrawerLayout drawerLayout;
    private Button reloadBtn;
    private EditText edtSearchPhone;
    private Button btnSearchUser;
    private List<UserResponse> userList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermanagement);
        edtSearchPhone = findViewById(R.id.edtSearchPhone);
        btnSearchUser = findViewById(R.id.btnSearchUser);
        btnSearchUser.setOnClickListener(v -> {
            String phoneNumber = edtSearchPhone.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                filterUsers(phoneNumber);
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Danh Sách Người Dùng");
        reloadBtn = findViewById(R.id.btnReloadUser);
        reloadBtn.setOnClickListener(v -> {
            Toast.makeText(UserManagementActivity.this, "Đang tải lại danh sách...", Toast.LENGTH_SHORT).show();
            fetchUsers();
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        userAdapter = new UserManagementAdapter(new ArrayList<>());
        recyclerView.setAdapter(userAdapter);

        fetchUsers();
    }

    private void fetchUsers() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<UserResponse>> call = apiService.getAllUsers();

        call.enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    userAdapter.setUserList(response.body());
                } else {
                    Toast.makeText(UserManagementActivity.this, "Lỗi khi tải danh sách người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                Toast.makeText(UserManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void filterUsers(String phoneNumber) {
        List<UserResponse> filteredList = new ArrayList<>();
        for (UserResponse user : userList) {
            if (user.getPhoneNumber().contains(phoneNumber)) {
                filteredList.add(user);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy người dùng với số điện thoại này", Toast.LENGTH_SHORT).show();
        }
        userAdapter.setUserList(filteredList);
    }
}