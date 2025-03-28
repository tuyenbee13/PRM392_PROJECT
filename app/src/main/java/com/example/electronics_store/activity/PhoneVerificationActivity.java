package com.example.electronics_store.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private TextInputEditText phoneNumberEditText;
    private Button sendOtpButton;
    private TextView backToEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String email;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verify);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = getIntent().getStringExtra("EMAIL");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        phoneNumberEditText = findViewById(R.id.phone_number);
        sendOtpButton = findViewById(R.id.send_otp_button);
        backToEmail = findViewById(R.id.back_to_email);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xác thực số điện thoại...");
        progressDialog.setCancelable(false);

        sendOtpButton.setOnClickListener(v -> verifyPhoneNumber());

        backToEmail.setOnClickListener(v -> finish());
    }

    private void verifyPhoneNumber() {
        if (!isNetworkConnected()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumber = Objects.requireNonNull(phoneNumberEditText.getText()).toString().trim();
        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Vui lòng nhập số điện thoại");
            phoneNumberEditText.requestFocus();
            return;
        }

        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+84" + phoneNumber.replaceFirst("^0+", "");
        }

        final String formattedPhoneNumber = phoneNumber;

        progressDialog.show();

        String finalEmail = email.replace(".", ",");
        db.collection("users").document(finalEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String storedPhone = document.getString("phone");

                            if (storedPhone != null && storedPhone.equals(formattedPhoneNumber)) {
                                sendOTP(formattedPhoneNumber);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(PhoneVerificationActivity.this,
                                        "Số điện thoại không khớp với tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            checkPhoneInRealtimeDatabase(formattedPhoneNumber, finalEmail);
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PhoneVerificationActivity.this,
                                "Lỗi: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    private void checkPhoneInRealtimeDatabase(String phoneNumber, String emailKey) {
        FirebaseFirestore.getInstance().collection("users").document(emailKey)
                .get()
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        String storedPhone = task.getResult().getString("phone");
                        if (storedPhone != null && storedPhone.equals(phoneNumber)) {
                            sendOTP(phoneNumber);
                        } else {
                            Toast.makeText(PhoneVerificationActivity.this,
                                    "Số điện thoại không khớp với tài khoản", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PhoneVerificationActivity.this,
                                "Không tìm thấy tài khoản hoặc có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressDialog.dismiss();
                                Toast.makeText(PhoneVerificationActivity.this,
                                        "Xác thực số điện thoại thành công", Toast.LENGTH_SHORT).show();
                                proceedToResetPassword();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressDialog.dismiss();
                                Toast.makeText(PhoneVerificationActivity.this,
                                        "Xác thực thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                progressDialog.dismiss();
                                Toast.makeText(PhoneVerificationActivity.this,
                                        "Mã OTP đã được gửi", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(PhoneVerificationActivity.this, OtpVerificationActivity.class);
                                intent.putExtra("VERIFICATION_ID", verificationId);
                                intent.putExtra("EMAIL", email);
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void proceedToResetPassword() {
        Intent intent = new Intent(PhoneVerificationActivity.this, ResetPasswordActivity.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        finish();
    }
}