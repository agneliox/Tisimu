package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.models.SongItem;
import com.lhavanguane.tisimu.services.HymnalStorageManager;
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

    private TabLayout tabLayout;
    private RecyclerView rvSongs;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private HymnalStorageManager storageManager;
    private PreferencesManager preferencesManager;
    private SongAdapter adapter;

    // Data structures
    private List<SongItem> allSongs;
    private List<SongItem> filteredSongs;
    private Map<String, HymnalData> loadedHymnals;
    private List<String> selectedHymnalIds;
    private String currentHymnalFilter; // null means show all
    private int pendingLoadCount;
    private boolean isLoading = false;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        setupRecyclerView();
        setupSearchView(view);
        loadSelectedHymnals();

        return view;
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        rvSongs = view.findViewById(R.id.rvSongs);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        // In initViews:
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshHymnals();
        });
    }

    private void refreshHymnals() {
        allSongs.clear();
        loadedHymnals.clear();
        selectedHymnalIds.clear();
        loadSelectedHymnals();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView() {
        adapter = new SongAdapter();
        rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSongs.setAdapter(adapter);

        adapter.setOnSongClickListener(songItem -> {
            Intent intent = new Intent(requireContext(), SongDetailActivity.class);
            intent.putExtra("SONG_NUMBER", songItem.getSong().getNumber());
            intent.putExtra("SONG_TITLE", songItem.getSong().getTitle());
            intent.putExtra("SONG_LYRICS", songItem.getSong().getLyrics());
            intent.putExtra("SONG_AUTHOR", songItem.getSong().getAuthor());
            intent.putExtra("SONG_COMPOSER", songItem.getSong().getComposer());
            intent.putExtra("HYMNAL_NAME", songItem.getHymnalName());
            startActivity(intent);
        });
    }

    private void setupSearchView(View view) {
        searchView = view.findViewById(R.id.searchView);
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

    private void loadSelectedHymnals() {
        Set<String> selectedIds = preferencesManager.getSelectedHymnals();

        if (selectedIds.isEmpty()) {
            showEmptyState(true, "No hymnals selected. Please go to Settings to select hymnals.");
            return;
        }

        selectedHymnalIds.clear();
        selectedHymnalIds.addAll(selectedIds);
        pendingLoadCount = selectedHymnalIds.size();
        isLoading = true;

        showProgress(true);
        setupTabLayout();

        // Load each hymnal
        for (String hymnalId : selectedHymnalIds) {
            loadHymnal(hymnalId);
        }
    }

    private void setupTabLayout() {
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("All"));

        for (String hymnalId : selectedHymnalIds) {
            tabLayout.addTab(tabLayout.newTab().setText(hymnalId));
        }

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
                updateEmptyState();
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
                loadedHymnals.put(hymnalId, hymnal);

                // Add all songs from this hymnal to the list
                for (HymnalData.Song song : hymnal.getSongs()) {
                    allSongs.add(new SongItem(hymnalId, hymnal.getName(), song));
                }

                // Update tab title with actual hymnal name
                int index = selectedHymnalIds.indexOf(hymnalId);
                if (index != -1 && tabLayout.getTabAt(index + 1) != null) {
                    requireActivity().runOnUiThread(() -> {
                        tabLayout.getTabAt(index + 1).setText(hymnal.getName());
                    });
                }

                pendingLoadCount--;

                if (pendingLoadCount == 0) {
                    isLoading = false;
                    requireActivity().runOnUiThread(() -> {
                        showProgress(false);
                        filterSongs();
                        updateEmptyState();

                        String message = "Loaded " + allSongs.size() + " songs from " + loadedHymnals.size() + " hymnal(s)";
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                pendingLoadCount--;

                if (pendingLoadCount == 0 && loadedHymnals.isEmpty()) {
                    requireActivity().runOnUiThread(() -> {
                        showProgress(false);
                        showEmptyState(true, "Failed to load hymnals. Please check your connection and try again.");
                    });
                }
            }
        });
    }

    private void filterSongs() {
        filterSongs(null);
    }

    private void filterSongs(String searchQuery) {
        if (isLoading) return;

        filteredSongs.clear();

        for (SongItem item : allSongs) {
            // Filter by hymnal
            if (currentHymnalFilter != null && !item.getHymnalId().equals(currentHymnalFilter)) {
                continue;
            }

            // Filter by search query
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
        showEmptyState(!hasSongs, "No songs found");
        rvSongs.setVisibility(hasSongs ? View.VISIBLE : View.GONE);
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvSongs.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) {
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(boolean show, String message) {
        if (show) {
            tvEmptyState.setText(message);
            tvEmptyState.setVisibility(View.VISIBLE);
            rvSongs.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateEmptyState() {
        if (filteredSongs.isEmpty() && !isLoading) {
            String message = "No songs available in this hymnal";
            tvEmptyState.setText(message);
            tvEmptyState.setVisibility(View.VISIBLE);
            rvSongs.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvSongs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh hymnals when returning to this fragment
        if (selectedHymnalIds.isEmpty()) {
            loadSelectedHymnals();
        }
    }
}