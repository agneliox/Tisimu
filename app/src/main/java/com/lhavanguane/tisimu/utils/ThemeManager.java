package com.lhavanguane.tisimu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeManager {
    private static final String KEY_THEME = "app_theme";

    // Theme modes
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    private static ThemeManager instance;
    private SharedPreferences prefs;

    private ThemeManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context.getApplicationContext());
        }
        return instance;
    }

    public void applyTheme(int themeMode) {
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public void saveThemePreference(int themeMode) {
        prefs.edit().putInt(KEY_THEME, themeMode).apply();
        applyTheme(themeMode);
    }

    public int getThemePreference() {
        return prefs.getInt(KEY_THEME, THEME_SYSTEM);
    }

    public String getThemeName() {
        switch (getThemePreference()) {
            case THEME_LIGHT:
                return "Light";
            case THEME_DARK:
                return "Dark";
            case THEME_SYSTEM:
                return "System Default";
            default:
                return "System Default";
        }
    }

    public boolean isDarkMode() {
        int themeMode = getThemePreference();
        if (themeMode == THEME_SYSTEM) {
            int nightMode = AppCompatDelegate.getDefaultNightMode();
            return nightMode == AppCompatDelegate.MODE_NIGHT_YES;
        }
        return themeMode == THEME_DARK;
    }
}