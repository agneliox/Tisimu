package com.lhavanguane.tisimu.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Verse;
import com.lhavanguane.tisimu.ui.adapters.VerseAdapter;

import java.util.ArrayList;
import java.util.List;

public class LyricsFragment extends Fragment {

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);

        // Get data from arguments
        if (getArguments() != null) {
            songNumber = getArguments().getInt("SONG_NUMBER", 0);
            songTitle = getArguments().getString("SONG_TITLE");
            songLyrics = getArguments().getString("SONG_LYRICS");
            songAuthor = getArguments().getString("SONG_AUTHOR");
            songComposer = getArguments().getString("SONG_COMPOSER");
        }

        // Set default values if null
        if (songTitle == null) songTitle = "Unknown Title";
        if (songLyrics == null) songLyrics = "Lyrics not available";

        initViews(view);
        setupRecyclerView();
        displaySongInfo();
        parseAndDisplayLyrics();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        chipSongNumber = view.findViewById(R.id.chipSongNumber);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvComposer = view.findViewById(R.id.tvComposer);
        rvVerses = view.findViewById(R.id.rvVerses);
        btnShare = view.findViewById(R.id.btnShare);
        btnCopyAll = view.findViewById(R.id.btnCopyAll);
    }

    private void setupRecyclerView() {
        verses = new ArrayList<>();
        verseAdapter = new VerseAdapter();
        rvVerses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVerses.setAdapter(verseAdapter);

        // Use setOnVerseActionListener instead of setOnVerseClickListener
        verseAdapter.setOnVerseActionListener(new VerseAdapter.OnVerseActionListener() {
            @Override
            public void onVerseLongClick(Verse verse, int position) {
                // Long press already copies in adapter, just show confirmation
                Toast.makeText(getContext(), "Verse " + verse.getNumber() + " copied!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseClick(Verse verse, int position) {
                // Handle verse selection (already highlighted in adapter)
                Toast.makeText(getContext(), "Verse " + verse.getNumber() + " selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySongInfo() {
        chipSongNumber.setText("Hymn " + String.format("%03d", songNumber));
        tvSongTitle.setText(songTitle);

        if (songAuthor != null && !songAuthor.isEmpty() && !songAuthor.equals("null")) {
            tvAuthor.setText("Words by: " + songAuthor);
            tvAuthor.setVisibility(View.VISIBLE);
        } else {
            tvAuthor.setVisibility(View.GONE);
        }

        if (songComposer != null && !songComposer.isEmpty() && !songComposer.equals("null")) {
            tvComposer.setText("Music by: " + songComposer);
            tvComposer.setVisibility(View.VISIBLE);
        } else {
            tvComposer.setVisibility(View.GONE);
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
        verses.clear();
        for (int i = 0; i < verseTexts.size(); i++) {
            verses.add(new Verse(i + 1, verseTexts.get(i)));
        }

        verseAdapter.setVerses(verses);
    }

    private void setupListeners() {
        btnShare.setOnClickListener(v -> shareAllLyrics());
        btnCopyAll.setOnClickListener(v -> {
            if (verseAdapter != null) {
                verseAdapter.copyAllVersesToClipboard(requireContext());
            }
        });
    }

    private void shareAllLyrics() {
        StringBuilder shareContent = new StringBuilder();
        shareContent.append(songTitle).append("\n");
        shareContent.append("Hymn ").append(String.format("%03d", songNumber)).append("\n\n");

        if (songAuthor != null && !songAuthor.isEmpty()) {
            shareContent.append("By: ").append(songAuthor).append("\n\n");
        }

        if (verseAdapter != null) {
            shareContent.append(verseAdapter.getAllVersesText());
        } else {
            shareContent.append(songLyrics);
        }

        shareContent.append("\n\nShared via Tisimu App");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, songTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.toString());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}