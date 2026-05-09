package com.lhavanguane.tisimu.ui.activities;

import static android.content.ContentValues.TAG;

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

import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.services.HymnalStorageManager;
import com.lhavanguane.tisimu.ui.adapters.SongAdapter;
import com.lhavanguane.tisimu.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SongListActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvSongs;

    private HymnalStorageManager storageManager;
    private PreferencesManager preferencesManager;
    private SongAdapter adapter;
    private List<HymnalData.Song> allSongs;
    private List<HymnalData.Song> filteredSongs;
    private String currentHymnalId;
    private HymnalData currentHymnal;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        storageManager = new HymnalStorageManager(this);
        preferencesManager = PreferencesManager.getInstance(this);
        allSongs = new ArrayList<>();
        filteredSongs = new ArrayList<>();

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadSelectedHymnals();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvSongs = findViewById(R.id.rvSongs);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Hymns");
        }
    }

    private void loadSelectedHymnals() {
        Set<String> selectedIds = preferencesManager.getSelectedHymnals();

        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "No hymnals selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(this, "Loading " + selectedIds.size() + " hymnals...", Toast.LENGTH_SHORT).show();

        // For now, load the first selected hymnal
        // In a more advanced version, you could load multiple
        currentHymnalId = selectedIds.iterator().next();

        storageManager.loadHymnal(currentHymnalId, new HymnalStorageManager.HymnalLoadCallback() {
            @Override
            public void onSuccess(HymnalData hymnal) {
                currentHymnal = hymnal;
                allSongs.clear();
                allSongs.addAll(hymnal.getSongs());
                filteredSongs.clear();
                filteredSongs.addAll(allSongs);

                runOnUiThread(() -> {
                    adapter.setSongs(filteredSongs);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(hymnal.getName());
                        getSupportActionBar().setSubtitle(hymnal.getSongs().size() + " songs");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Failed to load hymnal: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(SongListActivity.this, "Failed to load hymnal: " + error, Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        });
    }

    private int getTotalSongsCount(Set<String> hymnalIds) {
        // This is approximate - we'll load all anyway
        return hymnalIds.size() * 100; // Placeholder
    }

    private void setupRecyclerView() {
        adapter = new SongAdapter();
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setAdapter(adapter);

        adapter.setOnSongClickListener(song -> {
            Intent intent = new Intent(SongListActivity.this, SongDetailActivity.class);
            intent.putExtra("SONG_NUMBER", song.getNumber());
            intent.putExtra("SONG_TITLE", song.getTitle());
            intent.putExtra("SONG_LYRICS", song.getLyrics());
            intent.putExtra("SONG_AUTHOR", song.getAuthor());
            startActivity(intent);
        });
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

    private void filterSongs(String query) {
        filteredSongs.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredSongs.addAll(allSongs);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (HymnalData.Song song : allSongs) {
                if (song.getTitle().toLowerCase().contains(lowerQuery) ||
                        String.valueOf(song.getNumber()).contains(lowerQuery)) {
                    filteredSongs.add(song);
                }
            }
        }

        adapter.setSongs(filteredSongs);

        if (filteredSongs.isEmpty()) {
            Toast.makeText(this, "No songs found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "Home pressed...", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_change_hymnals) {
            Toast.makeText(this, "Changing hymns...", Toast.LENGTH_SHORT).show();
            // Go back to hymnal selection
            Intent intent = new Intent(SongListActivity.this, HymnalSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}