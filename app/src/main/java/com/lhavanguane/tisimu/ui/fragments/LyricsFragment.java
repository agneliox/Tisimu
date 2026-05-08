package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.data.database.entities.Song;
import com.lhavanguane.tisimu.models.Verse;
import com.lhavanguane.tisimu.ui.adapters.VersesAdapter;
import com.lhavanguane.tisimu.viewmodels.SongDetailViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LyricsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LyricsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView tvSongTitle, tvSongNumber;
    private LinearLayout llLyricsContainer;
    private MaterialButton btnSuggestCorrection, btnShare;
    private SongDetailViewModel viewModel;
    private VersesAdapter versesAdapter;

    private int songId;


    public LyricsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LyricsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LyricsFragment newInstance(String param1, String param2) {
        LyricsFragment fragment = new LyricsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);

        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvSongNumber = view.findViewById(R.id.tvSongNumber);
        llLyricsContainer = view.findViewById(R.id.llLyricsContainer);
        btnSuggestCorrection = view.findViewById(R.id.btnSuggestCorrection);
        btnShare = view.findViewById(R.id.btnShare);

        if (getArguments() != null) {
            songId = getArguments().getInt("SONG_ID", -1);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(SongDetailViewModel.class);

        setupRecyclerView();
        observeData();
        setupListeners();

        return view;
    }

    private void setupRecyclerView() {
        versesAdapter = new VersesAdapter();
        llLyricsContainer.removeAllViews();

        // We'll use the adapter to populate the container
        versesAdapter.setOnVerseClickListener((verse, position) -> {
            // Handle verse click - copy to clipboard or show options
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    requireContext().getSystemService(requireContext().CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("verse", verse.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(requireContext(), "Verse copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeData() {
        viewModel.getSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                displaySong(song);
            }
        });
    }

    private void displaySong(Song song) {
        tvSongTitle.setText(song.getTitle());
        tvSongNumber.setText("No. " + String.format("%03d", song.getNumber()));

        // Parse lyrics into verses
        List<Verse> verses = parseLyricsToVerses(song.getLyrics());
        versesAdapter.setVerses(verses);

        // Manually add views to LinearLayout
        for (int i = 0; i < verses.size(); i++) {
            Verse verse = verses.get(i);
            View verseView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_verse, llLyricsContainer, false);

            TextView tvVerseLabel = verseView.findViewById(R.id.tvVerseLabel);
            TextView tvVerseText = verseView.findViewById(R.id.tvVerseText);
            View verseContainer = verseView.findViewById(R.id.llVerseContainer);

            tvVerseLabel.setText("Verse " + (i + 1));
            tvVerseText.setText(verse.getText());

            final int position = i;
            verseContainer.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Verse " + (position + 1) + " selected", Toast.LENGTH_SHORT).show();
            });

            llLyricsContainer.addView(verseView);
        }
    }

    private List<Verse> parseLyricsToVerses(String lyrics) {
        List<Verse> verses = new ArrayList<>();
        if (lyrics == null) return verses;

        String[] lines = lyrics.split("\n");
        StringBuilder currentVerse = new StringBuilder();

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (currentVerse.length() > 0) {
                    verses.add(new Verse(currentVerse.toString().trim()));
                    currentVerse = new StringBuilder();
                }
            } else {
                if (currentVerse.length() > 0) {
                    currentVerse.append("\n");
                }
                currentVerse.append(line);
            }
        }

        if (currentVerse.length() > 0) {
            verses.add(new Verse(currentVerse.toString().trim()));
        }

        return verses;
    }

    private void setupListeners() {
        btnSuggestCorrection.setOnClickListener(v -> {
            // Show suggestion dialog
            showSuggestionDialog();
        });

        btnShare.setOnClickListener(v -> {
            shareLyrics();
        });
    }

    private void showSuggestionDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_suggest_correction, null);

        com.google.android.material.textfield.TextInputEditText etVerseNumber = dialogView.findViewById(R.id.etVerseNumber);
        com.google.android.material.textfield.TextInputEditText etCurrentText = dialogView.findViewById(R.id.etCurrentText);
        com.google.android.material.textfield.TextInputEditText etSuggestedText = dialogView.findViewById(R.id.etSuggestedText);
        com.google.android.material.textfield.TextInputEditText etJustification = dialogView.findViewById(R.id.etJustification);

        builder.setView(dialogView);
        builder.setCancelable(true);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            String verseNumber = etVerseNumber.getText().toString();
            String currentText = etCurrentText.getText().toString();
            String suggestedText = etSuggestedText.getText().toString();
            String justification = etJustification.getText().toString();

            if (!verseNumber.isEmpty() && !suggestedText.isEmpty()) {
                viewModel.submitSuggestion(
                        Integer.parseInt(verseNumber),
                        currentText,
                        suggestedText,
                        justification
                );
                Toast.makeText(requireContext(), "Suggestion submitted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void shareLyrics() {
        Song song = viewModel.getSong().getValue();
        if (song != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, song.getTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    song.getTitle() + "\n\n" + song.getLyrics() + "\n\nShared via Tisimu App");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }
}