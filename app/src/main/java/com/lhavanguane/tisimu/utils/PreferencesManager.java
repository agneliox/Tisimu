package com.lhavanguane.tisimu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String KEY_SELECTED_HYMNAL_IDS = "selected_hymnal_ids";

    private static PreferencesManager instance;
    private final SharedPreferences prefs;

    private PreferencesManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context.getApplicationContext());
        }
        return instance;
    }

    public void addSelectedHymnal(String hymnalId) {
        Set<String> selected = getSelectedHymnals();
        selected.add(hymnalId);
        Log.d("PreferencesManager", "Adding hymnal ID: " + hymnalId);
        prefs.edit().putStringSet(KEY_SELECTED_HYMNAL_IDS, selected).apply();
    }

    public void removeSelectedHymnal(String hymnalId) {
        Set<String> selected = getSelectedHymnals();
        selected.remove(hymnalId);
        prefs.edit().putStringSet(KEY_SELECTED_HYMNAL_IDS, selected).apply();
    }

    public Set<String> getSelectedHymnals() {
        return prefs.getStringSet(KEY_SELECTED_HYMNAL_IDS, new HashSet<>());
    }

    public void clearSelectedHymnals() {
        prefs.edit().remove(KEY_SELECTED_HYMNAL_IDS).apply();
    }

    public boolean isHymnalSelected(String hymnalId) {
        return getSelectedHymnals().contains(hymnalId);
    }
}