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
    String ein;
    String contactName;
    String address;
    String contactEmail;
    String zipcode;
    String description;
    List<String> shiftsAvailable = new ArrayList<>();
    List<String> shiftsCompleted = new ArrayList<>();


    // Empty Constructor for Parceler
    public Organization() {}

    // Basic Constructor
    public Organization(String pushId, String orgName, String ein, String contactName, String address, String zip, String description) {
        this.pushId = pushId;
        this.name = orgName;
        this.ein = ein;
        this.contactName = contactName;
        this.address = address;
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
    public String getEin() { return ein; }
    public void setEin(String ein) { this.ein = ein; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
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