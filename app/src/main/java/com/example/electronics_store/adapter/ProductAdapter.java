package com.example.electronics_store.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.electronics_store.R;
import com.example.electronics_store.activity.EditProductActivity;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.ProductResponse;
import com.example.electronics_store.retrofit.RetrofitClient;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private List<ProductResponse> productList;
    private Context context;

    public ProductAdapter(List<ProductResponse> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductResponse product = productList.get(position);
        holder.tvId.setText("ID: " + product.getId());
        holder.tvCreatedAt.setText("Ngày tạo: " + product.getCreatedAt());
        holder.tvName.setText(product.getName());
        holder.tvDescription.setText(product.getDescription());
        String formattedPrice = formatCurrency(product.getPrice());
        holder.tvPrice.setText("Giá: " + formattedPrice + " VNĐ");
        holder.tvStock.setText("Số lượng trong kho: " + product.getStock());

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.ivProduct);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProductActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn có chắc muốn xóa sản phẩm này?");

            builder.setPositiveButton("Xóa", (dialog, which) -> {
                deleteProduct(product.getId(), position);
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProductList(List<ProductResponse> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        Button btnEdit,btnDelete;
        TextView tvId, tvCreatedAt, tvName, tvDescription, tvPrice, tvStock;
        ImageView ivProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private String formatCurrency(double amount) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###");
        return formatter.format(amount);
    }

    private void deleteProduct(int productId, int position) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.deleteProduct(productId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                    Toast.makeText(context, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
