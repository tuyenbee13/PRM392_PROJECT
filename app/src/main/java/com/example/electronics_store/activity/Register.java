package com.example.electronics_store.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RegisterRequest;
import com.example.electronics_store.retrofit.RegisterResponse;
import com.example.electronics_store.retrofit.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        final EditText yourName = findViewById(R.id.name);
        final EditText emailAddress = findViewById(R.id.email);
        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final EditText confirmPassword = findViewById(R.id.reenter_password);
        final Button registerBtn = findViewById(R.id.signup_button);

        Button btnMale = findViewById(R.id.btnMale);
        Button btnFemale = findViewById(R.id.btnFemale);
        Button btnOther = findViewById(R.id.btnOther);

        TextView signin_textview = findViewById(R.id.signin_textview);

        signin_textview.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        });

        btnMale.setOnClickListener(v -> {
            selectedGender = "MALE";
            updateGenderButtonStyles(btnMale, btnFemale, btnOther);
        });

        btnFemale.setOnClickListener(v -> {
            selectedGender = "FEMALE";
            updateGenderButtonStyles(btnFemale, btnMale, btnOther);
        });

        btnOther.setOnClickListener(v -> {
            selectedGender = "OTHER";
            updateGenderButtonStyles(btnOther, btnMale, btnFemale);
        });

        registerBtn.setOnClickListener(v -> registerUser(yourName, emailAddress, phone, password, confirmPassword));


    }

    private void registerUser(EditText yourName, EditText emailAddress, EditText phone, EditText password, EditText confirmPassword) {
        final String nameText = yourName.getText().toString().trim();
        final String emailText = emailAddress.getText().toString().trim();
        final String phoneText = phone.getText().toString().trim();
        final String passwordText = password.getText().toString().trim();
        final String confirmPasswordText = confirmPassword.getText().toString().trim();

        if (nameText.isEmpty() || emailText.isEmpty() || phoneText.isEmpty()
                || passwordText.isEmpty() || confirmPasswordText.isEmpty()
                || selectedGender.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin");
            return;
        }

        if (!passwordText.equals(confirmPasswordText)) {
            showToast("Mật khẩu không khớp");
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(nameText, emailText, passwordText, phoneText, selectedGender);

        if (!isNetworkAvailable()) {
            showToast("Không có kết nối mạng. Vui lòng kiểm tra lại.");
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<RegisterResponse> call = apiService.registerUser(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isSuccess()) {
                        showToast("Đăng ký thành công: " + registerResponse.getMessage());
                        Intent intent = new Intent();
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        showToast("Lỗi: " + registerResponse.getMessage());
                    }
                } else {
                    showToast("Đăng ký thất bại: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                String errorMessage = "Lỗi kết nối: " + (t instanceof IOException ? "Không thể kết nối đến máy chủ" : t.getMessage());
                showToast(errorMessage);
                RetrofitClient.resetClient();
            }
        });
    }

    private void updateGenderButtonStyles(Button selectedButton, Button... otherButtons) {
        selectedButton.setBackgroundTintList(getColorStateList(R.color.white));
        selectedButton.setTextColor(getColor(R.color.red));

        for (Button btn : otherButtons) {
            btn.setBackgroundTintList(getColorStateList(R.color.red));
            btn.setTextColor(getColor(R.color.white));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showToast(String message) {
        Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
    }

}
