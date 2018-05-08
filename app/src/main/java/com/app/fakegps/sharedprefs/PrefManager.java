package com.app.fakegps.sharedprefs;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.app.fakegps.BuildConfig;
import com.app.fakegps.model.FavLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jubayer on 6/6/2017.
 */


public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = BuildConfig.APPLICATION_ID;

    private static Gson GSON = new Gson();

    private static final String KEY_MOCK_SPEED = "num_of_time_user_set_alarm";
    private static final String KEY_SET_IS_APP_RUN_FIRST_TIME = "is_app_run_first_time";
    private static final String KEY_FAV_LOCATION = "fav_location";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    public void setMockSpeed(int obj) {
        editor = pref.edit();

        editor.putInt(KEY_MOCK_SPEED, obj);

        // commit changes
        editor.commit();
    }
    public int getMockSpeed() {
        return pref.getInt(KEY_MOCK_SPEED,20);
    }

    public void setIsAppRunFirstTime(boolean obj) {
        editor = pref.edit();

        editor.putBoolean(KEY_SET_IS_APP_RUN_FIRST_TIME, obj);

        // commit changes
        editor.commit();
    }
    public boolean getIsAppRunFirstTime() {
        return pref.getBoolean(KEY_SET_IS_APP_RUN_FIRST_TIME,true);
    }


    public void setFavLocations(List<FavLocation> obj) {
        editor = pref.edit();

        editor.putString(KEY_FAV_LOCATION, GSON.toJson(obj));

        // commit changes
        editor.commit();
    }

    public void setFavLocations(String obj) {
        editor = pref.edit();

        editor.putString(KEY_FAV_LOCATION, obj);

        // commit changes
        editor.commit();
    }


    public List<FavLocation> getFavLocations() {

        List<FavLocation> productFromShared = new ArrayList<>();

        String gson = pref.getString(KEY_FAV_LOCATION, "");

        if (gson.isEmpty()) return productFromShared;

        Type type = new TypeToken<List<FavLocation>>() {
        }.getType();
        productFromShared = GSON.fromJson(gson, type);

        return productFromShared;
    }


}