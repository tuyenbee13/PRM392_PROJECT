package com.example.electronics_store.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronics_store.R;
import com.example.electronics_store.adapter.ChatAdapter;
import com.example.electronics_store.model.Message;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText editMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList;
    private final String OPENAI_API_KEY = ""; // Nhớ thêm key nếu dùng thật

    // Chế độ dùng thử không cần API
    private final boolean USE_MOCK_RESPONSE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerChat = findViewById(R.id.recyclerChat);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> {
            String userMessage = editMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addMessage(userMessage, "user");
                editMessage.setText("");

                if (USE_MOCK_RESPONSE) {
                    // Giả lập phản hồi từ GPT
                    recyclerChat.postDelayed(() -> {
                        String mockResponse = "Xin chào! Tôi là ChatGPT. Bạn cần hỗ trợ gì?";
                        addMessage(mockResponse, "gpt");
                    }, 1000); // Giả delay 1s cho giống thật
                } else {
                    sendMessageToGPT(userMessage);
                }
            }
        });
    }

    private void addMessage(String content, String sender) {
        messageList.add(new Message(content, sender));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerChat.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessageToGPT(String userMessage) {
        String url = "https://api.openai.com/v1/chat/completions";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", userMessage));
            jsonBody.put("messages", messages);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        String reply = response.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        addMessage(reply.trim(), "gpt");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi phản hồi từ GPT", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối GPT", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + OPENAI_API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
