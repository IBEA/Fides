package com.ibea.fides.models;

import org.parceler.Parcel;

/**
 * Created by KincaidJ on 1/25/17.
 */

@Parcel
public class Organization {
    String pushID;
    String name;

    // Empty Constructor for Parceler
    public Organization() {}

    // Basic Constructor
    public Organization(String pushId, String name) {
        this.pushId = pushId;
        this.name = name;
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
}
