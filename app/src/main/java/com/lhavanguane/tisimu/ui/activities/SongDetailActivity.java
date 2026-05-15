package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.ui.adapters.VerseAdapter;

import java.util.ArrayList;
import java.util.List;

public class SongDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvSongDetailNumber;
    private TextView tvSongTitle;
    private TextView tvAuthor;
    private TextView tvComposer;
    private RecyclerView rvVerses;
    private MaterialButton btnShare, btnCopyAll;

    private VerseAdapter verseAdapter;
    private List<HymnalData.LyricsSection> sections;

    // Song data
    private int songNumber;
    private String songTitle;
    private String songLyrics;
    private String songAuthor;
    private String songComposer;
    private String hymnalName;
    private List<HymnalData.LyricsSection> structuredVerses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageManager.getInstance(this).updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        // Get data from intent
        getIntentData();

        initViews();
        setupToolbar();
        setupRecyclerView();
        displaySongInfo();
        displayLyrics();
        setupListeners();
    }

    private void getIntentData() {
        songNumber = getIntent().getIntExtra("SONG_NUMBER", 0);
        songTitle = getIntent().getStringExtra("SONG_TITLE");
        songLyrics = getIntent().getStringExtra("SONG_LYRICS");
        songAuthor = getIntent().getStringExtra("SONG_AUTHOR");
        songComposer = getIntent().getStringExtra("SONG_COMPOSER");
        hymnalName = getIntent().getStringExtra("HYMNAL_NAME");

        // Get structured verses if available
        structuredVerses = (List<HymnalData.LyricsSection>) getIntent().getSerializableExtra("STRUCTURED_VERSES");

        // Set default values if null
        if (songTitle == null) songTitle = getString(R.string.song_title);
        if (songLyrics == null) songLyrics = getString(R.string.no_songs_found);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvSongDetailNumber = findViewById(R.id.tvSongDetailNumber);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvComposer = findViewById(R.id.tvComposer);
        rvVerses = findViewById(R.id.rvVerses);
        btnShare = findViewById(R.id.btnShare);
        btnCopyAll = findViewById(R.id.btnCopyAll);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            if (hymnalName != null && !hymnalName.isEmpty()) {
                toolbar.setSubtitle(hymnalName);
            }
        }
    }

    private void setupRecyclerView() {
        sections = new ArrayList<>();
        verseAdapter = new VerseAdapter();
        rvVerses.setLayoutManager(new LinearLayoutManager(this));
        rvVerses.setAdapter(verseAdapter);

        // Complete implementation of both interface methods
        verseAdapter.setOnVerseActionListener(new VerseAdapter.OnVerseActionListener() {
            @Override
            public void onVerseLongClick(HymnalData.LyricsSection section, int position) {
                // Long press already copies in adapter, just show confirmation
                String type = "chorus".equals(section.getType()) ? getString(R.string.label_chorus) : getString(R.string.label_verse);
                Toast.makeText(SongDetailActivity.this,
                        getString(R.string.copy_success, type, section.getLabel()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseClick(HymnalData.LyricsSection section, int position) {
                // Handle verse selection (already highlighted in adapter)
                String type = "chorus".equals(section.getType()) ? getString(R.string.label_chorus) : getString(R.string.label_verse);
                Toast.makeText(SongDetailActivity.this,
                        getString(R.string.item_selected, type, section.getLabel()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySongInfo() {
        tvSongDetailNumber.setText(getString(R.string.hymn_prefix, String.valueOf(songNumber)));
        tvSongTitle.setText(songTitle);

        if (songAuthor != null && !songAuthor.isEmpty() && !songAuthor.equals("null")) {
            tvAuthor.setText(getString(R.string.words_by, songAuthor));
            tvAuthor.setVisibility(View.VISIBLE);
        } else {
            tvAuthor.setVisibility(View.GONE);
        }

        if (songComposer != null && !songComposer.isEmpty() && !songComposer.equals("null")) {
            tvComposer.setText(getString(R.string.music_by, songComposer));
            tvComposer.setVisibility(View.VISIBLE);
        } else {
            tvComposer.setVisibility(View.GONE);
        }
    }

    private void displayLyrics() {
        // Try to use structured verses first
        if (structuredVerses != null && !structuredVerses.isEmpty()) {
            sections.clear();
            sections.addAll(structuredVerses);
            verseAdapter.setSections(sections);
        } else {
            // Fallback to parsing plain text lyrics
            parseAndDisplayLyrics();
        }
    }

    private void parseAndDisplayLyrics() {
        String[] lines = songLyrics.split("\n");
        List<String> verseTexts = new ArrayList<>();
        StringBuilder currentVerse = new StringBuilder();
        int verseNumber = 1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (currentVerse.length() > 0) {
                    // Create a verse section
                    HymnalData.LyricsSection section = new HymnalData.LyricsSection();
                    section.setType("verse");
                    section.setNumber(verseNumber);
                    section.setLabel(String.valueOf(verseNumber));

                    List<String> verseLines = new ArrayList<>();
                    verseLines.add(currentVerse.toString().trim());
                    section.setLines(verseLines);

                    sections.add(section);
                    verseNumber++;
                    currentVerse = new StringBuilder();
                }
            } else {
                if (currentVerse.length() > 0) {
                    currentVerse.append("\n");
                }
                currentVerse.append(line);
            }
        }

        // Add the last verse
        if (currentVerse.length() > 0) {
            HymnalData.LyricsSection section = new HymnalData.LyricsSection();
            section.setType("verse");
            section.setNumber(verseNumber);
            section.setLabel(String.valueOf(verseNumber));

            List<String> verseLines = new ArrayList<>();
            verseLines.add(currentVerse.toString().trim());
            section.setLines(verseLines);

            sections.add(section);
        }

        // If no empty lines were found, treat as single verse
        if (sections.isEmpty() && lines.length > 0) {
            HymnalData.LyricsSection section = new HymnalData.LyricsSection();
            section.setType("verse");
            section.setNumber(1);
            section.setLabel("1");

            List<String> verseLines = new ArrayList<>();
            StringBuilder singleVerse = new StringBuilder();
            for (String line : lines) {
                if (singleVerse.length() > 0) singleVerse.append("\n");
                singleVerse.append(line);
            }
            verseLines.add(singleVerse.toString());
            section.setLines(verseLines);

            sections.add(section);
        }

        verseAdapter.setSections(sections);
    }

    private void setupListeners() {
        btnShare.setOnClickListener(v -> shareAllLyrics());
        btnCopyAll.setOnClickListener(v -> {
            if (verseAdapter != null) {
                verseAdapter.copyAllSectionsToClipboard(this);
            }
        });
    }

    private void shareAllLyrics() {
        StringBuilder shareContent = new StringBuilder();
        shareContent.append(songTitle).append("\n");
        shareContent.append(getString(R.string.hymn_prefix, String.format("%03d", songNumber))).append("\n\n");

        if (songAuthor != null && !songAuthor.isEmpty()) {
            shareContent.append(getString(R.string.words_by, songAuthor)).append("\n\n");
        }

        if (verseAdapter != null) {
            shareContent.append(verseAdapter.getAllSectionsText());
        } else {
            shareContent.append(songLyrics);
        }

        shareContent.append("\n\n").append(getString(R.string.shared_via_tisimu));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, songTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
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
        } else if (item.getItemId() == R.id.action_share_song) {
            shareAllLyrics();
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