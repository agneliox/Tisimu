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
        // Apply language before super.onCreate
        LanguageManager.getInstance(this).updateAppLanguage(this);
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
            getSupportActionBar().setTitle(R.string.settings);
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
        String modeName = enableDarkMode ? getString(R.string.dark_mode) : getString(R.string.light_mode);

        new AlertDialog.Builder(this)
                .setTitle(R.string.change_theme)
                .setMessage(getString(R.string.language_change_message, modeName))
                .setPositiveButton(R.string.apply, (dialog, which) -> {
                    themeManager.setDarkMode(enableDarkMode);
                    restartApp();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    switchDarkMode.setChecked(!enableDarkMode);
                })
                .show();
    }

    private void showLanguageChangeDialog(String newLanguage) {
        String languageName = languageManager.getLanguageName(newLanguage);

        new AlertDialog.Builder(this)
                .setTitle(R.string.change_language)
                .setMessage(getString(R.string.language_change_message, languageName))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    languageManager.setLanguage(this, newLanguage);
                    restartApp();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
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
                R.string.feedback_toast,
                Toast.LENGTH_LONG).show();
    }

    private void openRateDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.rate_app))
                .setMessage(R.string.rate_tisimu_message)
                .setPositiveButton(R.string.rate_now, (dialog, which) -> {
                    openFeedbackForm();
                })
                .setNegativeButton(R.string.later, null)
                .show();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_app_text) + "\n\n" +
                        getString(R.string.shared_via_tisimu) + ": " + Constants.FEEDBACK_FORM_URL);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about_tisimu)
                .setMessage(getString(R.string.about_tisimu_message) + "\n\n" +
                        getString(R.string.report_issues, "feedback@tisimu.com"))
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}