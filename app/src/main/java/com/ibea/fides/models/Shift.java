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
    String from;
    String until;
    String organizationID;
    String description;
    String shortDescription;
    String organizationName;
    //TODO: Make string
    int zip;
    String date;
    int maxVolunteers;
    List<String> currentVolunteers = new ArrayList<String>();
    List<String> ratedVolunteers = new ArrayList<>();

    public Shift(){}
    public Shift(String _from, String _until, String _date, String _description, String _shortDescription, int _maxVolunteers, String _OID, String _address, String _city, String _state, int _zip, String _organizationName){
        from = _from;
        until = _until;
        organizationID = _OID;
        description = _description;
        shortDescription = _shortDescription;
        date = _date;
        maxVolunteers = _maxVolunteers;
        streetAddress = _address;
        city = _city;
        state = _state;
        zip = _zip;
        organizationName = _organizationName;
    }

    // Getters and Setters
    public String getPushId() { return pushId; }
    public void setPushId(String pushId) { this.pushId = pushId; }
    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUntil() {
        return until;
    }

    public void setUntil(String until) {
        this.until = until;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMaxVolunteers() {
        return maxVolunteers;
    }

    public void setMaxVolunteers(int maxVolunteers) {
        this.maxVolunteers = maxVolunteers;
    }

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

    public void addRated(String userId) { ratedVolunteers.add(userId); }
    public void addVolunteer(String userID){
        currentVolunteers.add(userID);
    }

    public void removeVolunteer(String userID){
        //Untested
        Log.v("Shift Model:", String.valueOf(currentVolunteers.size()));
        int index = currentVolunteers.indexOf(userID);
        currentVolunteers.remove(index);
        Log.v("Shift Model:", String.valueOf(currentVolunteers.size()));
    }
}
