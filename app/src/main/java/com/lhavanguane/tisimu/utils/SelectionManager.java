package com.lhavanguane.tisimu.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SelectionManager {
    private static final String PREF_NAME = "TisimuPrefs";
    private static final String KEY_HAS_SELECTED_HYMNALS = "has_selected_hymnals";

    private static SelectionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SelectionManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SelectionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SelectionManager(context);
        }
        return instance;
    }

    public void setHasSelectedHymnals(boolean hasSelected) {
        editor.putBoolean(KEY_HAS_SELECTED_HYMNALS, hasSelected);
        editor.apply();
    }

    public boolean hasSelectedHymnals() {
        return sharedPreferences.getBoolean(KEY_HAS_SELECTED_HYMNALS, false);
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}
