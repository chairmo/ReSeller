package com.android.reseller.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String uid;
    private String phoneNumber;
    private String city;
    private String state;
    private String photo;
    private double rating;
    private int ratingCount;
    private List<Object> posts;

    // default constructor, do nothing
    public User() {}

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
        this.uid = "";
        this.phoneNumber = "";
        this.city = "";
        this.state = "";
        this.photo = "";
        this.rating = 0.0;
        this.ratingCount = 0;
        this.posts = new ArrayList<>();
    }

    // Getters
    public String getFullName(){return firstName + " " + lastName;}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPhoto() {
        return photo;
    }

    public double getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public List<Object> getPosts() {
        return posts;
    }

    // Setters
    public void setFullName(String newName) {
        fullName = newName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String newEmail) {
        email = newEmail;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPhoneNumber(String newPhone) {
        phoneNumber = newPhone;
    }

    public void setCity(String newCity) {
        city = newCity;
    }

    public void setState(String newState) {
        state = newState;
    }

    public void setPhoto(String newPhoto) {
        photo = newPhoto;
    }

    // completely overwrites rating
    public void setRating(double newRating) {

    //    rating = newRating;
     rating = addRating(newRating);
    }

    // adds a new rating (calculate average)
    double addRating(double newRating) {
        // get total of all old ratings (multiply current average rating by # of ratings
        double oldRatingTotal = rating * ratingCount;

        // increment rating count
        ratingCount++;

        // add new rating to the total, then divide it by the new rating count to get new average
        double newRatingTotal = oldRatingTotal + newRating;
        return newRatingTotal / ratingCount;
    }

}


