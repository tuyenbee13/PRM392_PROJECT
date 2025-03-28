package com.example.electronics_store.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.Helper.DateUtils;
import com.example.electronics_store.R;
import com.example.electronics_store.retrofit.OrderResponse;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.List;
public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.ViewHolder> {
    private List<OrderResponse> orderList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(OrderResponse order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OrderManagementAdapter(List<OrderResponse> orderList) {
        this.orderList = orderList;
    }

    public void setOrderList(List<OrderResponse> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orderList.get(position);
//        holder.txtOrderId.setText("Mã đơn: " + order.getId());
//        holder.txtUserId.setText("Mã khách: " + order.getUserId());
//        holder.txtTotalPrice.setText("Tổng tiền: " + order.getTotalPrice() + " VNĐ");
//        holder.txtStatus.setText("Trạng thái: " + order.getStatus());
//        holder.txtCreatedAt.setText("Ngày tạo: " + order.getCreatedAt());
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtUserId, txtTotalPrice, txtStatus, txtCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.Order_txtId);
            txtUserId = itemView.findViewById(R.id.Order_txtUserId);
            txtTotalPrice = itemView.findViewById(R.id.Order_txtTotalPrice);
            txtStatus = itemView.findViewById(R.id.Order_txtStatus);
            txtCreatedAt = itemView.findViewById(R.id.Order_txtCreatedDate);
        }

        public void bind(OrderResponse order, OnItemClickListener listener) {
            txtOrderId.setText("Mã đơn: " + order.getId());
            txtUserId.setText("Mã khách: " + order.getUserId());
            txtTotalPrice.setText("Tổng tiền: " + order.getTotalPrice() + " VNĐ");
            txtStatus.setText("Trạng thái: " + order.getStatus());
            txtCreatedAt.setText("Ngày tạo đơn: " + DateUtils.formatDate(order.getCreatedAt()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(order);
                }
            });
        }
    }
}
