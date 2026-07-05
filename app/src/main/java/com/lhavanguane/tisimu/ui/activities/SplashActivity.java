package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lhavanguane.tisimu.BuildConfig;
import com.lhavanguane.tisimu.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private FirebaseAuth mAuth;
    private final Handler handler = new Handler();
    TextView splashAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply language before super.onCreate
        com.lhavanguane.tisimu.utils.LanguageManager.getInstance(this).updateAppLanguage(this);
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        splashAppVersion = findViewById(R.id.splash_version);
        setupVersionInfo();

        handler.postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                PreferencesManager prefs = PreferencesManager.getInstance(this);
//                if (prefs.getSelectedHymnals().isEmpty()) {
//                    // No hymnals selected, go to selection screen
//                    startActivity(new Intent(SplashActivity.this, HymnalSelectionActivity.class));
//                } else {
//                    // Hymnals already selected, go to song list
//                    startActivity(new Intent(SplashActivity.this, SongListActivity.class));
//                }
            } else {
                // User not logged in, go to login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);

        // In MainActivity or SplashActivity
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "app_install");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "App Install");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        // Set Crashlytics user ID if logged in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            FirebaseCrashlytics.getInstance().setUserId(user.getUid());
        }
    }

    private void setupVersionInfo() {
        splashAppVersion.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}