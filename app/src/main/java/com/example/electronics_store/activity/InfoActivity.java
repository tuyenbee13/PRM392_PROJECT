package com.example.electronics_store.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class InfoActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = findViewById(R.id.sl_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thông tin cửa hàng");

        drawerLayout = findViewById(R.id.sl_drawer_layout);

        NavigationView navigationView = findViewById(R.id.sl_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Đặt vị trí của cửa hàng (123 ABC Street, HCM)
        LatLng storeLocation = new LatLng(10.7769, 106.7009);
        mMap.addMarker(new MarkerOptions().position(storeLocation).title("Electronics Store"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, 15));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_user_order_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        } else if (id == R.id.nav_user_update_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_user_change_password) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (id == R.id.nav_user_product) {
            startActivity(new Intent(this, ProductListActivity.class));
        } else if (id == R.id.nav_user_store_location) {
            startActivity(new Intent(this, InfoActivity.class));
        } else if (id == R.id.nav_user_logout) {
            logout(); // Đóng ứng dụng hoặc xử lý đăng xuất
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        RetrofitClient.setAuthToken(null);

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        finish();
    }
}
