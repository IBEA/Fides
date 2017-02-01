package com.ibea.fides.utils;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.ibea.fides.ui.LoginActivity;

/**
 * Created by KincaidJ on 1/24/17.
 */

public class Universal {
    // Logout User
    public static void logout(Context mContext) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }
}
