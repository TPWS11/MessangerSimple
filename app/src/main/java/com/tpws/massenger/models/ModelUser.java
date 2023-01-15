package com.tpws.massenger.models;

public class ModelUser {
    String uid, name, email, password, confirm, imageUri, status;

    public ModelUser() {}

    public ModelUser(String uid, String name, String email, String password, String imageUri, String status) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageUri = imageUri;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}