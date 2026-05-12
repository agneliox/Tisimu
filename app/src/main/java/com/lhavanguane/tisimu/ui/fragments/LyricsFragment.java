package com.lhavanguane.tisimu.ui.fragments;

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
import com.lhavanguane.tisimu.models.HymnalData;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);

        // Get data from arguments
        if (getArguments() != null) {
            songNumber = getArguments().getInt("SONG_NUMBER", 0);
            songTitle = getArguments().getString("SONG_TITLE");
            songLyrics = getArguments().getString("SONG_LYRICS");
            songAuthor = getArguments().getString("SONG_AUTHOR");
            songComposer = getArguments().getString("SONG_COMPOSER");
            hymnalName = getArguments().getString("HYMNAL_NAME");
            structuredVerses = (List<HymnalData.LyricsSection>) getArguments().getSerializable("STRUCTURED_VERSES");
        }

        // Set default values if null
        if (songTitle == null) songTitle = "Unknown Title";
        if (songLyrics == null) songLyrics = "Lyrics not available";

        initViews(view);
        setupRecyclerView();
        displaySongInfo();
        displayLyrics();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
//        chipSongNumber = view.findViewById(R.id.tvSongDetailNumber);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvComposer = view.findViewById(R.id.tvComposer);
        rvVerses = view.findViewById(R.id.rvVerses);
        btnShare = view.findViewById(R.id.btnShare);
        btnCopyAll = view.findViewById(R.id.btnCopyAll);
    }

    private void setupRecyclerView() {
        sections = new ArrayList<>();
        verseAdapter = new VerseAdapter();
        rvVerses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVerses.setAdapter(verseAdapter);

        // Complete implementation of both interface methods
        verseAdapter.setOnVerseActionListener(new VerseAdapter.OnVerseActionListener() {
            @Override
            public void onVerseLongClick(HymnalData.LyricsSection section, int position) {
                // Long press already copies in adapter, just show confirmation
                String type = "verse".equals(section.getType()) ? "Verse" : "Chorus";
                Toast.makeText(getContext(),
                        type + " " + section.getLabel() + " copied!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseClick(HymnalData.LyricsSection section, int position) {
                // Handle verse selection (already highlighted in adapter)
                String type = "verse".equals(section.getType()) ? "Verse" : "Chorus";
                Toast.makeText(getContext(),
                        type + " " + section.getLabel() + " selected", Toast.LENGTH_SHORT).show();
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

        // Set toolbar subtitle through activity if needed
        if (getActivity() != null && hymnalName != null && !hymnalName.isEmpty()) {
            getActivity().setTitle(hymnalName);
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
                verseAdapter.copyAllSectionsToClipboard(requireContext());
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
            shareContent.append(verseAdapter.getAllSectionsText());
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