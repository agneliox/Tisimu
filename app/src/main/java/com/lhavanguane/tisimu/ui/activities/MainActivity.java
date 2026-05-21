package com.lhavanguane.tisimu.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.ui.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private FirebaseAuth mAuth;
    private final boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply language before super.onCreate and setContentView
        LanguageManager.getInstance(this).updateAppLanguage(this);
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);


        // Enable dynamic colors for Android 12+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Dynamic colors are automatically applied from the theme
            // You can check if dynamic color is available
            androidx.core.content.res.ResourcesCompat.FontCallback callback = new androidx.core.content.res.ResourcesCompat.FontCallback() {
                @Override
                public void onFontRetrieved(@NonNull Typeface typeface) {
                    // Font loaded
                }

                @Override
                public void onFontRetrievalFailed(int reason) {
                    // Font failed to load
                }
            };
        }

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupNavigation();
        setupBottomNavigation();
        setupDrawerContent();
        updateUserInfo();

        // Handle which tab to show (coming from HymnalSelectionActivity)
        handleIntentTabSelection();
    }

    private void handleIntentTabSelection() {
        int selectedTab = getIntent().getIntExtra("SELECTED_TAB", -1);
        if (selectedTab != -1 && bottomNavigationView != null) {
            // Navigate to the selected tab
            if (selectedTab == 0) {
                bottomNavigationView.setSelectedItemId(R.id.homeFragment);
                navController.navigate(R.id.homeFragment);
            } else if (selectedTab == 1) {
                bottomNavigationView.setSelectedItemId(R.id.hymnalFragment);
                navController.navigate(R.id.hymnalFragment);
            } else if (selectedTab == 2) {
                bottomNavigationView.setSelectedItemId(R.id.communityFragment);
                navController.navigate(R.id.communityFragment);
            }
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


    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
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
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
//                    .setTitle("Home");
                }
            } else if (destinationId == R.id.hymnalFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
//                    getSupportActionBar();
//                            .setTitle("Hymnal");
                }
            } else if (destinationId == R.id.communityFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar();
//                    .setTitle("Community");
                }
            } else if (destinationId == R.id.profileFragment) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar();
//                    .setTitle("Me");
                }
            }
//            else if (destinationId == R.id.settingsFragment) {
//                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                if (getSupportActionBar() != null) {
//                    getSupportActionBar().setTitle("Settings");
//                }
//            }
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

            // Handle menu items that are not part of the navigation graph
            if (id == R.id.nav_language) {
                showLanguageSelectionDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_about_app) {
                showAboutDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_about_publisher) {
                showPublisherDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_share) {
                shareApp();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_settings) {
                // Open SettingsActivity instead of fragment navigation
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_logout) {
                logout();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            // For navigation items, let NavController handle it
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return handled;
        });
    }

    private void showLanguageSelectionDialog() {
        LanguageManager languageManager = LanguageManager.getInstance(this);
        String currentLang = languageManager.getCurrentLanguage();
        int checkedItem = 0;

        for (int i = 0; i < LanguageManager.SUPPORTED_LANGUAGES.length; i++) {
            if (LanguageManager.SUPPORTED_LANGUAGES[i].equals(currentLang)) {
                checkedItem = i;
                break;
            }
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.change_language)
                .setSingleChoiceItems(LanguageManager.LANGUAGE_NAMES, checkedItem, (dialog, which) -> {
                    String selectedLang = LanguageManager.SUPPORTED_LANGUAGES[which];
                    if (!selectedLang.equals(currentLang)) {
                        languageManager.setLanguage(this, selectedLang);
                        // Restart activity to apply language changes
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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

            HomeFragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putString("userName", name);
            homeFragment.setArguments(args);

        }
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.about_tisimu)
                .setMessage(R.string.about_tisimu_message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void showPublisherDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.about_publisher)
                .setMessage(R.string.about_publisher_message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, R.string.logged_out_successfully, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp();
    }

    @SuppressLint("GestureBackNavigation")
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