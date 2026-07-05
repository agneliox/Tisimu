package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.SongFirestore;
import com.lhavanguane.tisimu.services.SongFirestoreManager;
import com.lhavanguane.tisimu.ui.activities.SongDetailActivity;
import com.lhavanguane.tisimu.ui.adapters.CommunitySongAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommunitySongsFragment extends Fragment {

    private RecyclerView rvCommunitySongs;
    private TextInputEditText etSearch;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddSong;

    private CommunitySongAdapter adapter;
    private SongFirestoreManager songManager;
    private ListenerRegistration songsListener;
    private List<SongFirestore> allSongs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songManager = SongFirestoreManager.getInstance();

        rvCommunitySongs = view.findViewById(R.id.rvCommunitySongs);
        etSearch = view.findViewById(R.id.etSearch);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        fabAddSong = view.findViewById(R.id.fabAddSong);

        adapter = new CommunitySongAdapter();
        adapter.setOnSongClickListener(this::openSongDetail);
        rvCommunitySongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCommunitySongs.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        fabAddSong.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                com.google.android.material.snackbar.Snackbar.make(view,
                        R.string.sign_in_to_interact, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                return;
            }
            AddCommunitySongDialog.newInstance().show(getChildFragmentManager(), "AddSong");
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (songsListener != null) songsListener.remove();
    }

    private void startListener() {
        songsListener = songManager.listenToCommunitySongs(
                new SongFirestoreManager.SongsCallback() {
                    @Override
                    public void onSuccess(List<SongFirestore> songs) {
                        allSongs = songs;
                        String query = etSearch.getText() != null
                                ? etSearch.getText().toString() : "";
                        filterSongs(query);
                        tvEmptyState.setVisibility(songs.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                    @Override
                    public void onFailure(Exception e) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void filterSongs(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setSongs(allSongs);
            return;
        }
        String lower = query.toLowerCase().trim();
        List<SongFirestore> filtered = new ArrayList<>();
        for (SongFirestore s : allSongs) {
            if (s.getTitle() != null && s.getTitle().toLowerCase().contains(lower)) {
                filtered.add(s);
            }
        }
        adapter.setSongs(filtered);
        tvEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openSongDetail(SongFirestore song) {
        Intent intent = new Intent(requireContext(), SongDetailActivity.class);
        intent.putExtra("SONG_TITLE", song.getTitle());
        intent.putExtra("SONG_LYRICS", song.getLyrics());
        intent.putExtra("SONG_AUTHOR", song.getAuthorName());
        intent.putExtra("SONG_COMPOSER", "");
        intent.putExtra("SONG_NUMBER", 0);
        intent.putExtra("HYMNAL_NAME", getString(R.string.community_songs));
        intent.putExtra("FIRESTORE_SONG_ID", song.getId());
        intent.putExtra("IS_COMMUNITY_SONG", true);
        startActivity(intent);
    }
}
