package com.example.electronics_store.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.example.electronics_store.retrofit.UpdateProfileRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private EditText etName, etPhoneNumber, etAvatar;
    private RadioGroup rgGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAvatar = findViewById(R.id.etAvatar);
        rgGender = findViewById(R.id.rgGender);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String avatar = etAvatar.getText().toString().trim();
        String gender = ((RadioButton) findViewById(rgGender.getCheckedRadioButtonId())).getText().toString();

        if (name.isEmpty() || phoneNumber.isEmpty() || avatar.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(name, phoneNumber, avatar, gender);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.updateProfile(updateProfileRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}