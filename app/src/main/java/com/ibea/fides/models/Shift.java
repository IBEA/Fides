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
    String address;
    String from;
    String until;
    String organizationID;
    String description;
    String shortDescription;
    String organizationName;
    int zip;
    String date;
    int maxVolunteers;
    List<String> currentVolunteers = new ArrayList<String>();

    public Shift(){}
    public Shift(String _from, String _until, String _date, String _description, String _shortDescription, int _maxVolunteers, String _OID, String _address, int _zip, String _organizationName){
        from = _from;
        until = _until;
        organizationID = _OID;
        description = _description;
        shortDescription = _shortDescription;
        date = _date;
        maxVolunteers = _maxVolunteers;
        address = _address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
