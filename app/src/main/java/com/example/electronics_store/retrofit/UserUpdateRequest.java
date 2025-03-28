package com.example.electronics_store.retrofit;

public class UserUpdateRequest {
    private String name;
    private String phoneNumber;
    private String avatar;

    public UserUpdateRequest(String name, String phoneNumber, String avatar) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

}
