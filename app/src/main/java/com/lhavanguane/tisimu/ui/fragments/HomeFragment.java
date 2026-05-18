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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lhavanguane.tisimu.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.DailyVerse;
import com.lhavanguane.tisimu.services.DailyVerseManager;
import com.lhavanguane.tisimu.utils.LanguageManager;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvWelcomeMessage;
    private TextView tvDailyVerse;
    private TextView tvVerseReference;
    private TextView tvDevotionalTitle;
    private TextView tvDevotionalBody;
    private TextView tvApplication;
    private TextView tvReflection;
    private TextView tvPrayer;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View devotionalCard;
    private View applicationCard;
    private View reflectionCard;
    private View prayerCard;

    private FirebaseAuth mAuth;
    private DailyVerseManager dailyVerseManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(requireActivity());
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        dailyVerseManager = DailyVerseManager.getInstance(requireContext());
        LanguageManager languageManager = LanguageManager.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews(view);
        setupToolbar();
        setupSwipeRefresh(view);
        displayUserGreeting();
        loadDailyVerse();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.homeFragmentToolbar);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        tvDailyVerse = view.findViewById(R.id.tvDailyVerse);
        tvVerseReference = view.findViewById(R.id.tvVerseReference);
        tvDevotionalTitle = view.findViewById(R.id.tvDevotionalTitle);
        tvDevotionalBody = view.findViewById(R.id.tvDevotionalBody);
        tvApplication = view.findViewById(R.id.tvApplication);
        tvReflection = view.findViewById(R.id.tvReflection);
        tvPrayer = view.findViewById(R.id.tvPrayer);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        devotionalCard = view.findViewById(R.id.devotionalCard);
        applicationCard = view.findViewById(R.id.applicationCard);
        reflectionCard = view.findViewById(R.id.reflectionCard);
        prayerCard = view.findViewById(R.id.prayerCard);
    }

    private void setupToolbar() {
        if (toolbar == null) return;

        if (getActivity() != null) {
            ((MainActivity) requireActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("");
            toolbar.setNavigationIcon(R.drawable.ic_menu_2);
            toolbar.setNavigationOnClickListener(v -> {
                DrawerLayout drawerLayout = ((MainActivity) requireActivity()).getDrawerLayout();
                if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });

            toolbar.inflateMenu(R.menu.home_fragment_menu);
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_refresh_verse) {
                    refreshVerse();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupSwipeRefresh(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.md_theme_primary);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                loadDailyVerse();
                swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    private void displayUserGreeting() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();
            String firstName = "";

            if (displayName != null && !displayName.isEmpty()) {
                String[] nameParts = displayName.split(" ");
                firstName = nameParts[0];
            } else if (email != null && !email.isEmpty()) {
                String emailPrefix = email.split("@")[0];
                String[] nameParts = emailPrefix.split("[._]");
                firstName = nameParts[0];
                firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            }

            if (!firstName.isEmpty()) {
                tvGreeting.setText(getString(R.string.hello, firstName));
                tvWelcomeMessage.setText(R.string.welcome_to_tisimu);
            } else {
                tvGreeting.setText(R.string.hello_hand);
                tvWelcomeMessage.setText(R.string.welcome_to_tisimu);
            }
        } else {
            tvGreeting.setText(R.string.hello_hand);
            tvWelcomeMessage.setText(R.string.welcome_to_tisimu);
        }
    }

    private void loadDailyVerse() {
        DailyVerse verse = dailyVerseManager.getTodaysVerse();

        if (verse != null) {
            displayVerse(verse);
        } else {
            showError(getString(R.string.unable_to_load_verse));
        }
    }

    private void displayVerse(DailyVerse verse) {
        // Set verse and reference
        tvDailyVerse.setText("\"" + verse.getVerse() + "\"");
        tvVerseReference.setText(verse.getReference());

        // Set devotional content
        tvDevotionalTitle.setText(verse.getDevotionalTitle());
        tvDevotionalBody.setText(verse.getDevotionalBody());

        // Set application
        tvApplication.setText(verse.getApplication());

        // Set reflection
        tvReflection.setText(verse.getReflection());

        // Set prayer
        tvPrayer.setText(verse.getPrayer());

        // Show all cards
        devotionalCard.setVisibility(View.VISIBLE);
        applicationCard.setVisibility(View.VISIBLE);
        reflectionCard.setVisibility(View.VISIBLE);
        prayerCard.setVisibility(View.VISIBLE);
    }

    private void refreshVerse() {
        loadDailyVerse();
        Toast.makeText(getContext(), R.string.refresh_verse, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        tvDailyVerse.setText(R.string.unable_to_load_verse);
        tvVerseReference.setText(R.string.check_connection);
        devotionalCard.setVisibility(View.GONE);
        applicationCard.setVisibility(View.GONE);
        reflectionCard.setVisibility(View.GONE);
        prayerCard.setVisibility(View.GONE);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_refresh_verse) {
            refreshVerse();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        displayUserGreeting();
        loadDailyVerse();
    }
}