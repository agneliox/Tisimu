package com.lhavanguane.tisimu.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lhavanguane.tisimu.BuildConfig;
import com.lhavanguane.tisimu.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.LoginActivity;
import com.lhavanguane.tisimu.utils.Constants;
import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.utils.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private ShapeableImageView ivAvatar;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvMemberSince;
    private TextView tvCommunitiesCount;
    private TextView tvHymnalsCount;
    private TextView tvFavoritesCount;
    private TextView tvCurrentLanguage;
    private SwitchMaterial switchDarkMode;
    private MaterialButton btnLogout;
    private TextView tvAppVersion;

    private View layoutEditProfile;
    private View layoutChangePassword;
    private View layoutSendFeedback;
    private View layoutRateApp;
    private View layoutShareApp;
    private View layoutAbout;

    private FirebaseAuth mAuth;
    private LanguageManager languageManager;
    private ThemeManager themeManager;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(requireActivity());
        mAuth = FirebaseAuth.getInstance();
        languageManager = LanguageManager.getInstance(requireContext());
        themeManager = ThemeManager.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews(view);
        setupToolbar();
        setupUserInfo();
        setupStats();
        setupLanguageDisplay();
        setupDarkModeSwitch();
        setupClickListeners();
        setupVersionInfo();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.profileToolbar);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
        tvCommunitiesCount = view.findViewById(R.id.tvCommunitiesCount);
        tvHymnalsCount = view.findViewById(R.id.tvHymnalsCount);
        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount);
        tvCurrentLanguage = view.findViewById(R.id.tvCurrentLanguage);
        switchDarkMode = view.findViewById(R.id.switchDarkModeProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvAppVersion = view.findViewById(R.id.tvAppVersion);

        layoutEditProfile = view.findViewById(R.id.layoutEditProfile);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutSendFeedback = view.findViewById(R.id.layoutSendFeedback);
        layoutRateApp = view.findViewById(R.id.layoutRateApp);
        layoutShareApp = view.findViewById(R.id.layoutShareApp);
        layoutAbout = view.findViewById(R.id.layoutAbout);
    }


    private void setupToolbar() {
        if (getActivity() != null) {
            ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_menu_2);
            toolbar.setNavigationOnClickListener(v -> {
                DrawerLayout drawerLayout = ((MainActivity) requireActivity()).getDrawerLayout();
                if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });
        }
    }
    private void setupUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Set user name
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvUserName.setText(displayName);
            } else {
                String email = user.getEmail();
                if (email != null) {
                    String nameFromEmail = email.split("@")[0];
                    tvUserName.setText(nameFromEmail);
                } else {
                    tvUserName.setText("User");
                }
            }

            // Set user email
            tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No email");

            // Set member since (using user creation time if available)
            if (user.getMetadata() != null && user.getMetadata().getCreationTimestamp() > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                String date = sdf.format(new Date(user.getMetadata().getCreationTimestamp()));
                tvMemberSince.setText("Member since " + date);
                Toast.makeText(requireContext(), "URL " + user.getPhotoUrl(), Toast.LENGTH_SHORT).show();
            } else {
                tvMemberSince.setText("Member");
            }

            // Load avatar (placeholder - can be expanded later)
            // For now, keep default avatar
        } else {
            tvUserName.setText("Guest User");
            tvUserEmail.setText("Not logged in");
            tvMemberSince.setText("");
        }
    }

    private void setupStats() {
        // TODO: Load actual counts from database/preferences
        // Communities count - from CommunityManager
        // Hymnals count - from PreferencesManager
        // Favorites count - from FavoritesManager (when implemented)

        // Placeholder values
        tvCommunitiesCount.setText("0");
        tvHymnalsCount.setText("0");
        tvFavoritesCount.setText("0");
    }

    private void setupLanguageDisplay() {
        String currentLangCode = languageManager.getCurrentLanguage();
        String languageName = languageManager.getLanguageName(currentLangCode);
        tvCurrentLanguage.setText(languageName);
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

        new AlertDialog.Builder(requireContext())
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

    private void setupClickListeners() {
        layoutEditProfile.setOnClickListener(v -> showEditProfileDialog());
        layoutChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        layoutSendFeedback.setOnClickListener(v -> openFeedbackForm());
        layoutRateApp.setOnClickListener(v -> openRateDialog());
        layoutShareApp.setOnClickListener(v -> shareApp());
        layoutAbout.setOnClickListener(v -> showAboutDialog());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void showEditProfileDialog() {
        // TODO: Implement edit profile
        Toast.makeText(requireContext(), "Edit Profile coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showChangePasswordDialog() {
        // TODO: Implement change password via Firebase
        Toast.makeText(requireContext(), "Change Password coming soon", Toast.LENGTH_SHORT).show();
    }

    private void openFeedbackForm() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FEEDBACK_FORM_URL));
        startActivity(browserIntent);
        Toast.makeText(requireContext(),
                "Please include your device model and Android version in the feedback",
                Toast.LENGTH_LONG).show();
    }

    private void openRateDialog() {
        new AlertDialog.Builder(requireContext())
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
        new AlertDialog.Builder(requireContext())
                .setTitle("About Tisimu")
                .setMessage("Version: " + BuildConfig.VERSION_NAME + "\n\n" +
                        "Tisimu is a gospel hymnal reader that allows you to download and read hymnals offline, " +
                        "join communities, and receive daily verses.\n\n" +
                        "Developed with ❤️ for gospel music lovers.\n\n" +
                        "* * * * *")
                .setPositiveButton("OK", null)
                .show();
    }

    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void restartApp() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void setupVersionInfo() {
        tvAppVersion.setText("Version " + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to profile
        setupUserInfo();
        setupLanguageDisplay();
    }
}