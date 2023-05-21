package com.example.firebase;

import android.graphics.Bitmap;

public class UserData {
    public UserData(String firstname, String lastname, String email, Bitmap picture) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.picture = picture;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    String firstname;
    String lastname;
    String email;
    Bitmap picture;
}
