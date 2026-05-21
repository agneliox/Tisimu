package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.models.SongItem;
import com.lhavanguane.tisimu.services.HymnalStorageManager;
import com.lhavanguane.tisimu.ui.adapters.SongAdapter;
import com.lhavanguane.tisimu.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SongListActivity extends AppCompatActivity {

    private static final String TAG = "SongListActivity";

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvSongs;
    private SearchView searchView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply language before super.onCreate
        com.lhavanguane.tisimu.utils.LanguageManager.getInstance(this).updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        storageManager = new HymnalStorageManager(this);
        preferencesManager = PreferencesManager.getInstance(this);

        allSongs = new ArrayList<>();
        filteredSongs = new ArrayList<>();
        loadedHymnals = new HashMap<>();
        selectedHymnalIds = new ArrayList<>();

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadSelectedHymnals();
    }

    private void initViews() {
        toolbar = findViewById(R.id.songListActivityToolbar);
        tabLayout = findViewById(R.id.tabLayout);
        rvSongs = findViewById(R.id.rvSongs);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.gospel_hymns_library);
        }
    }

    private void setupRecyclerView() {
        adapter = new SongAdapter();
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setAdapter(adapter);

        adapter.setOnSongClickListener(songItem -> {
            if (songItem instanceof SongItem) {
                SongItem item = songItem;
                Intent intent = new Intent(SongListActivity.this, SongDetailActivity.class);
                intent.putExtra("SONG_NUMBER", item.getSong().getNumber());
                intent.putExtra("SONG_TITLE", item.getSong().getTitle());
                intent.putExtra("SONG_LYRICS", item.getSong().getLyrics());
                intent.putExtra("SONG_AUTHOR", item.getSong().getAuthor());
                intent.putExtra("SONG_COMPOSER", item.getSong().getComposer());
                intent.putExtra("HYMNAL_NAME", item.getHymnalName());
                startActivity(intent);
            }
        });
    }

    private void loadSelectedHymnals() {
        Set<String> selectedIds = preferencesManager.getSelectedHymnals();

        if (selectedIds.isEmpty()) {
            Toast.makeText(this, R.string.try_a_different_search_or_select_more_hymnals, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        selectedHymnalIds.clear();
        selectedHymnalIds.addAll(selectedIds);
        pendingLoadCount = selectedHymnalIds.size();

        Toast.makeText(this, getString(R.string.loading_hymnals, pendingLoadCount), Toast.LENGTH_SHORT).show();

        // Setup tab layout for filtering
        setupTabLayout();

        // Load each hymnal
        for (String hymnalId : selectedHymnalIds) {
            loadHymnal(hymnalId);
        }
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.all_selected_hymnals));

        for (String hymnalId : selectedHymnalIds) {
            // We'll update tab titles after loading hymnal names
            tabLayout.addTab(tabLayout.newTab().setText(hymnalId));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    currentHymnalFilter = null;
                    filterSongs();
                    updateToolbarTitle();
                } else if (position - 1 < selectedHymnalIds.size()) {
                    currentHymnalFilter = selectedHymnalIds.get(position - 1);
                    filterSongs();
                    updateToolbarTitle();
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
                loadedHymnals.put(hymnalId, hymnal);

                // Add all songs from this hymnal to the list
                for (HymnalData.Song song : hymnal.getSongs()) {
                    allSongs.add(new SongItem(hymnalId, hymnal.getName(), song));
                }

                // Update tab title with actual hymnal name
                int index = selectedHymnalIds.indexOf(hymnalId);
                if (index != -1 && tabLayout.getTabAt(index + 1) != null) {
                    tabLayout.getTabAt(index + 1).setText(hymnal.getName());
                }

                pendingLoadCount--;

                if (pendingLoadCount == 0) {
                    // All hymnals loaded
                    runOnUiThread(() -> {
                        filterSongs();
                        updateToolbarTitle();

                        String message = getString(R.string.copy_success, String.valueOf(allSongs.size()), getString(R.string._0_songs).replace("0", "").trim()) + " " + getString(R.string.from) + " " + loadedHymnals.size() + " " + getString(R.string.hymnal);
                        // Let's refine this, we need a better string for "Loaded X songs from Y hymnals"
                        // I'll add "loaded_info" to strings.xml
                        Toast.makeText(SongListActivity.this, getString(R.string.loaded_info, allSongs.size(), loadedHymnals.size()), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to load hymnal: " + hymnalId + " - " + error);
                pendingLoadCount--;

                if (pendingLoadCount == 0 && loadedHymnals.isEmpty()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SongListActivity.this, R.string.failed_load_hymnals, Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
            }
        });
    }

    private void filterSongs() {
        filterSongs(null);
    }

    private void filterSongs(String searchQuery) {
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

        // Update empty state
        if (filteredSongs.isEmpty()) {
            Toast.makeText(this, R.string.no_songs_found, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateToolbarTitle() {
        int totalSongs = allSongs.size();
        int hymnalCount = loadedHymnals.size();

        if (getSupportActionBar() != null) {
            if (currentHymnalFilter == null) {
                getSupportActionBar().setTitle(R.string.gospel_hymns_library);
                getSupportActionBar().setSubtitle(totalSongs + " " + getString(R.string._0_songs).replace("0", "") + " • " + hymnalCount + " " + getString(R.string.hymnal).toLowerCase());
            } else {
                HymnalData hymnal = loadedHymnals.get(currentHymnalFilter);
                if (hymnal != null) {
                    getSupportActionBar().setTitle(hymnal.getName());
                    getSupportActionBar().setSubtitle(hymnal.getSongs().size() + " " + getString(R.string._0_songs).replace("0", ""));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_change_hymnals) {
            Intent intent = new Intent(SongListActivity.this, HymnalSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}