package com.lhavanguane.tisimu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageManager {
    private static final String KEY_LANGUAGE = "app_language";
    private static final String DEFAULT_LANGUAGE = "en";

    private static LanguageManager instance;
    private SharedPreferences prefs;

    // Supported languages
    public static final String[] SUPPORTED_LANGUAGES = {"en", "pt", "es", "ts"};
    public static final String[] LANGUAGE_NAMES = {"English", "Português", "Español", "Xitsonga"};

    private LanguageManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized LanguageManager getInstance(Context context) {
        if (instance == null) {
            instance = new LanguageManager(context.getApplicationContext());
        }
        return instance;
    }

    public String getCurrentLanguage() {
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    public void setLanguage(Context context, String languageCode) {
        if (!isLanguageSupported(languageCode)) {
            languageCode = DEFAULT_LANGUAGE;
        }

        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
        applyLanguage(context, languageCode);
    }

    private boolean isLanguageSupported(String languageCode) {
        for (String lang : SUPPORTED_LANGUAGES) {
            if (lang.equals(languageCode)) {
                return true;
            }
        }
        return false;
    }

    public void applyLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
        } else {
            config.locale = locale;
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void updateAppLanguage(Context context) {
        String currentLanguage = getCurrentLanguage();
        applyLanguage(context, currentLanguage);
    }

    public String getLanguageName(String languageCode) {
        for (int i = 0; i < SUPPORTED_LANGUAGES.length; i++) {
            if (SUPPORTED_LANGUAGES[i].equals(languageCode)) {
                return LANGUAGE_NAMES[i];
            }
        }
        return "English";
    }
}