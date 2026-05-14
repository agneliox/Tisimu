package com.lhavanguane.tisimu.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lhavanguane.tisimu.MainActivity;
import com.lhavanguane.tisimu.R;

public class HomeFragment extends Fragment {

    private TextView tvDailyVerse, tvVerseReference, homeSalutation;
    private TextView tvGreeting;
    private TextView tvWelcomeMessage;
    private Toolbar toolbar;
//    private com.google.android.material.appbar.MaterialToolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        tvDailyVerse = view.findViewById(R.id.tvDailyVerse);
        tvVerseReference = view.findViewById(R.id.tvVerseReference);
        toolbar = view.findViewById(R.id.toolbar);

        setupToolbar();
        displayUserGreeting();
        loadDailyVerse();

        return view;
    }

    private void setupToolbar() {
        // Set up the toolbar
        if (getActivity() != null) {
            // Set up the toolbar as the action bar
            ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

            // Enable the navigation icon to open drawer
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(v -> {
                DrawerLayout drawerLayout = ((MainActivity) requireActivity()).getDrawerLayout();
                if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });

            // Set title
            toolbar.setTitle("Home");
        }
    }

    private void displayUserGreeting() {
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();
            String firstName = "";

            // Get first name from display name if available
            if (displayName != null && !displayName.isEmpty()) {
                String[] nameParts = displayName.split(" ");
                firstName = nameParts[0];
            }
            // Otherwise extract from email
            else if (email != null && !email.isEmpty()) {
                String emailPrefix = email.split("@")[0];
                // Try to split by dot or underscore to get first name
                String[] nameParts = emailPrefix.split("[._]");
                firstName = nameParts[0];
                // Capitalize first letter
                firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            }

            if (!firstName.isEmpty()) {
                tvGreeting.setText("Hello, " + firstName + "! 👋");
                tvWelcomeMessage.setText("Welcome back to Tisimu");
            } else {
                tvGreeting.setText("Hello, Beloved! 👋");
                tvWelcomeMessage.setText("Welcome to Tisimu");
            }
        } else {
            tvGreeting.setText("Hello, Guest! 👋");
            tvWelcomeMessage.setText("Welcome to Tisimu");
        }
    }

    private void loadDailyVerse() {
        // TODO: Implement daily verse from API or local storage
        tvDailyVerse.setText("For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.");
        tvVerseReference.setText("John 3:16");
    }

    private void refreshVerse() {
        loadDailyVerse();
        Toast.makeText(getContext(), "Verse refreshed", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.home_fragment_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.action_refresh_verse) {
//            refreshVerse();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}