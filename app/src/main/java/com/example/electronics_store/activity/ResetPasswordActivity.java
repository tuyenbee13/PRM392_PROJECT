package com.example.electronics_store.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText newPasswordEditText, confirmPasswordEditText;
    private TextInputLayout newPasswordLayout, confirmPasswordLayout;
    private Button resetPasswordButton;
    private TextView backToLogin;
    private String email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://techstore-81b9b-default-rtdb.firebaseio.com/");

        email = getIntent().getStringExtra("EMAIL");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật mật khẩu...");
        progressDialog.setCancelable(false);

        setupPasswordValidation();

        setupButtonClicks();
    }

    private void initializeViews() {
        newPasswordEditText = findViewById(R.id.new_password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        newPasswordLayout = findViewById(R.id.new_password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        backToLogin = findViewById(R.id.back_to_login);
    }

    private void setupPasswordValidation() {
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (!password.isEmpty()) {
                    if (password.length() < 8) {
                        newPasswordLayout.setError("Mật khẩu phải có ít nhất 8 ký tự");
                    } else if (!password.matches(".*[A-Z].*")) {
                        newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 chữ hoa");
                    } else if (!password.matches(".*[a-z].*")) {
                        newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 chữ thường");
                    } else if (!password.matches(".*[0-9].*")) {
                        newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 chữ số");
                    } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                        newPasswordLayout.setError("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
                    } else {
                        newPasswordLayout.setError(null);
                    }
                } else {
                    newPasswordLayout.setError(null);
                }
                validateConfirmPassword();
            }
        };

        // Confirm password validation
        TextWatcher confirmPasswordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword();
            }
        };

        newPasswordEditText.addTextChangedListener(passwordWatcher);
        confirmPasswordEditText.addTextChangedListener(confirmPasswordWatcher);
    }

    private void validateConfirmPassword() {
        String password = Objects.requireNonNull(newPasswordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString();

        if (!confirmPassword.isEmpty()) {
            if (!confirmPassword.equals(password)) {
                confirmPasswordLayout.setError("Mật khẩu xác nhận không khớp");
            } else {
                confirmPasswordLayout.setError(null);
            }
        } else {
            confirmPasswordLayout.setError(null);
        }

        boolean isValid = !password.isEmpty() && !confirmPassword.isEmpty()
                && password.equals(confirmPassword)
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        resetPasswordButton.setEnabled(isValid);
    }

    private void setupButtonClicks() {
        resetPasswordButton.setOnClickListener(v -> resetPassword());

        backToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void resetPassword() {
        final String newPassword = newPasswordEditText.getText().toString();

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        String emailKey = email.replace(".", ",");

        db.collection("users").document(emailKey)
                .update("password", newPassword)
                .addOnSuccessListener(aVoid -> {
                    updateFirebaseAuthPassword(newPassword);
                })
                .addOnFailureListener(e -> {
                    updateRealtimeDatabasePassword(emailKey, newPassword);
                });
    }

    private void updateRealtimeDatabasePassword(String emailKey, String newPassword) {
        databaseReference.child("users").child(emailKey).child("password")
                .setValue(newPassword)
                .addOnSuccessListener(aVoid -> {
                    updateFirebaseAuthPassword(newPassword);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(ResetPasswordActivity.this,
                            "Không thể cập nhật mật khẩu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFirebaseAuthPassword(String newPassword) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getSignInMethods() != null &&
                                !task.getResult().getSignInMethods().isEmpty()) {

                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(resetTask -> {
                                        progressDialog.dismiss();
                                        if (resetTask.isSuccessful()) {
                                            showSuccessAndFinish();
                                        } else {
                                            showSuccessAndFinish();
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            showSuccessAndFinish();
                        }
                    } else {
                        progressDialog.dismiss();
                        showSuccessAndFinish();
                    }
                });
    }

    private void showSuccessAndFinish() {
        Toast.makeText(ResetPasswordActivity.this,
                "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();

        // Show success dialog
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Thành công")
                .setMessage("Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập bằng mật khẩu mới.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Intent intent = new Intent(ResetPasswordActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}