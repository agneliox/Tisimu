package com.lhavanguane.tisimu.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.utils.Constants;
import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.utils.ThemeManager;

public class SettingsActivity extends AppCompatActivity {
    private View layoutSendFeedback;
    private View layoutRateApp;
    private View layoutShareApp;
    private View layoutAbout;
    private TextView tvAppVersion;

    private LanguageManager languageManager;
    private ThemeManager themeManager;
    private TextView tvCurrentLanguage;
    private RadioGroup radioGroupTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply language before super.onCreate and setContentView
        LanguageManager.getInstance(this).updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        setupToolbar();
        setupLanguageDisplay();
        setupThemeRadioGroup();
        setupVersionInfo();
    }

    private void setupToolbar() {
    }

    private void initViews() {
        layoutSendFeedback = findViewById(R.id.layoutSendFeedback);
        layoutRateApp = findViewById(R.id.layoutRateApp);
        layoutShareApp = findViewById(R.id.layoutShareApp);
        layoutAbout = findViewById(R.id.layoutAbout);
        tvAppVersion = findViewById(R.id.tvAppVersion);
        languageManager = LanguageManager.getInstance(this);
        themeManager = ThemeManager.getInstance(this);
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
    }

    private void setupClickListeners() {
        layoutSendFeedback.setOnClickListener(v -> openFeedbackForm());
        layoutRateApp.setOnClickListener(v -> openRateDialog());
        layoutShareApp.setOnClickListener(v -> shareApp());
        layoutAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void setupThemeRadioGroup() {
        int currentTheme = themeManager.getThemePreference();

        switch (currentTheme) {
            case ThemeManager.THEME_LIGHT:
                radioGroupTheme.check(R.id.radioLight);
                break;
            case ThemeManager.THEME_DARK:
                radioGroupTheme.check(R.id.radioDark);
                break;
            case ThemeManager.THEME_SYSTEM:
            default:
                radioGroupTheme.check(R.id.radioSystem);
                break;
        }

        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int newTheme;
            if (checkedId == R.id.radioLight) {
                newTheme = ThemeManager.THEME_LIGHT;
            } else if (checkedId == R.id.radioDark) {
                newTheme = ThemeManager.THEME_DARK;
            } else {
                newTheme = ThemeManager.THEME_SYSTEM;
            }

            if (newTheme != themeManager.getThemePreference()) {
                themeManager.saveThemePreference(newTheme);
            }
        });
    }

    private void openFeedbackForm() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FEEDBACK_FORM_URL));
        startActivity(browserIntent);
        Toast.makeText(this,
                "Please include your device model and Android version in the feedback",
                Toast.LENGTH_LONG).show();
    }

    private void openRateDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Rate Tisimu")
                .setMessage("If you enjoy using Tisimu, please take a moment to rate it. Your feedback helps us improve!")
                .setPositiveButton("Rate Now", (dialog, which) -> {
                    openFeedbackForm();
                })
                .setNegativeButton("Later", null)
                .show();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tisimu - Gospel Hymnal Reader");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Check out Tisimu! A beautiful gospel hymnal reader with communities, daily verses, and offline hymnals.\n\n" +
                        "Download it here: https://github.com/agneliox/Tisimu\n\n" +
                        "Share your feedback: " + Constants.FEEDBACK_FORM_URL);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About Tisimu")
                .setMessage("Tisimu is a gospel hymnal reader that allows you to download and read hymnals offline, " +
                        "join communities, and receive daily verses.\n\n" +
                        "Developed with ❤️ for gospel music lovers.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupLanguageDisplay() {
        String currentLangCode = languageManager.getCurrentLanguage();
        String languageName = languageManager.getLanguageName(currentLangCode);
        tvCurrentLanguage.setText(languageName);
    }

    private void setupVersionInfo() {
        tvAppVersion.setText(getString(R.string.version_1_0_1));
    }
}