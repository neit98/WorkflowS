package com.example.workflow_s.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.workflow_s.R;
import com.example.workflow_s.model.Organization;
import com.example.workflow_s.model.User;

/**
 * Workflow_S
 * Created by TinhPV on 2019-06-20
 * Copyright © 2019 TinhPV. All rights reserved
 **/


public class SharedPreferenceUtils {

    public static void saveCurrentUserData(Context context, User currentUser, Organization organization) {
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (null != currentUser) {
            editor.putString(context.getString(R.string.pref_userId), currentUser.getId());
            editor.putString(context.getString(R.string.pref_username), currentUser.getName());
            editor.putString(context.getString(R.string.pref_email), currentUser.getEmail());
            editor.putString(context.getString(R.string.pref_avatar), currentUser.getAvatar());
            editor.putString(context.getString(R.string.pref_token), currentUser.getToken());
        }

        if (null != organization) {
            editor.putString(context.getString(R.string.pref_orgId), String.valueOf(organization.getId()));
            editor.putString(context.getString(R.string.pref_orgName), organization.getName());
        }

        editor.commit();
    }
    public static void saveCurrentOrder(Context context, int orderContent) {
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.order),orderContent);
        editor.commit();
    }
    public static int retrieveDataInt(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    public static String retrieveData(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    public static void clearDataUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static void saveNotificationChange(Context context, int flag) {
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(context.getString(R.string.notify), flag);
        editor.commit();
    }

}
