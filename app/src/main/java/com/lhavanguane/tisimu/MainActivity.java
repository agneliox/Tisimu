package com.lhavanguane.tisimu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private FirebaseAuth mAuth;
    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupToolbar();
        setupNavigation();
        setupBottomNavigation();
        setupDrawerContent();
        updateUserInfo();
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
//
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigation() {
        // Simply link the bottom navigation with NavController
        // Don't add additional listeners that cause recursion
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Update toolbar title when destination changes
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (isNavigating) return;

            int destinationId = destination.getId();

            if (destinationId == R.id.homeFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Home");
                }
            } else if (destinationId == R.id.hymnalFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Hymnal");
                }
            } else if (destinationId == R.id.communityFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Community");
                }
            } else if (destinationId == R.id.settingsFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Settings");
                }
            }
        });
    }

    private void setupBottomNavigation() {
        // Ensure bottom navigation is visible and enabled
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setEnabled(true);

        // Set default selection (ID must match nav_graph.xml)
        bottomNavigationView.setSelectedItemId(R.id.homeFragment);
    }

    private void setupDrawerContent() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // 1. Try to handle with NavigationUI (matches IDs in nav_graph.xml)
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

            // 2. If not handled by NavigationUI, handle custom menu items
            if (!handled) {
                if (id == R.id.nav_language) {
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
            }

            // Close the drawer
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
                String email = currentUser.getEmail();
                if (email != null) {
                    name = email.split("@")[0];
                } else {
                    name = "User";
                }
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
            // Check if we're on a top-level destination
            NavDestination currentDestination = navController.getCurrentDestination();
            if (currentDestination != null) {
                int currentId = currentDestination.getId();
                if (currentId == R.id.homeFragment ||
                        currentId == R.id.hymnalFragment ||
                        currentId == R.id.communityFragment) {
                    // On top-level, allow back press to close app
                    super.onBackPressed();
                } else {
                    // Navigate up
                    navController.navigateUp();
                }
            } else {
                super.onBackPressed();
            }
        }
    }
}