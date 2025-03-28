package com.example.electronics_store.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.R;
import com.example.electronics_store.adapter.CartAdapter;
import com.example.electronics_store.retrofit.CartUtils;
import com.example.electronics_store.retrofit.ProductResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalPriceText;
    private Button btnContinueShopping, continueButton;
    private List<ProductResponse> cartList = new ArrayList<>();
    private CartAdapter cartAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        recyclerView = findViewById(R.id.recyclerview);
        totalPriceText = findViewById(R.id.total_price_text);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);
        continueButton = findViewById(R.id.continue_button);

        if (continueButton == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy continue_button", Toast.LENGTH_SHORT).show();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCartData();

        cartAdapter = new CartAdapter(cartList, this, this::removeItemFromCart, this::updateItemQuantity);
        recyclerView.setAdapter(cartAdapter);
        updateTotalPrice();

        btnContinueShopping.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, ProductListActivity.class));
            finish();
        });

        continueButton.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartList));
            startActivity(intent);
        });
    }

    private void loadCartData() {
        List<ProductResponse> originalCartList = CartUtils.getCartList(this);
        if (originalCartList == null || originalCartList.isEmpty()) {
            cartList.clear();
            return;
        }

        Map<Integer, ProductResponse> groupedCartMap = new HashMap<>();
        for (ProductResponse product : originalCartList) {
            if (groupedCartMap.containsKey(product.getId())) {
                ProductResponse existingProduct = groupedCartMap.get(product.getId());
                existingProduct.setQuantity(existingProduct.getQuantity() + product.getQuantity());
            } else {
                groupedCartMap.put(product.getId(), product);
            }
        }

        List<ProductResponse> newCartList = new ArrayList<>(groupedCartMap.values());
        if (!cartList.equals(newCartList)) {
            cartList.clear();
            cartList.addAll(newCartList);
            CartUtils.saveCartList(this, cartList);
        }
    }
    private void removeItemFromCart(int position) {
        if (position < 0 || position >= cartList.size()) return;

        List<ProductResponse> newCartList = new ArrayList<>(cartList);
        ProductResponse product = newCartList.get(position);

        if (product.getQuantity() > 1) {
            product.setQuantity(product.getQuantity() - 1);
            cartAdapter.notifyItemChanged(position);
        } else {
            newCartList.remove(position);
        }

        CartUtils.saveCartList(this, newCartList);
        cartAdapter.updateData(newCartList);

        // Cập nhật danh sách giỏ hàng
        cartList.clear();
        cartList.addAll(newCartList);

        // Gọi updateTotalPrice() để cập nhật giao diện
        updateTotalPrice();
    }


    private void updateItemQuantity(int position, int newQuantity) {
        if (position < 0 || position >= cartList.size() || newQuantity < 1) return;

        cartList.get(position).setQuantity(newQuantity);
        CartUtils.saveCartList(this, cartList);
        cartAdapter.updateData(cartList);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0.0;
        for (ProductResponse product : cartList) {
            total += product.getPrice() * product.getQuantity();
        }

        // Kiểm tra nếu giỏ hàng trống thì hiển thị 0đ
        if (cartList.isEmpty()) {
            totalPriceText.setText("0đ");
        } else {
            totalPriceText.setText(String.format("%.0fđ", total));
        }
    }


    public static void clearCart(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
