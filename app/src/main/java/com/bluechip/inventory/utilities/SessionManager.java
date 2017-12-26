package com.bluechip.inventory.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {

    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "BlueChipLogin";

    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_AUDITOR_EMAIL = "userEmail";
    public static final String KEY_AUDITOR_ID = "userId";
    public static final String KEY_AUDITOR_JOB_TABLE_NAME = "defaut";
    public static final String KEY_MASTER_TABLE_NAME = "master";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    // String Value
    public void putString(String KEY, String value) {
        editor.putString(KEY, value);
        editor.commit();
    }

    public String getString(String keyUserId) {
        return pref.getString(keyUserId, "");
    }


    // int value
    public void putInt(String KEY, int value) {
        editor.putInt(KEY, value);
        editor.commit();
    }

    public int getInt(String keyUserId) {
        return pref.getInt(keyUserId,0);
    }



    public void clearSession() {

        editor.clear();
        editor.commit();
    }
}
