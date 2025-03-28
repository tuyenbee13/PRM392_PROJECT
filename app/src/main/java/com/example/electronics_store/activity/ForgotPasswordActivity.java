package com.example.electronics_store.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button continueButton;
    private TextView backToSignin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.email_forgot);
        continueButton = findViewById(R.id.continue_reset_button);
        backToSignin = findViewById(R.id.back_to_signin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang kiểm tra email...");
        progressDialog.setCancelable(false);

        continueButton.setOnClickListener(v -> verifyEmail());

        backToSignin.setOnClickListener(v -> finish());
    }

    private void verifyEmail() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        if (email.isEmpty()) {
            emailEditText.setError("Vui lòng nhập email");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Vui lòng nhập email hợp lệ");
            emailEditText.requestFocus();
            return;
        }

        progressDialog.show();

        String emailKey = email.replace(".", ",");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(emailKey);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, PhoneVerificationActivity.class);
                    intent.putExtra("EMAIL", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPasswordActivity.this,
                        "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}