package com.example.electronics_store.retrofit;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductResponse implements Parcelable {
    private int id;
    private String createdAt;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String imageUrl;

    // Thêm thuộc tính quantity
    private int quantity;

    public ProductResponse(int id, String createdAt, String name, String description, double price, int stock, String imageUrl) {
        this.id = id;
        this.createdAt = createdAt;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.quantity = 1; // Mặc định số lượng là 1 khi thêm vào giỏ hàng
    }

    // Getters
    public int getId() { return id; }
    public String getCreatedAt() { return createdAt; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Parcelable
    protected ProductResponse(Parcel in) {
        id = in.readInt();
        createdAt = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        stock = in.readInt();
        imageUrl = in.readString();
        quantity = in.readInt(); // Đọc quantity từ Parcel
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(createdAt);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeDouble(price);
        parcel.writeInt(stock);
        parcel.writeString(imageUrl);
        parcel.writeInt(quantity); // Ghi quantity vào Parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductResponse> CREATOR = new Creator<ProductResponse>() {
        @Override
        public ProductResponse createFromParcel(Parcel in) {
            return new ProductResponse(in);
        }

        @Override
        public ProductResponse[] newArray(int size) {
            return new ProductResponse[size];
        }
    };
}
