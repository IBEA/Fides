package com.ibea.fides.models;

import org.parceler.Parcel;

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

    // Empty Constructor for Parceler
    public User() {}

    // Basic Constructor
    public User(String pushId, String name) {
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

}
