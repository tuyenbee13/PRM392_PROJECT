package com.example.electronics_store.activity;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.LoginRequest;
import com.example.electronics_store.retrofit.LoginResponse;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.*;
import com.google.firebase.database.annotations.NotNull;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

  private static final int RC_SIGN_IN = 9001;
  private GoogleSignInClient mGoogleSignInClient;
  private FirebaseAuth mAuth;
  DatabaseReference databaseReference = FirebaseDatabase
          .getInstance()
          .getReferenceFromUrl("https://techstore-81b9b-default-rtdb.firebaseio.com/");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.signin);

    mAuth = FirebaseAuth.getInstance();

    String savedToken = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("auth_token", null);
    if (savedToken != null) {
      RetrofitClient.setAuthToken(savedToken);
    }

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

    mGoogleSignInClient = getClient(this, gso);

    final EditText email = findViewById(R.id.email);
    final EditText password = findViewById(R.id.password);
    final Button loginBtn = findViewById(R.id.continue_button);
    final TextView registerBtn = findViewById(R.id.signupNow);
    final TextView forgotPassword = findViewById(R.id.forgot_password);
    final LinearLayout googleSignInContainer = findViewById(R.id.google_signin_container);

    forgotPassword.setOnClickListener(v -> {
      Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
      startActivity(intent);
    });

    loginBtn.setOnClickListener(v -> {
      final String emailTxt = email.getText().toString();
      final String passwordTxt = password.getText().toString();
      if (emailTxt.isEmpty() || passwordTxt.isEmpty()) {
        Toast.makeText(Login.this, "Please enter both your email and password!!", Toast.LENGTH_SHORT).show();
      } else {
        loginWithApi(emailTxt, passwordTxt);
//        String emailKey = emailTxt.replace(".", ",");
//        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//          @Override
//          public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//            if (snapshot.hasChild(emailKey)) {
//              final String getPassword = snapshot.child(emailKey).child("password").getValue(String.class);
//              String role = snapshot.child(emailKey).child("role").getValue(String.class);
//              if (getPassword != null && getPassword.equals(passwordTxt)) {
//                Toast.makeText(Login.this, "Login successfully!!", Toast.LENGTH_SHORT).show();
//                redirectBasedOnRole(role);
//              } else {
//                Toast.makeText(Login.this, "Try again! Wrong email or password!!", Toast.LENGTH_SHORT).show();
//              }
//            } else {
//              Toast.makeText(Login.this, "Try again! Wrong email or password!!", Toast.LENGTH_SHORT).show();
//            }
//          }
//
//          @Override
//          public void onCancelled(@NonNull DatabaseError error) {
//            Toast.makeText(Login.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//          }
//        });
      }
    });

    googleSignInContainer.setOnClickListener(v -> signInWithGoogle());
    registerBtn.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));
  }

  private void loginWithApi(String email, String password) {
    LoginRequest loginRequest = new LoginRequest(email, password);

    ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
    Call<LoginResponse> call = apiService.loginUser(loginRequest);

    call.enqueue(new Callback<LoginResponse>() {
      @Override
      public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
          LoginResponse loginResponse = response.body();
          String token = loginResponse.getToken();
          if (token != null) {
            saveAuthToken(token);
            RetrofitClient.setAuthToken(token);
          }
          String role = decodeJwtRole(token);
          redirectBasedOnRole(role);
          Toast.makeText(Login.this, "Login successfully!!", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(Login.this, "Try again! Wrong email or password!!", Toast.LENGTH_SHORT).show();
          Log.e("LOGIN_FAILED", response.message());
        }
      }

      @Override
      public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
        Toast.makeText(Login.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });
  }


  private void redirectBasedOnRole(String role) {
    Intent intent;
    if ("ADMIN".equals(role)) {
      intent = new Intent(Login.this, UserManagementActivity.class);
    } else {
      intent = new Intent(Login.this, ProductListActivity.class);
    }
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
  }
  private void signInWithGoogle() {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = getSignedInAccountFromIntent(data);
      try {
        GoogleSignInAccount account = task.getResult(ApiException.class);
        firebaseAuthWithGoogle(account.getIdToken());
      } catch (ApiException e) {
        Log.w("Google Sign-In", "Google sign-in failed", e);
        Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void firebaseAuthWithGoogle(String idToken) {
    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                Toast.makeText(this, "Sign-in successful: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
              } else {
                Log.w("Google Sign-In", "signInWithCredential:failure", task.getException());
                Toast.makeText(this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
              }
            });
  }

  private void saveAuthToken(String token) {
    getSharedPreferences("AppPrefs", MODE_PRIVATE)
            .edit()
            .putString("auth_token", token)
            .apply();
  }

  private String decodeJwtRole(String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        return "USER";
      }

      // Giải mã phần payload (Base64 URL-Safe)
      byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE);
      String payload = new String(decodedBytes, StandardCharsets.UTF_8);

      // Chuyển payload thành JSON và lấy role
      JSONObject jsonObject = new JSONObject(payload);
      return jsonObject.optString("role", "USER");
    } catch (Exception e) {
      e.printStackTrace();
      return "USER";
    }
  }

}
