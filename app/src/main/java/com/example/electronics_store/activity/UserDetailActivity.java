package com.example.electronics_store.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.example.electronics_store.model.UserDetail;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailEmail, tvDetailPhone, tvDetailRole, tvDetailStatus, tvDetailBanStatus;
    private MaterialButton btnToggleBan;
    private int userId;
    private boolean isBanned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailRole = findViewById(R.id.tvDetailRole);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailBanStatus = findViewById(R.id.tvDetailBanStatus);
        ImageView btnClose = findViewById(R.id.btnClose);
        btnToggleBan = findViewById(R.id.btnToggleBan);

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gọi API để lấy thông tin chi tiết người dùng
        fetchUserDetail(userId);

        // Xử lý sự kiện đóng Activity
        btnClose.setOnClickListener(v -> finish());

        // Xử lý sự kiện khi nhấn nút Ban/Unban
        btnToggleBan.setOnClickListener(v -> toggleBanStatus());
    }

    private void fetchUserDetail(int userId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<UserDetail> call = apiService.getUserDetail(userId);

        call.enqueue(new Callback<UserDetail>() {
            @Override
            public void onResponse(@NonNull Call<UserDetail> call, @NonNull Response<UserDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDetail userDetail = response.body();
                    displayUserDetail(userDetail);
                } else {
                    Toast.makeText(UserDetailActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserDetail> call, @NonNull Throwable t) {
                Toast.makeText(UserDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserDetail(UserDetail userDetail) {
        tvDetailName.setText(userDetail.getName());
        tvDetailEmail.setText(userDetail.getEmail());
        tvDetailPhone.setText(userDetail.getPhoneNumber());
        tvDetailRole.setText(userDetail.getRole());
        tvDetailStatus.setText(userDetail.getStatus());

        isBanned = userDetail.isBanned();
        updateBanStatusUI(isBanned);
    }

    @SuppressLint({"UseCompatLoadingForColorStateLists", "SetTextI18n"})
    private void updateBanStatusUI(boolean isBanned) {
        if (isBanned) {
            tvDetailBanStatus.setText("BANNED");
            tvDetailBanStatus.setBackgroundResource(R.drawable.ban_background);
            btnToggleBan.setText("Unban User");
            btnToggleBan.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        } else {
            tvDetailBanStatus.setText("ACTIVE");
            tvDetailBanStatus.setBackgroundResource(R.drawable.active_background);
            btnToggleBan.setText("Ban User");
            btnToggleBan.setBackgroundTintList(getResources().getColorStateList(R.color.red));
        }
    }

    private void toggleBanStatus() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call;

        if (isBanned) {
            call = apiService.unbanUser(userId);
        } else {
            call = apiService.banUser(userId);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    isBanned = !isBanned;
                    updateBanStatusUI(isBanned);
                    Toast.makeText(UserDetailActivity.this, isBanned ? "User banned successfully" : "User unbanned successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserDetailActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(UserDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}