package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.lhavanguane.tisimu.R;

public class SongDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvTitle, tvNumber, tvLyrics, tvAuthor;
    private MaterialButton btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        initViews();
        setupToolbar();
        displaySongDetails();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvSongTitle);
        tvNumber = findViewById(R.id.tvSongNumber);
        tvLyrics = findViewById(R.id.tvSongLyrics);
        tvAuthor = findViewById(R.id.tvSongAuthor);
        btnShare = findViewById(R.id.btnShare);

        tvLyrics.setMovementMethod(new ScrollingMovementMethod());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void displaySongDetails() {
        String title = getIntent().getStringExtra("SONG_TITLE");
        int number = getIntent().getIntExtra("SONG_NUMBER", 0);
        String lyrics = getIntent().getStringExtra("SONG_LYRICS");
        String author = getIntent().getStringExtra("SONG_AUTHOR");

        tvTitle.setText(title);
        tvNumber.setText("Hymn " + number);
        tvLyrics.setText(lyrics);

        if (author != null && !author.isEmpty()) {
            tvAuthor.setText("By: " + author);
        } else {
            tvAuthor.setVisibility(android.view.View.GONE);
        }

        toolbar.setTitle(title);
    }

    private void setupListeners() {
        btnShare.setOnClickListener(v -> shareLyrics());
    }

    private void shareLyrics() {
        String title = tvTitle.getText().toString();
        String lyrics = tvLyrics.getText().toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + lyrics + "\n\nShared via Tisimu");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}