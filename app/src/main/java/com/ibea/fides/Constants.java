package com.ibea.fides;

/**
 * Created by KincaidJ on 1/24/17.
 */

public class Constants {
    //Base nodes
    public static final String DB_NODE_USERS = "users";
    public static final String DB_NODE_SHIFTS = "shifts";
    public static final String DB_NODE_ORGANIZATIONS = "organizations";
    public static final String DB_NODE_APPLICATIONS = "pendingOrganizations";
    public static final String DB_NODE_TAGS = "tags";
    public static final String DB_NODE_SHIFTSAVAILABLE = "shiftsAvailable";
    public static final String DB_NODE_SHIFTSPENDING = "shiftsPending";
    public static final String DB_NODE_SHIFTSCOMPLETE = "shiftsComplete";

    // Sub-nodes
    public static final String DB_SUBNODE_ZIPCODE = "zipcode";
    public static final String DB_SUBNODE_ORGANIZATIONS = "organizations";
    public static final String DB_SUBNODE_VOLUNTEERS = "volunteers";
    public static final String DB_SUBNODE_STATECITY = "stateCity";

    //Keys
    public static final String KEY_ISORGANIZATION = "isOrganization";
}
