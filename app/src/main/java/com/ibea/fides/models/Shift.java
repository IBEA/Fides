package com.ibea.fides.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alaina Traxler on 1/25/2017.
 */

public class Shift {
    private String from;
    private String until;
    private String OID;
    private String description;
    private String date;
    private int maxVolunteers;
    private List<String> currentVolunteers = new ArrayList<String>();

    public Shift(){}
    public Shift(String _from, String _until, String _date, String _description, int _maxVolunteers, String _OID){
        from = _from;
        until = _until;
        OID = _OID;
        description = _description;
        date = _date;
        maxVolunteers = _maxVolunteers;
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

    public String getOID() {
        return OID;
    }

    public void setOID(String OID) {
        this.OID = OID;
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
}
