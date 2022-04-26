package com.example.easybookshare.models;

public class UserModel
{
    String uniqueKey;
    String uName,email,password,city;
    String phoneNumber;

    public UserModel(){}

    public UserModel(String uniqueKey, String uName, String email, String password,String city,String phoneNumber)
    {
        this.uniqueKey = uniqueKey;
        this.uName = uName;
        this.email = email;
        this.password = password;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey)
    {
        this.uniqueKey = uniqueKey;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
