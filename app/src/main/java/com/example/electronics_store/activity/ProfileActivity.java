package com.example.electronics_store.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.example.electronics_store.retrofit.UpdateProfileRequest;
import com.example.electronics_store.retrofit.UpdateProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText;
    private RadioGroup genderRadioGroup;
    private Button updateProfileButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        loadUserData();

        updateProfileButton.setOnClickListener(v -> updateProfile());
    }

    private void loadUserData() {
        String name = sharedPreferences.getString("user_name", "");
        String phone = sharedPreferences.getString("user_phone", "");
        String gender = sharedPreferences.getString("user_gender", "MALE");

        nameEditText.setText(name);
        phoneEditText.setText(phone);

        if ("MALE".equals(gender)) {
            genderRadioGroup.check(R.id.maleRadioButton);
        } else {
            genderRadioGroup.check(R.id.femaleRadioButton);
        }
    }

    private void updateProfile() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

        // Kiểm tra tên (Không chứa số hoặc ký tự đặc biệt)
        if (!name.matches("^[a-zA-ZÀ-Ỹà-ỹ\\s]+$")) {
            nameEditText.setError("Tên không hợp lệ! Vui lòng nhập lại.");
            nameEditText.requestFocus();
            return;
        }

        // Kiểm tra số điện thoại (Phải có 10 chữ số và bắt đầu bằng số 0)
        if (!phone.matches("^0[0-9]{9}$")) {
            phoneEditText.setError("Số điện thoại không hợp lệ! Vui lòng nhập số gồm 10 chữ số.");
            phoneEditText.requestFocus();
            return;
        }

        // Kiểm tra giới tính đã chọn chưa
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính!", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = (selectedGenderId == R.id.maleRadioButton) ? "MALE" : "FEMALE";

        // Kiểm tra token
        String authToken = sharedPreferences.getString("auth_token", "");
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thực hiện cập nhật API
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        UpdateProfileRequest request = new UpdateProfileRequest(name, phone, gender);

        apiService.updateProfile("Bearer " + authToken, request).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpdateProfileResponse> call, @NonNull Response<UpdateProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    saveUserData(name, phone, gender);
                } else {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UpdateProfile", "Lỗi: ", t);
            }
        });
    }


    private void saveUserData(String name, String phone, String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name", name);
        editor.putString("user_phone", phone);
        editor.putString("user_gender", gender);
        editor.apply();
    }
}
