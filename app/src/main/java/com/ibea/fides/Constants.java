package com.ibea.fides;

/**
 * Created by KincaidJ on 1/24/17.
 */

public class Constants {
    public static final String DB_NODE_USERS = "users";
    public static final String DB_NODE_SHIFTS = "shifts";
    public static final String DB_NODE_ORGANIZATIONS = "organizations";
    public static final String DB_NODE_TAGS = "tags";

    // Constants for fields. Reusable if fields are named same.
    //OID == Organization ID, as provided by the API

    //For shifts.
    public static final String DB_FIELD_FROM = "from";
    public static final String DB_FIELD_UNTIL = "until";
    public static final String DB_FIELD_DATE = "date";
    public static final String DB_FIELD_MAXVOLUNTEERS = "maxVolunteers";
    public static final String DB_FIELD_OID = "OID";
    public static final String DB_FIELD_DESCRIPTION = "description";
}
