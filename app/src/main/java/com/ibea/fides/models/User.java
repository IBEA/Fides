package com.ibea.fides.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KincaidJ on 1/24/17.
 */

@Parcel
public class User {
    String pushId;
    String name;
    String email;
    boolean isOrganization = false;
    boolean isAdmin = false;
    int rating;
    double hours;
    List<Integer> ratingHistory = new ArrayList<>();
    int absences;

    // Empty Constructor for Parceler
    public User() {}

    // Basic Constructor
    public User(String pushId, String name, String email) {
        this.pushId = pushId;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getPushId() {
        return pushId;
    }
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean getIsOrganization() { return isOrganization; }
    public void setIsOrganization(boolean isOrganization) {
        this.isOrganization = isOrganization;
    }
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
}
