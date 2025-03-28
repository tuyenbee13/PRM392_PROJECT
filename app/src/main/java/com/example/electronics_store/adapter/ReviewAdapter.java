package com.example.electronics_store.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electronics_store.model.Review;
import com.example.electronics_store.R;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.username.setText(review.getUser() != null ? review.getUser().getYourName() : "Ẩn danh");
        holder.comment.setText(review.getComment());
        holder.ratingBar.setRating(parseRating(review.getRating()));
        holder.createdAt.setText(review.getCreatedAt());
    }

    private float parseRating(String rating) {
        if (rating == null) return 0.0f;

        try {
            return Float.parseFloat(rating);
        } catch (NumberFormatException e) {
            switch (rating.toUpperCase()) {
                case "ONE": return 1.0f;
                case "TWO": return 2.0f;
                case "THREE": return 3.0f;
                case "FOUR": return 4.0f;
                case "FIVE": return 5.0f;
                default: return 0.0f;
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, comment, createdAt;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tv_username);
            comment = itemView.findViewById(R.id.tv_comment);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            createdAt = itemView.findViewById(R.id.tv_created_at);
        }
    }
}