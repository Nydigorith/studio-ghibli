package com.example.firebase;

public class ReviewData {


    public ReviewData(String email, String review, String rating, String title, String date) {
        this.email = email;
        this.review = review;
        this.rating = rating;
        this.title = title;
        this.date = date;
    }

    private String email;
    private String review;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String rating;
    private String title;
    private String date;



}
