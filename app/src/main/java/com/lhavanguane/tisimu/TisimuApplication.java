package com.lhavanguane.tisimu;

import android.app.Application;

import com.lhavanguane.tisimu.utils.ThemeManager;

public class TisimuApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Apply saved theme preference on app start
        ThemeManager themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(themeManager.getThemePreference());
    }
}