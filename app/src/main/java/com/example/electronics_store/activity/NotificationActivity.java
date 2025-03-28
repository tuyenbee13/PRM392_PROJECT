package com.example.electronics_store.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.electronics_store.R;

public class NotificationActivity extends AppCompatActivity {
    private TextView notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationText = findViewById(R.id.notificationText);

        // Lấy thông báo từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("OrderPrefs", MODE_PRIVATE);
        String notification = sharedPreferences.getString("lastOrderNotification", "Không có thông báo.");

        // Hiển thị thông báo
        notificationText.setText(notification);
    }
}
