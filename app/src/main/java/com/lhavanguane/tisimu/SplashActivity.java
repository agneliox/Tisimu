package com.lhavanguane.tisimu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lhavanguane.tisimu.ui.activities.HymnalSelectionActivity;
import com.lhavanguane.tisimu.ui.activities.SongListActivity;
import com.lhavanguane.tisimu.utils.PreferencesManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private FirebaseAuth mAuth;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply language before super.onCreate
        com.lhavanguane.tisimu.utils.LanguageManager.getInstance(this).updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

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

// Set Crashlytics user ID
        FirebaseCrashlytics.getInstance().setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}