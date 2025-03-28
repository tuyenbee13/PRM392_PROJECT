package com.example.electronics_store.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.ApiService;
import com.example.electronics_store.retrofit.CategoryRequest;
import com.example.electronics_store.retrofit.CategoryResponse;
import com.example.electronics_store.retrofit.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryResponse> categoryList;
    private Context context;
    private ApiService apiService;

    public CategoryAdapter(List<CategoryResponse> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void setCategoryList(List<CategoryResponse> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryResponse category = categoryList.get(position);
        holder.categoryName.setText(category.getName());

        holder.btnEdit.setOnClickListener(v -> updateCategory(category, position));
        holder.btnDelete.setOnClickListener(v -> deleteCategory(category.getId(), position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    private void updateCategory(CategoryResponse category, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa danh mục");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_category, null);
        EditText etCategoryName = dialogView.findViewById(R.id.etCategoryName);
        etCategoryName.setText(category.getName());

        builder.setView(dialogView);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String updatedName = etCategoryName.getText().toString().trim();

            // Validation
            if (updatedName.isEmpty()) {
                Toast.makeText(context, "Tên danh mục không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (updatedName.length() < 3) {
                Toast.makeText(context, "Tên danh mục phải có ít nhất 3 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (updatedName.equals(category.getName())) {
                Toast.makeText(context, "Tên danh mục không thay đổi!", Toast.LENGTH_SHORT).show();
                return;
            }

            CategoryRequest categoryRequest = new CategoryRequest(updatedName);
            Call<Void> call = apiService.updateCategory(category.getId(), categoryRequest);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        category.setName(updatedName);
                        categoryList.set(position, category);
                        notifyItemChanged(position);
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi khi cập nhật danh mục!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteCategory(int categoryId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xóa danh mục");
        builder.setMessage("Bạn có chắc chắn muốn xóa danh mục này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<Void> call = apiService.deleteCategory(categoryId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        categoryList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi khi xóa danh mục!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        Button btnEdit, btnDelete;

        CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
