package com.example.electronics_store.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6;
    private TextView resendOtp, countdownTimer;
    private Button verifyButton;
    private String verificationId;
    private String email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private CountDownTimer timer;
    private boolean isResendEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        verificationId = getIntent().getStringExtra("VERIFICATION_ID");
        email = getIntent().getStringExtra("EMAIL");

        if (verificationId == null || verificationId.isEmpty() || email == null || email.isEmpty()) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xác thực mã OTP...");
        progressDialog.setCancelable(false);

        // Setup OTP input fields
        setupOtpInputs();

        // Setup button and timer
        setupVerifyButton();
        setupResendOtpButton();
        startCountdownTimer();
    }

    private void initializeViews() {
        otpDigit1 = findViewById(R.id.otp_digit_1);
        otpDigit2 = findViewById(R.id.otp_digit_2);
        otpDigit3 = findViewById(R.id.otp_digit_3);
        otpDigit4 = findViewById(R.id.otp_digit_4);
        otpDigit5 = findViewById(R.id.otp_digit_5);
        otpDigit6 = findViewById(R.id.otp_digit_6);
        resendOtp = findViewById(R.id.resend_otp);
        countdownTimer = findViewById(R.id.countdown_timer);
        verifyButton = findViewById(R.id.verify_otp_button);
    }

    private void setupOtpInputs() {
        EditText[] otpFields = {otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6};

        for (int i = 0; i < otpFields.length; i++) {
            final int currentIndex = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && currentIndex < otpFields.length - 1) {
                        otpFields[currentIndex + 1].requestFocus();
                    }
                    boolean allFilled = true;
                    for (EditText field : otpFields) {
                        if (field.getText().toString().isEmpty()) {
                            allFilled = false;
                            break;
                        }
                    }
                    verifyButton.setEnabled(allFilled);
                    if (allFilled) {
                        verifyButton.setAlpha(1.0f);
                    } else {
                        verifyButton.setAlpha(0.5f);
                    }
                }
            });
            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                        otpFields[currentIndex].getText().toString().isEmpty() &&
                        currentIndex > 0) {
                    otpFields[currentIndex - 1].requestFocus();
                    return true;
                }
                return false;
            });

            otpFields[i].setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_NEXT && currentIndex < otpFields.length - 1) {
                    otpFields[currentIndex + 1].requestFocus();
                    return true;
                } else if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE && currentIndex == otpFields.length - 1) {
                    if (verifyButton.isEnabled()) {
                        verifyOtp();
                    }
                    return true;
                }
                return false;
            });
        }
        otpDigit1.requestFocus();
    }

    private void setupVerifyButton() {
        verifyButton.setEnabled(false);
        verifyButton.setOnClickListener(v -> verifyOtp());
    }

    private void setupResendOtpButton() {
        resendOtp.setOnClickListener(v -> {
            if (isResendEnabled) {
                resendOTP();
            }
        });
    }

    private void startCountdownTimer() {
        isResendEnabled = false;
        resendOtp.setTextColor(getResources().getColor(android.R.color.darker_gray));

        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTimer.setText(String.format("Gửi lại sau: %d giây", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                countdownTimer.setText("");
                resendOtp.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                isResendEnabled = true;
            }
        }.start();
    }

    private void verifyOtp() {
        StringBuilder otpBuilder = new StringBuilder();
        otpBuilder.append(otpDigit1.getText().toString())
                .append(otpDigit2.getText().toString())
                .append(otpDigit3.getText().toString())
                .append(otpDigit4.getText().toString())
                .append(otpDigit5.getText().toString())
                .append(otpDigit6.getText().toString());

        String otp = otpBuilder.toString();

        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        verifyPhoneNumberWithCode(credential);
    }

    private void verifyPhoneNumberWithCode(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(OtpVerificationActivity.this,
                                "Xác thực OTP thành công", Toast.LENGTH_SHORT).show();
                        proceedToResetPassword();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this,
                                "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(OtpVerificationActivity.this,
                            "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void resendOTP() {
        progressDialog.setMessage("Đang gửi lại mã OTP...");
        progressDialog.show();

        String emailKey = email.replace(".", ",");
        db.collection("users").document(emailKey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        String phoneNumber = task.getResult().getString("phone");
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            sendOtpToPhone(phoneNumber);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(OtpVerificationActivity.this,
                                    "Không tìm thấy số điện thoại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(OtpVerificationActivity.this,
                                "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendOtpToPhone(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressDialog.dismiss();
                                verifyPhoneNumberWithCode(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressDialog.dismiss();
                                Toast.makeText(OtpVerificationActivity.this,
                                        "Gửi lại OTP thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                progressDialog.dismiss();
                                verificationId = newVerificationId;
                                resendToken = token;
                                Toast.makeText(OtpVerificationActivity.this,
                                        "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show();
                                startCountdownTimer();

                                otpDigit1.setText("");
                                otpDigit2.setText("");
                                otpDigit3.setText("");
                                otpDigit4.setText("");
                                otpDigit5.setText("");
                                otpDigit6.setText("");
                                otpDigit1.requestFocus();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void proceedToResetPassword() {
        Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}