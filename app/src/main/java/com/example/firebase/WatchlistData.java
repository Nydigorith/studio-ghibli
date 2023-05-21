package com.example.firebase;

public class WatchlistData {


    public WatchlistData(String email, String title, String poster) {
        this.email = email;
        this.title = title;
        this.poster = poster;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    String email;
    String title;
    String poster;
}
