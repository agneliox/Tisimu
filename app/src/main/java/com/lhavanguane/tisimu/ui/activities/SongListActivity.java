package com.lhavanguane.tisimu.ui.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.data.models.SectionWithSongs;
import com.lhavanguane.tisimu.utils.SelectionManager;
import com.lhavanguane.tisimu.viewmodels.SongListViewModel;

import com.lhavanguane.tisimu.ui.adapters.SongListAdapter;


public class SongListActivity extends AppCompatActivity {


    private MaterialToolbar toolbar;
    private TextView tvHymnalName;
    private SearchView searchView;
    private ProgressBar progressBar;
    private RecyclerView rvSongs;
    private View llEmptyState;

    private SongListViewModel viewModel;
    private SongListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_song_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupViewModel();
        observeData();
        setupListeners();

    }
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvHymnalName = findViewById(R.id.tvHymnalName);
        searchView = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.progressBar);
        rvSongs = findViewById(R.id.rvSongs);
        llEmptyState = findViewById(R.id.llEmptyState);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        tvHymnalName.setText("All Selected Hymnals");
    }

    private void setupRecyclerView() {
        adapter = new SongListAdapter();
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setAdapter(adapter);

        adapter.setOnSongClickListener(song -> {
            Log.d(TAG, "Song clicked: " + song.getTitle());
            Intent intent = new Intent(SongListActivity.this, SongDetailActivity.class);
            intent.putExtra("SONG_ID", song.getId());
            intent.putExtra("SONG_TITLE", song.getTitle());
            startActivity(intent);
        });

        adapter.setOnSectionClickListener(position -> {
            Log.d(TAG, "Section clicked at position: " + position);
            viewModel.toggleSectionExpansion(position);
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SongListViewModel.class);
    }

    private void observeData() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                rvSongs.setVisibility(View.GONE);
                llEmptyState.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getSectionWithSongs().observe(this, sectionWithSongs -> {
            Log.d(TAG, "Sections received: " + (sectionWithSongs != null ? sectionWithSongs.size() : 0));

            if (sectionWithSongs != null && !sectionWithSongs.isEmpty()) {
                adapter.setSections(sectionWithSongs);
                rvSongs.setVisibility(View.VISIBLE);
                llEmptyState.setVisibility(View.GONE);

                // Log first section details
                SectionWithSongs first = sectionWithSongs.get(0);
                Log.d(TAG, "First section: " + first.getSection().getName() +
                        ", Songs: " + first.getSongs().size());
            } else {
                rvSongs.setVisibility(View.GONE);
                llEmptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Search submitted: " + query);
                viewModel.searchSongs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Search changing: " + newText);
                viewModel.searchSongs(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this screen
        if (viewModel != null) {
            viewModel.refresh();
        }
    }
}