package com.example.firebase;

public class FilmData {
    String poster;
    String title;

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FilmData(String poster, String title) {
        this.poster = poster;
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public String getTitle() {
        return title;
    }
}
