package com.lhavanguane.tisimu.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.lhavanguane.tisimu.utils.LanguageManager;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalData;
import com.lhavanguane.tisimu.models.SongFirestore;
import com.lhavanguane.tisimu.models.SongMelody;
import com.lhavanguane.tisimu.services.SongFirestoreManager;
import com.lhavanguane.tisimu.ui.adapters.MelodyAdapter;
import com.lhavanguane.tisimu.ui.adapters.VerseAdapter;
import com.lhavanguane.tisimu.ui.fragments.AddMelodyBottomSheet;
import com.lhavanguane.tisimu.ui.fragments.CommentsBottomSheet;
import com.google.firebase.firestore.ListenerRegistration;

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

    // Social views
    private LinearLayout llSocialRow;
    private LinearLayout llMelodiesSection;
    private MaterialButton btnLikeSong;
    private MaterialButton btnShowComments;
    private MaterialButton btnAddMelody;
    private RecyclerView rvMelodies;

    private VerseAdapter verseAdapter;
    private MelodyAdapter melodyAdapter;
    private List<HymnalData.LyricsSection> sections;

    // Song data
    private int songNumber;
    private String songTitle;
    private String songLyrics;
    private String songAuthor;
    private String songComposer;
    private String hymnalName;
    private List<HymnalData.LyricsSection> structuredVerses;

    // Social data
    private String songId;
    private boolean isCommunitySong;
    private SongFirestoreManager songManager;
    private ExoPlayer exoPlayer;
    private ListenerRegistration melodiesListener;
    private ListenerRegistration songListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageManager.getInstance(this).updateAppLanguage(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_song_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getIntentData();
        initViews();
        setupToolbar();
        setupRecyclerView();
        displaySongInfo();
        displayLyrics();
        setupListeners();
        initSocialFeatures();
    }

    private void getIntentData() {
        songNumber = getIntent().getIntExtra("SONG_NUMBER", 0);
        songTitle = getIntent().getStringExtra("SONG_TITLE");
        songLyrics = getIntent().getStringExtra("SONG_LYRICS");
        songAuthor = getIntent().getStringExtra("SONG_AUTHOR");
        songComposer = getIntent().getStringExtra("SONG_COMPOSER");
        hymnalName = getIntent().getStringExtra("HYMNAL_NAME");
        structuredVerses = (List<HymnalData.LyricsSection>) getIntent().getSerializableExtra("STRUCTURED_VERSES");

        if (songTitle == null) songTitle = getString(R.string.song_title);
        if (songLyrics == null) songLyrics = getString(R.string.no_songs_found);

        // Resolve Firestore song ID
        String hymnalId = getIntent().getStringExtra("HYMNAL_ID");
        String firestoreSongId = getIntent().getStringExtra("FIRESTORE_SONG_ID");
        isCommunitySong = getIntent().getBooleanExtra("IS_COMMUNITY_SONG", false);

        if (firestoreSongId != null) {
            songId = firestoreSongId;
        } else if (hymnalId != null && songNumber > 0) {
            songId = SongFirestoreManager.buildHymnalSongId(hymnalId, songNumber);
        } else {
            songId = null;
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.song_detail_toolbar);
        tvSongDetailNumber = findViewById(R.id.tvSongDetailNumber);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvComposer = findViewById(R.id.tvComposer);
        rvVerses = findViewById(R.id.rvVerses);
        btnShare = findViewById(R.id.btnShare);
        btnCopyAll = findViewById(R.id.btnCopyAll);

        llSocialRow = findViewById(R.id.llSocialRow);
        llMelodiesSection = findViewById(R.id.llMelodiesSection);
        btnLikeSong = findViewById(R.id.btnLikeSong);
        btnShowComments = findViewById(R.id.btnShowComments);
        btnAddMelody = findViewById(R.id.btnAddMelody);
        rvMelodies = findViewById(R.id.rvMelodies);
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

        verseAdapter.setOnVerseActionListener(new VerseAdapter.OnVerseActionListener() {
            @Override
            public void onVerseLongClick(HymnalData.LyricsSection section, int position) {
                String type = "chorus".equals(section.getType()) ? getString(R.string.label_chorus) : getString(R.string.label_verse);
                Toast.makeText(SongDetailActivity.this,
                        getString(R.string.copy_success, type, section.getLabel()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseClick(HymnalData.LyricsSection section, int position) {
                String type = "chorus".equals(section.getType()) ? getString(R.string.label_chorus) : getString(R.string.label_verse);
                Toast.makeText(SongDetailActivity.this,
                        getString(R.string.item_selected, type, section.getLabel()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySongInfo() {
        if (isCommunitySong) {
            tvSongDetailNumber.setVisibility(View.GONE);
        } else {
            tvSongDetailNumber.setText(getString(R.string.hymn_prefix, String.valueOf(songNumber)));
        }
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
        if (structuredVerses != null && !structuredVerses.isEmpty()) {
            sections.clear();
            sections.addAll(structuredVerses);
            verseAdapter.setSections(sections);
        } else {
            parseAndDisplayLyrics();
        }
    }

    private void parseAndDisplayLyrics() {
        if (songLyrics == null || songLyrics.isEmpty()) return;
        String[] lines = songLyrics.split("\n");
        StringBuilder currentVerse = new StringBuilder();
        int verseNumber = 1;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                if (currentVerse.length() > 0) {
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
                if (currentVerse.length() > 0) currentVerse.append("\n");
                currentVerse.append(line);
            }
        }

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

        if (sections.isEmpty() && lines.length > 0) {
            HymnalData.LyricsSection section = new HymnalData.LyricsSection();
            section.setType("verse");
            section.setNumber(1);
            section.setLabel("1");
            List<String> verseLines = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(line);
            }
            verseLines.add(sb.toString());
            section.setLines(verseLines);
            sections.add(section);
        }

        verseAdapter.setSections(sections);
    }

    private void setupListeners() {
        btnShare.setOnClickListener(v -> shareAllLyrics());
        btnCopyAll.setOnClickListener(v -> {
            if (verseAdapter != null) verseAdapter.copyAllSectionsToClipboard(this);
        });
    }

    // ── Social features ──────────────────────────────────────────────

    private void initSocialFeatures() {
        if (songId == null) {
            llSocialRow.setVisibility(View.GONE);
            return;
        }

        songManager = SongFirestoreManager.getInstance();
        exoPlayer = new ExoPlayer.Builder(this).build();

        melodyAdapter = new MelodyAdapter();
        melodyAdapter.setExoPlayer(exoPlayer);
        rvMelodies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMelodies.setAdapter(melodyAdapter);

        melodyAdapter.setOnMelodyActionListener(new MelodyAdapter.OnMelodyActionListener() {
            @Override
            public void onLikeClick(SongMelody melody) {
                if (requireSignIn()) return;
                songManager.toggleMelodyLike(songId, melody.getId(),
                        new SongFirestoreManager.VoidCallback() {
                            @Override public void onSuccess() {}
                            @Override public void onFailure(Exception e) {
                                showToast(getString(R.string.error_generic));
                            }
                        });
            }

            @Override
            public void onPlayAudio(SongMelody melody, int position) {
                MediaItem mediaItem = MediaItem.fromUri(melody.getUrl());
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            }

            @Override
            public void onVideoClick(SongMelody melody) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(melody.getUrl()));
                startActivity(intent);
            }
        });

        btnLikeSong.setOnClickListener(v -> handleLikeSong());
        btnShowComments.setOnClickListener(v -> openCommentsSheet());
        btnAddMelody.setOnClickListener(v -> openAddMelodySheet());

        songManager.ensureSongDocument(songId, songTitle, isCommunitySong,
                new SongFirestoreManager.VoidCallback() {
                    @Override public void onSuccess() { startListeners(); }
                    @Override public void onFailure(Exception e) {
                        // Social unavailable — hide social row silently
                        llSocialRow.setVisibility(View.GONE);
                    }
                });
    }

    private void startListeners() {
        songListener = songManager.listenToSong(songId, new SongFirestoreManager.SongCallback() {
            @Override
            public void onSuccess(SongFirestore song) {
                updateLikeButton(song.getLikesCount(), false);
                int count = song.getCommentCount();
                btnShowComments.setText(count > 0
                        ? count + " " + getString(R.string.comments)
                        : getString(R.string.add_a_comment));
            }
            @Override public void onFailure(Exception e) {}
        });

        melodiesListener = songManager.listenToMelodies(songId,
                new SongFirestoreManager.MelodiesCallback() {
                    @Override
                    public void onSuccess(List<SongMelody> melodies) {
                        melodyAdapter.setMelodies(melodies);
                        llMelodiesSection.setVisibility(melodies.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                    @Override public void onFailure(Exception e) {}
                });

        songManager.isCurrentUserLikedSong(songId, liked -> updateLikeButton(0, liked));
    }

    private void handleLikeSong() {
        if (requireSignIn()) return;
        songManager.toggleSongLike(songId, new SongFirestoreManager.VoidCallback() {
            @Override public void onSuccess() {
                songManager.isCurrentUserLikedSong(songId,
                        liked -> updateLikeButton(0, liked));
            }
            @Override public void onFailure(Exception e) {
                showToast(getString(R.string.error_generic));
            }
        });
    }

    private void updateLikeButton(int count, boolean liked) {
        btnLikeSong.setAlpha(liked ? 1.0f : 0.5f);
    }

    private void openCommentsSheet() {
        CommentsBottomSheet sheet = CommentsBottomSheet.newInstance(songId);
        sheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
    }

    private void openAddMelodySheet() {
        if (requireSignIn()) return;
        AddMelodyBottomSheet sheet = AddMelodyBottomSheet.newInstance(songId);
        sheet.show(getSupportFragmentManager(), "AddMelodyBottomSheet");
    }

    private boolean requireSignIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showToast(getString(R.string.sign_in_to_interact));
            return true;
        }
        return false;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // ── Lyrics sharing ───────────────────────────────────────────────

    private void shareAllLyrics() {
        StringBuilder shareContent = new StringBuilder();
        shareContent.append(songTitle).append("\n");
        if (!isCommunitySong) {
            shareContent.append(getString(R.string.hymn_prefix, String.format("%03d", songNumber))).append("\n\n");
        }
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
        } else if (item.getItemId() == R.id.action_copy_song) {
            if (verseAdapter != null) verseAdapter.copyAllSectionsToClipboard(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) exoPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (melodiesListener != null) melodiesListener.remove();
        if (songListener != null) songListener.remove();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
