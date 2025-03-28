package com.example.electronics_store.retrofit;

public class UpdateProfileRequest {
    private String name;
    private String phoneNumber;
    private String avatar;
    private String gender;

    public UpdateProfileRequest(String name, String phoneNumber, String avatar, String gender) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.gender = gender;
    }
    public UpdateProfileRequest(String name, String phone, String gender) {
        this.name = name;
        this.phoneNumber = phone;
        this.gender = gender;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}