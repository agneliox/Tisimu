package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.utils.SelectionManager;
import com.lhavanguane.tisimu.viewmodels.HymnalViewModel;

public class MainActivity extends AppCompatActivity {
//    private static final int SPLASH_DELAY = 2000; // 2 seconds
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        mAuth = FirebaseAuth.getInstance();
//
//        // Check if user is logged in
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            Toast.makeText(this, "Welcome " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
//        }
//
//        // Navigate to Hymnal Selection after login
//        // Add a small delay to show welcome message
//        findViewById(android.R.id.content).postDelayed(() -> {
//            Intent intent = new Intent(MainActivity.this, HymnalSelectionActivity.class);
//            startActivity(intent);
//            finish();
//        }, 1500);
//
//        new Handler().postDelayed(() -> {
//            checkUserLoginStatus();
//        }, SPLASH_DELAY);
//    }
//
//    private void checkUserLoginStatus() {
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if (currentUser != null) {
//            // User is logged in, stay in MainActivity or do nothing
//            // If this was a splash activity, we would navigate to MainActivity
//        } else {
//            // User is not logged in, go to LoginActivity
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    private FirebaseAuth mAuth;
    private Handler handler = new Handler();
    private SelectionManager selectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        selectionManager = SelectionManager.getInstance(this);

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Welcome " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();

            // Check if user has selected any hymnals
            if (selectionManager.hasSelectedHymnals()) {
                redirectToSongList();
            } else {
                redirectToHymnalSelection();
            }
        } else {
            // No user logged in, go to login
            redirectToLogin();
        }
    }

    private void redirectToHymnalSelection() {
        handler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, HymnalSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void redirectToSongList() {
        handler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, SongListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void redirectToLogin() {
        handler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}