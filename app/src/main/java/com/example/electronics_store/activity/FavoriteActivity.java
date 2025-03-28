package com.example.electronics_store.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electronics_store.R;
import com.example.electronics_store.adapter.FavoriteAdapter;
import com.example.electronics_store.adapter.UserProductAdapter;
import com.example.electronics_store.retrofit.FavoriteManager;
import com.example.electronics_store.retrofit.ProductResponse;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserProductAdapter adapter; // Tái sử dụng adapter
    private List<ProductResponse> favoriteList;
    private Button btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_activity);

        // Ánh xạ view RecyclerView và Button
        recyclerView = findViewById(R.id.recyclerViewFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnContinueShopping = findViewById(R.id.btnContinueShopping);  // Ánh xạ Button

        // Load danh sách yêu thích từ SharedPreferences thông qua FavoriteManager
        favoriteList = FavoriteManager.getFavorites(this);


        // Thiết lập sự kiện cho nút "Tiếp tục mua hàng"
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteActivity.this, ProductListActivity.class);
            startActivity(intent);
            finish(); // Trở về trang chính
        });

        if (favoriteList != null && !favoriteList.isEmpty()) {
            boolean isWishlistScreen = true;
            // Tạo adapter và thiết lập cho RecyclerView
            FavoriteAdapter adapter = new FavoriteAdapter(favoriteList, this); // this = Activity context


            recyclerView.setAdapter(adapter);
        } else {
            // Nếu danh sách yêu thích rỗng, thông báo cho người dùng
            Toast.makeText(this, "Chưa có sản phẩm yêu thích!", Toast.LENGTH_SHORT).show();
        }

    }
}
