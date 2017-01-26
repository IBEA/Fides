package com.ibea.fides.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KincaidJ on 1/25/17.
 */

@Parcel
public class Organization {
    String pushID;
    String name;
    String address;
    String zipcode;
    String description;
    List<String> shiftsAvailable = new ArrayList<>();
    List<String> shiftsCompleted = new ArrayList<>();

    // Empty Constructor for Parceler
    public Organization() {}

    // Basic Constructor
    public Organization(String name) {
        this.name = name;
    }

    // Getters and Setters

    public String getPushID() {
        return pushID;
    }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getShiftsAvailable() {
        return shiftsAvailable;
    }

    public void setShiftsAvailable(List<String> shiftsAvailable) {
        this.shiftsAvailable = shiftsAvailable;
    }

    public List<String> getShiftsCompleted() {
        return shiftsCompleted;
    }

    public void setShiftsCompleted(List<String> shiftsCompleted) {
        this.shiftsCompleted = shiftsCompleted;
    }

    // Shift Functions
    public void addShift(String shiftToAdd) {
        this.shiftsAvailable.add(shiftToAdd);
    }

    public void completeShift(String pushID) {
        for(String shift : this.shiftsAvailable) {
            if(shift.equals(pushID)) {
                shiftsAvailable.remove(shift);
                shiftsCompleted.add(shift);
            }
        }
    }
}