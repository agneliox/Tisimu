package com.lhavanguane.tisimu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Welcome " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(() -> {
            checkUserLoginStatus();
        }, SPLASH_DELAY);

        initViews();
        setupToolbar();
        setupNavigation();
        setupDrawerContent();
        updateUserInfo();
    }

    private void checkUserLoginStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in, stay in MainActivity or do nothing
            // If this was a splash activity, we would navigate to MainActivity
        } else {
            // User is not logged in, go to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Setup NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigation() {
        // Setup bottom navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.hymnalFragment, R.id.communityFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setupDrawerContent() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.homeFragment);
            } else if (id == R.id.nav_hymnal) {
                navController.navigate(R.id.hymnalFragment);
            } else if (id == R.id.nav_community) {
                navController.navigate(R.id.communityFragment);
            } else if (id == R.id.nav_settings) {
                navController.navigate(R.id.settingsFragment);
            } else if (id == R.id.nav_language) {
                Toast.makeText(this, "Language settings coming soon", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about_app) {
                showAboutDialog();
            } else if (id == R.id.nav_about_publisher) {
                showPublisherDialog();
            } else if (id == R.id.nav_share) {
                shareApp();
            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void updateUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && navigationView.getHeaderView(0) != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView tvUserName = headerView.findViewById(R.id.tvUserName);
            TextView tvUserEmail = headerView.findViewById(R.id.tvUserEmail);

            String name = currentUser.getDisplayName();
            if (name == null || name.isEmpty()) {
                name = currentUser.getEmail().split("@")[0];
            }
            tvUserName.setText(name);
            tvUserEmail.setText(currentUser.getEmail());
        }
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About Tisimu")
                .setMessage("Tisimu is a gospel hymnal reader that allows you to download and read hymnals offline.\n\nVersion: 1.0\n\nDeveloped with ❤️ for gospel music lovers.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPublisherDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About the Publisher")
                .setMessage("Tisimu is developed and maintained by Agnelio Xavier, a passionate developer dedicated to creating tools that help people connect with their faith through music and scripture.\n\nContact: agnelioxavier@gmail.com")
                .setPositiveButton("OK", null)
                .show();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Tisimu - Gospel Hymnal Reader");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Tisimu! A beautiful gospel hymnal reader for Android.\n\nDownload it here: https://github.com/agneliox/Tisimu");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}