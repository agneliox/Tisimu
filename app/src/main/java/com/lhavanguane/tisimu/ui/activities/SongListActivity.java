package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

        for (String hymnalId : selectedIds) {
            storageManager.loadHymnal(hymnalId, new HymnalStorageManager.HymnalLoadCallback() {
                @Override
                public void onSuccess(HymnalData hymnal) {
                    allSongs.addAll(hymnal.getSongs());

                    if (allSongs.size() == getTotalSongsCount(selectedIds)) {
                        // All hymnals loaded
                        filteredSongs.clear();
                        filteredSongs.addAll(allSongs);
                        adapter.setSongs(filteredSongs);
                        toolbar.setSubtitle(allSongs.size() + " songs");
                    }
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(SongListActivity.this, "Failed to load: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}