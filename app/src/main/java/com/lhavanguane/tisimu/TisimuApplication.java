package com.lhavanguane.tisimu;

import android.app.Application;
import android.content.res.Configuration;

import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.utils.ThemeManager;

import java.util.Locale;

public class TisimuApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Apply saved language preference
        LanguageManager languageManager = LanguageManager.getInstance(this);
        languageManager.updateAppLanguage(this);

        // Apply saved theme preference
        ThemeManager themeManager = ThemeManager.getInstance(this);
        themeManager.applyTheme(themeManager.getThemePreference());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Re-apply language on configuration change
        LanguageManager languageManager = LanguageManager.getInstance(this);
        languageManager.updateAppLanguage(this);
    }
}