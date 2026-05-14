package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.lhavanguane.tisimu.MainActivity;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.models.SongItem;
import com.lhavanguane.tisimu.services.HymnalStorageManager;
import com.lhavanguane.tisimu.ui.activities.HymnalSelectionActivity;
import com.lhavanguane.tisimu.ui.activities.SongDetailActivity;
import com.lhavanguane.tisimu.ui.adapters.SongAdapter;
import com.lhavanguane.tisimu.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HymnalFragment extends Fragment {

    private static final String TAG = "HymnalFragment";

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvSongs;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefreshLayout;

    private HymnalStorageManager storageManager;
    private PreferencesManager preferencesManager;
    private SongAdapter adapter;

    // Data structures
    private List<SongItem> allSongs;
    private List<SongItem> filteredSongs;
    private Map<String, HymnalData> loadedHymnals;
    private List<String> selectedHymnalIds;
    private String currentHymnalFilter;
    private int pendingLoadCount;
    private boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Important: This enables the fragment's menu
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hymnal, container, false);

        storageManager = new HymnalStorageManager(requireContext());
        preferencesManager = PreferencesManager.getInstance(requireContext());

        allSongs = new ArrayList<>();
        filteredSongs = new ArrayList<>();
        loadedHymnals = new HashMap<>();
        selectedHymnalIds = new ArrayList<>();

        initViews(view);
        setupToolbar();
        setupRecyclerView();
        setupSearchView(view);
        setupSwipeRefresh(view);
        loadSelectedHymnals();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tabLayout = view.findViewById(R.id.tabLayout);
        rvSongs = view.findViewById(R.id.rvSongs);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
    }

    private void setupToolbar() {
        if (toolbar == null) {
            return;
        }

        if (getActivity() != null) {
            // Set the toolbar as the action bar for the activity
            ((MainActivity) requireActivity()).setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(v -> {
                DrawerLayout drawerLayout = ((MainActivity) requireActivity()).getDrawerLayout();
                if (drawerLayout != null && !drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });

            toolbar.setTitle("My Hymnal");

            // Inflate the menu programmatically to ensure it's attached
            toolbar.inflateMenu(R.menu.hymnal_fragment_menu);

            // Set menu item click listener
            toolbar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_select_hymnals) {
                    openHymnalSelection();
                    return true;
                } else if (id == R.id.action_refresh) {
                    refreshHymnals();
                    return true;
                }
                return false;
            });
        }
    }

    private void setupRecyclerView() {
        adapter = new SongAdapter();
        rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSongs.setAdapter(adapter);

        adapter.setOnSongClickListener(songItem -> {
            if (isAdded() && getContext() != null) {
                Intent intent = new Intent(requireContext(), SongDetailActivity.class);
                intent.putExtra("SONG_NUMBER", songItem.getSong().getNumber());
                intent.putExtra("SONG_TITLE", songItem.getSong().getTitle());
                intent.putExtra("SONG_LYRICS", songItem.getSong().getLyrics());
                intent.putExtra("SONG_AUTHOR", songItem.getSong().getAuthor());
                intent.putExtra("SONG_COMPOSER", songItem.getSong().getComposer());
                intent.putExtra("HYMNAL_NAME", songItem.getHymnalName());
                startActivity(intent);
            }
        });
    }

    private void setupSearchView(View view) {
        searchView = view.findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterSongs(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterSongs(newText);
                    return true;
                }
            });
        }
    }

    private void setupSwipeRefresh(View view) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.md_theme_primary);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                refreshHymnals();
            });
        }
    }

    private void loadSelectedHymnals() {
        Set<String> selectedIds = preferencesManager.getSelectedHymnals();

        android.util.Log.d(TAG, "loadSelectedHymnals - Selected IDs: " + selectedIds);

        if (selectedIds.isEmpty()) {
            if (isAdded()) {
                showEmptyState(true, "No hymnals selected. Tap the menu icon to select hymnals.");
                showProgress(false);
            }
            return;
        }

        selectedHymnalIds.clear();
        selectedHymnalIds.addAll(selectedIds);
        pendingLoadCount = selectedHymnalIds.size();
        isLoading = true;

        if (isAdded()) {
            showProgress(true);
            setupTabLayout();
        }

        // Load each hymnal
        for (String hymnalId : selectedHymnalIds) {
            loadHymnal(hymnalId);
        }
    }

    private void setupTabLayout() {
        if (tabLayout == null) return;

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("All"));

        for (String hymnalId : selectedHymnalIds) {
            tabLayout.addTab(tabLayout.newTab().setText(hymnalId));
        }

        tabLayout.clearOnTabSelectedListeners();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    currentHymnalFilter = null;
                } else if (position - 1 < selectedHymnalIds.size()) {
                    currentHymnalFilter = selectedHymnalIds.get(position - 1);
                }
                filterSongs();
                if (isAdded()) {
                    updateEmptyState();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadHymnal(String hymnalId) {
        storageManager.loadHymnal(hymnalId, new HymnalStorageManager.HymnalLoadCallback() {
            @Override
            public void onSuccess(HymnalData hymnal) {
                if (!isAdded() || getActivity() == null) {
                    return;
                }

                loadedHymnals.put(hymnalId, hymnal);

                for (HymnalData.Song song : hymnal.getSongs()) {
                    allSongs.add(new SongItem(hymnalId, hymnal.getName(), song));
                }

                int index = selectedHymnalIds.indexOf(hymnalId);
                if (index != -1 && tabLayout != null && tabLayout.getTabAt(index + 1) != null) {
                    requireActivity().runOnUiThread(() -> {
                        if (isAdded() && tabLayout != null && tabLayout.getTabAt(index + 1) != null) {
                            tabLayout.getTabAt(index + 1).setText(hymnal.getName());
                        }
                    });
                }

                pendingLoadCount--;

                if (pendingLoadCount == 0) {
                    isLoading = false;
                    requireActivity().runOnUiThread(() -> {
                        if (isAdded() && getContext() != null) {
                            showProgress(false);
                            filterSongs();
                            updateEmptyState();

                            String message = "Loaded " + allSongs.size() + " songs from " + loadedHymnals.size() + " hymnal(s)";
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded() || getActivity() == null) {
                    return;
                }

                pendingLoadCount--;

                if (pendingLoadCount == 0 && loadedHymnals.isEmpty()) {
                    requireActivity().runOnUiThread(() -> {
                        if (isAdded() && getContext() != null) {
                            showProgress(false);
                            showEmptyState(true, "Failed to load hymnals. Please check your connection and try again.");
                        }
                    });
                }
            }
        });
    }

    private void filterSongs() {
        filterSongs(null);
    }

    private void filterSongs(String searchQuery) {
        if (isLoading || !isAdded()) return;

        filteredSongs.clear();

        for (SongItem item : allSongs) {
            if (currentHymnalFilter != null && !item.getHymnalId().equals(currentHymnalFilter)) {
                continue;
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String lowerQuery = searchQuery.toLowerCase().trim();
                if (!item.getSong().getTitle().toLowerCase().contains(lowerQuery) &&
                        !String.valueOf(item.getSong().getNumber()).contains(lowerQuery)) {
                    continue;
                }
            }

            filteredSongs.add(item);
        }

        adapter.setSongs(filteredSongs);

        boolean hasSongs = !filteredSongs.isEmpty();
        if (isAdded()) {
            showEmptyState(!hasSongs, "No songs found");
            if (rvSongs != null) {
                rvSongs.setVisibility(hasSongs ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void showProgress(boolean show) {
        if (!isAdded()) return;

        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (swipeRefreshLayout != null && !show) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showEmptyState(boolean show, String message) {
        if (!isAdded()) return;

        if (show) {
            if (tvEmptyState != null) {
                tvEmptyState.setText(message);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
            if (rvSongs != null) {
                rvSongs.setVisibility(View.GONE);
            }
        } else {
            if (tvEmptyState != null) {
                tvEmptyState.setVisibility(View.GONE);
            }
        }
    }

    private void updateEmptyState() {
        if (!isAdded()) return;

        if (filteredSongs.isEmpty() && !isLoading) {
            String message = "No songs available in this hymnal";
            if (tvEmptyState != null) {
                tvEmptyState.setText(message);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
            if (rvSongs != null) {
                rvSongs.setVisibility(View.GONE);
            }
        } else {
            if (tvEmptyState != null) {
                tvEmptyState.setVisibility(View.GONE);
            }
            if (rvSongs != null) {
                rvSongs.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refreshHymnals() {
        // Clear existing data
        allSongs.clear();
        filteredSongs.clear();
        loadedHymnals.clear();
        selectedHymnalIds.clear();

        // Reload from preferences
        loadSelectedHymnals();
    }

    private void openHymnalSelection() {
        if (isAdded()) {
            android.util.Log.d(TAG, "openHymnalSelection called - Opening HymnalSelectionActivity");
            Intent intent = new Intent(requireContext(), HymnalSelectionActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // This method is now optional since we're inflating the menu directly on the toolbar
        // But keep it for compatibility
        inflater.inflate(R.menu.hymnal_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // This method may not be called when using toolbar.setOnMenuItemClickListener
        // But keep it as backup
        int id = item.getItemId();

        android.util.Log.d(TAG, "onOptionsItemSelected called with id: " + id);

        if (id == R.id.action_select_hymnals) {
            openHymnalSelection();
            return true;
        } else if (id == R.id.action_refresh) {
            refreshHymnals();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        android.util.Log.d(TAG, "onResume - Refreshing hymnals");
        refreshHymnals();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }
        if (tabLayout != null) {
            tabLayout.clearOnTabSelectedListeners();
        }
        // Clear toolbar menu listener
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(null);
        }
    }
}