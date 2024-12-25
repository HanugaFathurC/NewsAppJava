package com.example.newsapplication.model;

public class User {
    private String username;
    public  String fullName;
    private String gender;
    private String birthDate;
    private String password;

    public User() {
    }

    public User(String username, String fullName, String gender, String birthDate, String password) {
        this.username = username;
        this.fullName = fullName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
