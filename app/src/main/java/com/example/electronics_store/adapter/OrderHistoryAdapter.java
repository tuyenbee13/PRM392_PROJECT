package com.example.electronics_store.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.Helper.DateUtils;
import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.OrderResponse;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private List<OrderResponse> orderList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OrderResponse order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OrderHistoryAdapter(List<OrderResponse> orderList) {
        this.orderList = orderList;
    }

    public void setOrderList(List<OrderResponse> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);
//        holder.tvOrderId.setText("Mã đơn hàng: " + order.getId());
//        holder.tvStatus.setText("Trạng thái: " + order.getStatus());
//        holder.tvTotalPrice.setText("Tổng tiền: " + order.getTotalPrice() + " VNĐ");
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrderList(List<OrderResponse> newOrders) {
        this.orderList.clear(); // Xóa dữ liệu cũ
        this.orderList.addAll(newOrders); // Thêm dữ liệu mới
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvTotalPrice, tvCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.oh_tvOrderId);
            tvStatus = itemView.findViewById(R.id.oh_tvStatus);
            tvTotalPrice = itemView.findViewById(R.id.oh_tvTotalPrice);
            tvCreatedAt = itemView.findViewById(R.id.oh_txtCreatedDate);
        }

        public void bind(OrderResponse order, OnItemClickListener listener) {
            tvOrderId.setText("Mã đơn hàng: " + order.getId());
            tvTotalPrice.setText("Tổng tiền: " + order.getTotalPrice() + " VNĐ");
            tvStatus.setText("Trạng thái: " + order.getStatus());
            tvCreatedAt.setText("Ngày đặt hàng: " + DateUtils.formatDate(order.getCreatedAt()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(order);
                }
            });
        }
    }
}
