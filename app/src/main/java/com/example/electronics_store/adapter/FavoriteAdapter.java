package com.example.electronics_store.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.FavoriteManager;
import com.example.electronics_store.retrofit.ProductResponse;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<ProductResponse> favoriteList;
    private Context context;

    public FavoriteAdapter(List<ProductResponse> favoriteList, Context context) {
        this.favoriteList = favoriteList;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        ProductResponse product = favoriteList.get(position);

        holder.txtProductName.setText(product.getName());
        holder.txtProductPrice.setText(String.format("%.0fđ", product.getPrice()));

        // Load ảnh sản phẩm với Glide
        Glide.with(context).load(product.getImageUrl()).into(holder.imgProduct);

        // Xử lý sự kiện nút Xóa với hộp thoại xác nhận
        holder.btnRemoveFavorite.setOnClickListener(v -> {
            // Kiểm tra nếu context có phải là Activity không
            if (context instanceof android.app.Activity) {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa sản phẩm này khỏi danh sách yêu thích?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            FavoriteManager.removeFavorite(context, product.getId());
                            favoriteList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, favoriteList.size());
                            Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(context, "Lỗi: Không thể hiển thị hộp thoại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtProductPrice;
        ImageView imgProduct;
        Button btnRemoveFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
        }
    }
}
