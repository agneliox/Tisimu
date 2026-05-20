package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.BuildConfig;
import com.lhavanguane.tisimu.ui.activities.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.utils.Constants;
import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.utils.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private SwitchMaterial switchDarkMode;
    private Spinner spinnerLanguage;
    private MaterialButton btnSendFeedback;
    private MaterialButton btnRateApp;
    private MaterialButton btnShareApp;
    private MaterialButton btnAbout;

    private LanguageManager languageManager;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        languageManager = LanguageManager.getInstance(this);
        themeManager = ThemeManager.getInstance(this);

        initViews();
        setupToolbar();
        setupLanguageSpinner();
        setupDarkModeSwitch();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.settingsToolbar);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        btnSendFeedback = findViewById(R.id.btnSendFeedback);
        btnRateApp = findViewById(R.id.btnRateApp);
        btnShareApp = findViewById(R.id.btnShareApp);
        btnAbout = findViewById(R.id.btnAbout);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                LanguageManager.LANGUAGE_NAMES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        String currentLang = languageManager.getCurrentLanguage();
        for (int i = 0; i < LanguageManager.SUPPORTED_LANGUAGES.length; i++) {
            if (LanguageManager.SUPPORTED_LANGUAGES[i].equals(currentLang)) {
                spinnerLanguage.setSelection(i);
                break;
            }
        }

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String newLanguage = LanguageManager.SUPPORTED_LANGUAGES[position];
                if (!newLanguage.equals(languageManager.getCurrentLanguage())) {
                    showLanguageChangeDialog(newLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupDarkModeSwitch() {
        boolean isDarkMode = themeManager.isDarkMode();
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showThemeRestartDialog(isChecked);
        });
    }

    private void showThemeRestartDialog(boolean enableDarkMode) {
        String modeName = enableDarkMode ? "Dark Mode" : "Light Mode";

        new AlertDialog.Builder(this)
                .setTitle("Change Theme")
                .setMessage("The app will restart to apply " + modeName + ". Continue?")
                .setPositiveButton("Apply", (dialog, which) -> {
                    themeManager.setDarkMode(enableDarkMode);
                    restartApp();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    switchDarkMode.setChecked(!enableDarkMode);
                })
                .show();
    }

    private void showLanguageChangeDialog(String newLanguage) {
        String languageName = languageManager.getLanguageName(newLanguage);

        new AlertDialog.Builder(this)
                .setTitle("Change Language")
                .setMessage("The app will restart to apply " + languageName + ". Continue?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    languageManager.setLanguage(this, newLanguage);
                    restartApp();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Revert spinner selection
                    String currentLang = languageManager.getCurrentLanguage();
                    for (int i = 0; i < LanguageManager.SUPPORTED_LANGUAGES.length; i++) {
                        if (LanguageManager.SUPPORTED_LANGUAGES[i].equals(currentLang)) {
                            spinnerLanguage.setSelection(i);
                            break;
                        }
                    }
                })
                .show();
    }

    private void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        // Kill the current process to ensure complete restart
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void setupListeners() {
        btnSendFeedback.setOnClickListener(v -> openFeedbackForm());
        btnRateApp.setOnClickListener(v -> openRateDialog());
        btnShareApp.setOnClickListener(v -> shareApp());
        btnAbout.setOnClickListener(v -> showAboutDialog());
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
                .setMessage("Version: " + BuildConfig.VERSION_NAME + "\n\n" +
                        "Tisimu is a gospel hymnal reader that allows you to download and read hymnals offline, " +
                        "join communities, and receive daily verses.\n\n" +
                        "Developed with ❤️ for gospel music lovers.\n\n" +
                        "Report issues: feedback@tisimu.com")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}