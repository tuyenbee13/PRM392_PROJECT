package com.example.electronics_store.model;

public class User {

    private String yourName;
    private String emailAddress;
    private String phone;
    private String gender;
    private String password;
    private String role;

    public User(String yourName, String emailAddress, String phone, String gender, String password, String role) {
        this.yourName = yourName;
        this.emailAddress = emailAddress;
        this.phone = phone;
        this.gender = gender;
        this.password = password;
        this.role = role;
    }

    // Constructor đơn giản để tạo user "Ẩn danh"
    public User(String yourName) {
        this.yourName = yourName;
        this.emailAddress = "";
        this.phone = "";
        this.gender = "";
        this.password = "";
        this.role = "GUEST"; // Gán một vai trò mặc định
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getYourName() {
        return yourName;
    }

    public void setYourName(String yourName) {
        this.yourName = yourName;
    }
}
