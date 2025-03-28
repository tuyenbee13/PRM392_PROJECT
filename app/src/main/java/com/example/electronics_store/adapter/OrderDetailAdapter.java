package com.example.electronics_store.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.electronics_store.R;
import com.example.electronics_store.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private List<OrderDetail> orderDetailList;

    public OrderDetailAdapter(List<OrderDetail> orderDetailList) {
        this.orderDetailList = (orderDetailList != null) ? orderDetailList : new ArrayList<>();
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetailList.get(position);

        holder.txtProductName.setText(orderDetail.getProduct().getName());
        holder.txtProductDescription.setText(orderDetail.getProduct().getDescription());
        holder.txtProductPrice.setText(orderDetail.getPrice() + " VNĐ");
        holder.txtQuantity.setText("Số lượng: " + orderDetail.getQuantity());

        // Load hình ảnh từ URL bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(orderDetail.getProduct().getImageUrl())
                .placeholder(R.drawable.placeholder) // Ảnh mặc định nếu lỗi
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return (orderDetailList != null) ? orderDetailList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductDescription, txtProductPrice, txtQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.d_imgProduct);
            txtProductName = itemView.findViewById(R.id.d_txtProductName);
            txtProductDescription = itemView.findViewById(R.id.d_txtProductDescription);
            txtProductPrice = itemView.findViewById(R.id.d_txtProductTotalPrice);
            txtQuantity = itemView.findViewById(R.id.d_txtQuantity);
        }
    }
}
