package com.example.electronics_store.retrofit;

public class CategoryResponse {
    private int id;
    private String name;

    // ✅ Constructor không tham số (cần cho Retrofit/GSON)
    public CategoryResponse() {}

    // ✅ Constructor có tham số để tạo danh mục
    public CategoryResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; } // ✅ Sửa lại hàm này

    @Override
    public String toString() {
        return name; // Hiển thị tên danh mục trong Spinner
    }
}