package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lhavanguane.tisimu.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.utils.LanguageManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SwitchMaterial switchDarkMode;
    private Spinner spinnerLanguage;
    private LanguageManager languageManager;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        languageManager = LanguageManager.getInstance(requireContext());

        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);

        setupLanguageSpinner();
        setupDarkModeSwitch();

        return view;
    }

    private void setupDarkModeSwitch() {
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(getContext(), "Dark mode enabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(getContext(), "Light mode enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupLanguageSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                LanguageManager.LANGUAGE_NAMES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // Set current selection
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
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
        Toast.makeText(requireContext(), "Language changed. App restarting...", Toast.LENGTH_SHORT).show();
    }
}