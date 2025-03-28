package com.example.electronics_store.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.electronics_store.R;
import com.example.electronics_store.adapter.CartItemAdapter;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.CartUtils;
import com.example.electronics_store.retrofit.OrderRequest;
import com.example.electronics_store.retrofit.OrderResponse;
import com.example.electronics_store.retrofit.ProductResponse;
import com.example.electronics_store.Api.CreateOrder;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText fullName, phoneNumber, email, address;
    private ListView cartItemListView;
    private TextView totalPriceText;
    private RadioGroup paymentOptions;
    private Button btnConfirmPayment;
    private List<ProductResponse> cartList;
    private CartItemAdapter cartAdapter;
    private static final String CHANNEL_ID = "order_notifications";
    private GoogleMap mMap;
    private EditText edtAddress;
    private Button btnSearchAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);

        fullName = findViewById(R.id.fullName);
        phoneNumber = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        cartItemListView = findViewById(R.id.cartItemListView);
        totalPriceText = findViewById(R.id.totalPrice);
        paymentOptions = findViewById(R.id.paymentMethodGroup);
        btnConfirmPayment = findViewById(R.id.orderButton);
        edtAddress = findViewById(R.id.address);
        btnSearchAddress = findViewById(R.id.btn_search_address);
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            finish();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnSearchAddress.setOnClickListener(v -> searchLocation());

        cartList = getIntent().getParcelableArrayListExtra("cartItems");

        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        cartAdapter = new CartItemAdapter(this, cartList);
        cartItemListView.setAdapter(cartAdapter);

        updateTotalPrice();
        createNotificationChannel();
        btnConfirmPayment.setOnClickListener(v -> handleOrder());
    }

    private void handleOrder() {
        String customerName = fullName.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String customerEmail = email.getText().toString().trim();
        String customerAddress = address.getText().toString().trim();
        int selectedId = paymentOptions.getCheckedRadioButtonId();

        if (!isValidName(customerName)) {
            Toast.makeText(this, "Tên không hợp lệ. Không được chứa số hoặc ký tự đặc biệt!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Số điện thoại phải có 10 số và bắt đầu bằng 0!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidEmail(customerEmail)) {
            Toast.makeText(this, "Email phải là @gmail.com!", Toast.LENGTH_LONG).show();
            return;
        }

        if (customerAddress.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ!", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedId == -1) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán!", Toast.LENGTH_LONG).show();
            return;
        }
        String address = edtAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String paymentMethod = selectedRadioButton.getText().toString().equals("Thanh toán qua ZaloPay") ? "ZALOPAY" : "CASH";
        double totalAmount = calculateTotalAmount();

        if (paymentMethod.equals("CASH")) {
            createOrder(paymentMethod);
        } else if (paymentMethod.equals("ZALOPAY")) {
            processZaloPayPayment(totalAmount);
        }
    }

    // Kiểm tra tên khách hàng (chỉ cho phép chữ cái và khoảng trắng)
    private boolean isValidName(String name) {
        return name.matches("^[\\p{L} ]+$");
    }

    // Kiểm tra số điện thoại (10 số, bắt đầu bằng 0)
    private boolean isValidPhone(String phone) {
        return phone.matches("^0\\d{9}$");
    }

    // Kiểm tra email (phải có @gmail.com)
    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
    }

    private void createOrder(String paymentMethod) {
        List<OrderRequest.OrderItem> orderItems = new ArrayList<>();
        for (ProductResponse product : cartList) {
            orderItems.add(new OrderRequest.OrderItem(product.getId(), product.getQuantity()));
        }

        OrderRequest orderRequest = new OrderRequest(orderItems, paymentMethod);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<OrderResponse> call = apiService.createOrder(orderRequest);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    saveOrderNotification("Đơn hàng của bạn đã được đặt thành công!");
                    showOrderNotification();
                    CartUtils.clearCart(PaymentActivity.this); // Xóa giỏ hàng sau khi thanh toán thành công
                    navigateToProductList();
                } else {
                    Toast.makeText(PaymentActivity.this, "Số lượng sản phẩm không đủ!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                Toast.makeText(PaymentActivity.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processZaloPayPayment(double amount) {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    CreateOrder createOrder = new CreateOrder();
                    return createOrder.createOrder(String.valueOf((int) amount));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject orderResponse) {
                if (orderResponse != null && orderResponse.has("order_url")) {
                    try {
                        String paymentUrl = orderResponse.getString("order_url");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                        startActivity(intent);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            CartUtils.clearCart(PaymentActivity.this); // Xóa giỏ hàng sau khi thanh toán thành công
                            navigateToProductList();
                        }, 5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "Không thể tạo đơn hàng ZaloPay", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void navigateToProductList() {
        Intent intent = new Intent(PaymentActivity.this, ProductListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private double calculateTotalAmount() {
        double total = 0.0;
        for (ProductResponse product : cartList) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    private void updateTotalPrice() {
        totalPriceText.setText(String.format("%.0fđ", calculateTotalAmount()));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Notifications";
            String description = "Thông báo về đơn hàng";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showOrderNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle("Đặt hàng thành công!")
                .setContentText("Đơn hàng của bạn đã được xác nhận.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void saveOrderNotification(String message) {
        SharedPreferences sharedPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastOrderNotification", message);
        editor.apply();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(10.762622, 106.660172); // Tọa độ mặc định
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí đã chọn"));
            edtAddress.setText(latLng.latitude + ", " + latLng.longitude);
        });
    }

    private void searchLocation() {
        String address = edtAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ!", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Cập nhật bản đồ
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(address));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                Toast.makeText(this, "Không tìm thấy địa chỉ!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Lỗi tìm kiếm địa chỉ!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}