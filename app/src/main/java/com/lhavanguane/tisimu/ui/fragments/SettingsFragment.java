package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lhavanguane.tisimu.BuildConfig;
import com.lhavanguane.tisimu.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.utils.Constants;
import com.lhavanguane.tisimu.utils.LanguageManager;

public class SettingsFragment extends Fragment {

    private SwitchMaterial switchDarkMode;
    private Spinner spinnerLanguage;
    private MaterialButton btnSendFeedback;
    private MaterialButton btnRateApp;
    private MaterialButton btnShareApp;
    private MaterialButton btnAbout;

    private LanguageManager languageManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        languageManager = LanguageManager.getInstance(requireContext());

        initViews(view);
        setupLanguageSpinner();
        setupDarkModeSwitch();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        btnSendFeedback = view.findViewById(R.id.btnSendFeedback);
        btnRateApp = view.findViewById(R.id.btnRateApp);
        btnShareApp = view.findViewById(R.id.btnShareApp);
        btnAbout = view.findViewById(R.id.btnAbout);
    }

    private void setupListeners() {
        btnSendFeedback.setOnClickListener(v -> openFeedbackForm());
        btnRateApp.setOnClickListener(v -> openRateDialog());
        btnShareApp.setOnClickListener(v -> shareApp());
        btnAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void openFeedbackForm() {
        // Build URL with device info
        String deviceInfo = Build.MANUFACTURER + " " + Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String appVersion = BuildConfig.VERSION_NAME;

        // Create intent to open browser with feedback form
        // You can add device info as URL parameters if your form accepts them
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FEEDBACK_FORM_URL));
        startActivity(browserIntent);

        // Optional: Show toast reminding user to include device info
        Toast.makeText(requireContext(),
                "Please include your device model and Android version in the feedback",
                Toast.LENGTH_LONG).show();
    }

    private void openRateDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Rate Tisimu")
                .setMessage("If you enjoy using Tisimu, please take a moment to rate it. Your feedback helps us improve!")
                .setPositiveButton("Rate Now", (dialog, which) -> {
                    // Open Google Play Store rating (when published)
                    // For now, open feedback form
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
        new AlertDialog.Builder(requireContext())
                .setTitle("About Tisimu")
                .setMessage("Version: " + BuildConfig.VERSION_NAME + "\n\n" +
                        "Tisimu is a gospel hymnal reader that allows you to download and read hymnals offline, " +
                        "join communities, and receive daily verses.\n\n" +
                        "Developed with ❤️ for gospel music lovers.\n\n" +
                        "*****")
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupLanguageSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
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
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newLanguage = LanguageManager.SUPPORTED_LANGUAGES[position];
                if (!newLanguage.equals(languageManager.getCurrentLanguage())) {
                    showLanguageChangeDialog(newLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showLanguageChangeDialog(String newLanguage) {
        String languageName = languageManager.getLanguageName(newLanguage);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Language")
                .setMessage("The app will restart to apply " + languageName + ". Continue?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    languageManager.setLanguage(requireContext(), newLanguage);
                    restartApp();
                })
                .setNegativeButton("No", (dialog, which) -> {
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
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupDarkModeSwitch() {
        // Dark mode implementation
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Implement dark mode
            Toast.makeText(requireContext(), "Dark mode coming soon", Toast.LENGTH_SHORT).show();
        });
    }
}