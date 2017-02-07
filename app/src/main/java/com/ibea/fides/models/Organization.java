package com.ibea.fides.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KincaidJ on 1/25/17.
 */

@Parcel
public class Organization {
    String pushId;
    String name;
    String contactName;
    String streetAddress;
    String cityAddress;
    String stateAddress;
    String contactEmail;
    String zipcode;
    String description;
    List<String> shiftsAvailable = new ArrayList<>();
    List<String> shiftsCompleted = new ArrayList<>();
    List<String> tags = new ArrayList<>();


    // Empty Constructor for Parceler
    public Organization() {}

    // Basic Constructor
    public Organization(String pushId, String orgName, String contactName, String streetAddress, String cityAddress, String stateAddress, String zip, String description) {
        this.pushId = pushId;
        this.name = orgName;
        this.contactName = contactName;
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.stateAddress = stateAddress;
        this.zipcode = zip;
        this.description = description;
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
    public void setName(String orgName) {
        this.name = orgName;
    }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getStreetAddress() {
        return streetAddress;
    }
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
    public String getCityAddress() {
        return cityAddress;
    }
    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }
    public String getStateAddress() {
        return stateAddress;
    }
    public void setStateAddress(String stateAddress) { this.stateAddress = stateAddress; }
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
    public void setShiftsAvailable(List<String> shiftsAvailable) { this.shiftsAvailable = shiftsAvailable; }
    public List<String> getShiftsCompleted() {
        return shiftsCompleted;
    }
    public void setShiftsCompleted(List<String> shiftsCompleted) { this.shiftsCompleted = shiftsCompleted; }
    public String getContactEmail() {
        return contactEmail;
    }
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    // Shift Functions
    public void addShift(String shiftToAdd) {
        this.shiftsAvailable.add(shiftToAdd);
    }
    public void completeShift(String pushId) {
        for(String shift : this.shiftsAvailable) {
            if(shift.equals(pushId)) {
                shiftsAvailable.remove(shift);
                shiftsCompleted.add(shift);
            }
        }
    }
}