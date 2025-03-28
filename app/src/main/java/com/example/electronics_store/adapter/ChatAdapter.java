package com.example.electronics_store.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.electronics_store.R;
import com.example.electronics_store.model.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Message> messageList;
    private final int USER = 0;
    private final int GPT = 1;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSender().equals("user") ? USER : GPT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == USER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_gpt, parent, false);
            return new GptViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).txtMessage.setText(message.getContent());
        } else {
            ((GptViewHolder) holder).txtMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        UserViewHolder(View view) {
            super(view);
            txtMessage = view.findViewById(R.id.txtUserMessage);
        }
    }

    static class GptViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        GptViewHolder(View view) {
            super(view);
            txtMessage = view.findViewById(R.id.txtGptMessage);
        }
    }
}

