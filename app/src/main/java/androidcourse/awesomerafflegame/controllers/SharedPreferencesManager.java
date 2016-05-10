package androidcourse.awesomerafflegame.controllers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jesper on 09/05/16.
 */
public class SharedPreferencesManager {
    private static final String SHARED_TAG = "snake_eyes_shared_prefs";
    private static final String VERSION_TAG = "version";
    private static final String FIRST_TIME_TAG = "firsttime";

    private static SharedPreferencesManager instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(SHARED_TAG, 0);
        editor = preferences.edit();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
    }

    public static SharedPreferencesManager get() {
        return instance;
    }

    public void setVersionName(String version){
        editor.putString(VERSION_TAG, version);
    }

    public String getVersionName(){
       return preferences.getString(VERSION_TAG, "");
    }

    public void setFirstTimeUser(boolean firstTimeUser){
        editor.putBoolean(FIRST_TIME_TAG, firstTimeUser);
    }

    public boolean getFirstTimeUser(){
       return preferences.getBoolean(FIRST_TIME_TAG, true);
    }
}
