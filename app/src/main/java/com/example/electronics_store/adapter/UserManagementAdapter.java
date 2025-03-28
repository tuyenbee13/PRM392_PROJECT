package com.example.electronics_store.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.R;
import com.example.electronics_store.activity.UpdateUserActivity;
import com.example.electronics_store.activity.UserManagementActivity;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.RetrofitClient;
import com.example.electronics_store.retrofit.UserResponse;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.ViewHolder> {
    private List<UserResponse> userList;

    public UserManagementAdapter(List<UserResponse> userList) {
        this.userList = (userList != null) ? userList : new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUserList(List<UserResponse> userList) {
        this.userList = (userList != null) ? userList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        UserResponse user = userList.get(position);
        holder.txtName.setText(user.getName() != null ? user.getName() : "N/A");
        holder.txtEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        holder.txtRole.setText(user.getRole() != null ? user.getRole() : "N/A");
        holder.txtStatus.setText(user.isBanned() ? "BANNED" : "ACTIVE");

        if (user.isBanned()) {
            holder.txtStatus.setTextColor(Color.RED);
            holder.btnBanUser.setVisibility(View.GONE);
            holder.btnUnbanUser.setVisibility(View.VISIBLE);
        } else {
            holder.txtStatus.setTextColor(Color.GREEN);
            holder.btnBanUser.setVisibility(View.VISIBLE);
            holder.btnUnbanUser.setVisibility(View.GONE);
        }

        holder.btnBanUser.setOnClickListener(v -> {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<Void> call = apiService.banUser(user.getId());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "Người dùng đã bị cấm", Toast.LENGTH_SHORT).show();
                        user.setBanned(true);
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(v.getContext(), "Lỗi khi cấm người dùng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(v.getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnUnbanUser.setOnClickListener(v -> {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<Void> call = apiService.unbanUser(user.getId());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "Người dùng đã được mở khóa", Toast.LENGTH_SHORT).show();
                        user.setBanned(false);
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(v.getContext(), "Lỗi khi mở khóa người dùng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(v.getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
        holder.btnUpdateUser.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UpdateUserActivity.class);
            intent.putExtra("USER_ID", user.getId());
            intent.putExtra("USER_NAME", user.getName());
            intent.putExtra("USER_PHONE", user.getPhoneNumber());
            intent.putExtra("USER_AVATAR", user.getAvatar());
            ((UserManagementActivity) v.getContext()).startActivityForResult(intent, 100);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail, txtRole, txtStatus;
        Button btnUpdateUser, btnBanUser, btnUnbanUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.User_txtName);
            txtEmail = itemView.findViewById(R.id.User_txtEmail);
            txtRole = itemView.findViewById(R.id.User_txtRole);
            btnUpdateUser = itemView.findViewById(R.id.btnUpdateUser);
            btnBanUser = itemView.findViewById(R.id.btnBanUser);
            btnUnbanUser = itemView.findViewById(R.id.btnUnbanUser);
            txtStatus = itemView.findViewById(R.id.User_txtStatus);
        }
    }
}
