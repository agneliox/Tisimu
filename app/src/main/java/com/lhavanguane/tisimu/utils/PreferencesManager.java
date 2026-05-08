package com.lhavanguane.tisimu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String KEY_SELECTED_HYMNAL_IDS = "selected_hymnal_ids";
    private static final String KEY_LAST_MANIFEST_UPDATE = "last_manifest_update";

    private static PreferencesManager instance;
    private SharedPreferences prefs;

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

    public boolean isHymnalSelected(String hymnalId) {
        return getSelectedHymnals().contains(hymnalId);
    }

    public void clearSelectedHymnals() {
        prefs.edit().remove(KEY_SELECTED_HYMNAL_IDS).apply();
    }

    public void setLastManifestUpdate(long timestamp) {
        prefs.edit().putLong(KEY_LAST_MANIFEST_UPDATE, timestamp).apply();
    }

    public long getLastManifestUpdate() {
        return prefs.getLong(KEY_LAST_MANIFEST_UPDATE, 0);
    }

    public boolean shouldUpdateManifest() {
        long lastUpdate = getLastManifestUpdate();
        long now = System.currentTimeMillis();
        // Update every 24 hours
        return (now - lastUpdate) > 24 * 60 * 60 * 1000;
    }
}