package com.ibea.fides.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KincaidJ on 1/24/17.
 */

@Parcel
public class Volunteer {
    String userId;
    String name;
    String email;
    boolean isAdmin = false;
    int rating;
    double hours;
    List<Integer> ratingHistory = new ArrayList<>();
    int absences;
    String zipcode;
    String city;
    String state;
    String about;

    // Empty Constructor for Parceler
    public Volunteer() {}

    // Basic Constructor
    public Volunteer(String _pushId, String _name, String _email, String _zipcode, String _city, String _state) {
        this.userId = _pushId;
        this.name = _name;
        this.email = _email;
        zipcode = _zipcode;
        city = _city;
        state = _state;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<Integer> getRatingHistory() {
        return ratingHistory;
    }

    public void setRatingHistory(List<Integer> ratingHistory) {
        this.ratingHistory = ratingHistory;
    }

    public int getAbsences() {
        return absences;
    }

    public void setAbsences(int absences) {
        this.absences = absences;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}
