package com.lhavanguane.tisimu.ui.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.ui.adapters.SongDetailPagerAdapter;
import com.lhavanguane.tisimu.viewmodels.SongDetailViewModel;

public class SongDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SongDetailViewModel viewModel;
    private SongDetailPagerAdapter pagerAdapter;

    private int songId;
    private String songTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_song_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int songId = getIntent().getIntExtra("SONG_ID", -1);
        String songTitle = getIntent().getStringExtra("SONG_TITLE");

//        TextView tvTitle = findViewById(R.id.tvSongTitle);
//        tvTitle.setText(songTitle);
        // Get intent data
        songId = getIntent().getIntExtra("SONG_ID", -1);
        songTitle = getIntent().getStringExtra("SONG_TITLE");

        if (songId == -1) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewModel();
        setupViewPager();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(songTitle != null ? songTitle : "Song Detail");
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SongDetailViewModel.class);
        viewModel.loadSong(songId);
    }

    private void setupViewPager() {
        pagerAdapter = new SongDetailPagerAdapter(this, songId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Lyrics");
                            break;
                        case 1:
                            tab.setText("Comments");
                            break;
                        case 2:
                            tab.setText("Melodies");
                            break;
                    }
                }
        ).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            shareSong();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareSong() {
        // Share functionality will be handled by the LyricsFragment
        // This is a fallback
        android.widget.Toast.makeText(this, "Share from Lyrics tab", android.widget.Toast.LENGTH_SHORT).show();
    }
}