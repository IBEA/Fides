package com.ibea.fides.models;

import android.util.Log;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alaina Traxler on 1/25/2017.
 */

@Parcel
public class Shift {
    String pushId;
    String streetAddress;
    String city;
    String state;
    String startTime;
    String endTime;
    String organizationID;
    String description;
    String shortDescription;
    String organizationName;
    String zip;
    String startDate;
    String endDate;
    int maxVolunteers;
    int minTrust;
    List<String> currentVolunteers = new ArrayList<String>();
    List<String> ratedVolunteers = new ArrayList<>();
    Boolean complete = false;

    public Shift(){}
    public Shift(String _from, String _until, String _startDate, String _endDate, String _description, String _shortDescription, int _maxVolunteers, int _minTrust, String _OID, String _address, String _city, String _state, String _zip, String _organizationName){
        startTime = _from;
        endTime = _until;
        organizationID = _OID;
        description = _description;
        shortDescription = _shortDescription;
        startDate = _startDate;
        endDate = _endDate;
        maxVolunteers = _maxVolunteers;
        minTrust = _minTrust;
        streetAddress = _address;
        city = _city;
        state = _state;
        zip = _zip;
        organizationName = _organizationName;
    }

    // Getters and Setters
    public String getPushId() { return pushId; }
    public void setPushId(String pushId) { this.pushId = pushId; }
    public String getZip() {
        return zip;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String address) {
        this.streetAddress = address;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(String OID) {
        this.organizationID = OID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxVolunteers() {
        return maxVolunteers;
    }

    public void setMaxVolunteers(int maxVolunteers) {
        this.maxVolunteers = maxVolunteers;
    }

    public int getMinTrust() { return minTrust; }

    public void setMinTrust(int minTrust) { this.minTrust = minTrust; }

    public List<String> getCurrentVolunteers() {
        return currentVolunteers;
    }

    public void setCurrentVolunteers(List<String> currentVolunteers) {
        this.currentVolunteers = currentVolunteers;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public List<String> getRatedVolunteers() {
        return ratedVolunteers;
    }

    public void setRatedVolunteers(List<String> ratedVolunteers) {
        this.ratedVolunteers = ratedVolunteers;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public void addRated(String userId) { ratedVolunteers.add(userId); }
    public void addVolunteer(String userID){
        currentVolunteers.add(userID);
    }

    public void removeVolunteer(String userID){

        Log.v("Shift Model:", String.valueOf(currentVolunteers.size()));
        int index = currentVolunteers.indexOf(userID);
        currentVolunteers.remove(index);
        Log.v("Shift Model:", String.valueOf(currentVolunteers.size()));
    }
}
