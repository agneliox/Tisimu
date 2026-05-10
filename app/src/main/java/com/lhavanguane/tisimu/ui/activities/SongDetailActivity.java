package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Verse;
import com.lhavanguane.tisimu.ui.adapters.VerseAdapter;

import java.util.ArrayList;
import java.util.List;

public class SongDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private Chip chipSongNumber;
    private TextView tvSongTitle;
    private TextView tvAuthor;
    private TextView tvComposer;
    private RecyclerView rvVerses;
    private MaterialButton btnShare, btnCopyAll;

    private VerseAdapter verseAdapter;
    private List<Verse> verses;

    // Song data
    private int songNumber;
    private String songTitle;
    private String songLyrics;
    private String songAuthor;
    private String songComposer;
    private String hymnalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_song_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get data from intent

        // Get hymnal name from intent
        hymnalName = getIntent().getStringExtra("HYMNAL_NAME");

        getIntentData();

        initViews();
        setupToolbar();
        setupRecyclerView();
        displaySongInfo();
        parseAndDisplayLyrics();
        setupListeners();
    }

    private void getIntentData() {
        songNumber = getIntent().getIntExtra("SONG_NUMBER", 0);
        songTitle = getIntent().getStringExtra("SONG_TITLE");
        songLyrics = getIntent().getStringExtra("SONG_LYRICS");
        songAuthor = getIntent().getStringExtra("SONG_AUTHOR");
        songComposer = getIntent().getStringExtra("SONG_COMPOSER");
        hymnalName = getIntent().getStringExtra("HYMNAL_NAME");

        // Set default values if null
        if (songTitle == null) songTitle = "Unknown Title";
        if (songLyrics == null) songLyrics = "Lyrics not available";
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        chipSongNumber = findViewById(R.id.chipSongNumber);
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
        verses = new ArrayList<>();
        verseAdapter = new VerseAdapter();
        rvVerses.setLayoutManager(new LinearLayoutManager(this));
        rvVerses.setAdapter(verseAdapter);

        verseAdapter.setOnVerseActionListener(new VerseAdapter.OnVerseActionListener() {
            @Override
            public void onVerseLongClick(Verse verse, int position) {
                // Verse already copied in adapter, just show additional feedback
                Toast.makeText(SongDetailActivity.this, "Verse " + verse.getNumber() + " copied!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseClick(Verse verse, int position) {
                // Verse selection handled in adapter
            }
        });
    }

    private void displaySongInfo() {
        chipSongNumber.setText("Hymn " + String.format("%03d", songNumber));
        tvSongTitle.setText(songTitle);

        if (songAuthor != null && !songAuthor.isEmpty() && !songAuthor.equals("null")) {
            tvAuthor.setText("Words by: " + songAuthor);
            tvAuthor.setVisibility(View.VISIBLE);
        }

        if (songComposer != null && !songComposer.isEmpty() && !songComposer.equals("null")) {
            tvComposer.setText("Music by: " + songComposer);
            tvComposer.setVisibility(View.VISIBLE);
        }
    }

    private void parseAndDisplayLyrics() {
        String[] lines = songLyrics.split("\n");
        List<String> verseTexts = new ArrayList<>();
        StringBuilder currentVerse = new StringBuilder();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (currentVerse.length() > 0) {
                    verseTexts.add(currentVerse.toString().trim());
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
            verseTexts.add(currentVerse.toString().trim());
        }

        // If no empty lines were found, treat each line as a separate verse part
        if (verseTexts.isEmpty() && lines.length > 0) {
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    verseTexts.add(line.trim());
                }
            }
        }

        // Create Verse objects
        for (int i = 0; i < verseTexts.size(); i++) {
            verses.add(new Verse(i + 1, verseTexts.get(i)));
        }

        verseAdapter.setVerses(verses);
    }

    private void setupListeners() {
        btnShare.setOnClickListener(v -> shareAllLyrics());
        btnCopyAll.setOnClickListener(v -> verseAdapter.copyAllVersesToClipboard(this));
    }

    private void shareAllLyrics() {
        StringBuilder shareContent = new StringBuilder();
        shareContent.append(songTitle).append("\n");
        shareContent.append("Hymn ").append(String.format("%03d", songNumber)).append("\n\n");

        if (songAuthor != null && !songAuthor.isEmpty()) {
            shareContent.append("By: ").append(songAuthor).append("\n\n");
        }

        shareContent.append(verseAdapter.getAllVersesText());
        shareContent.append("\n\nShared via Tisimu App");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, songTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void shareVerse(int verseNumber, String verseText) {
        StringBuilder shareContent = new StringBuilder();
        shareContent.append(songTitle).append("\n");
        shareContent.append("Verse ").append(verseNumber).append("\n\n");
        shareContent.append(verseText);
        shareContent.append("\n\nShared via Tisimu App");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, songTitle + " - Verse " + verseNumber);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
        startActivity(Intent.createChooser(shareIntent, "Share verse via"));
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