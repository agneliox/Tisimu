package com.lhavanguane.tisimu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lhavanguane.tisimu.ui.activities.HymnalSelectionActivity;
import com.lhavanguane.tisimu.ui.activities.SongListActivity;
import com.lhavanguane.tisimu.utils.PreferencesManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private FirebaseAuth mAuth;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        handler.postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in
                PreferencesManager prefs = PreferencesManager.getInstance(this);
                if (prefs.getSelectedHymnals().isEmpty()) {
                    // No hymnals selected, go to selection screen
                    startActivity(new Intent(SplashActivity.this, HymnalSelectionActivity.class));
                } else {
                    // Hymnals already selected, go to song list
                    startActivity(new Intent(SplashActivity.this, SongListActivity.class));
                }
            } else {
                // User not logged in, go to login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}