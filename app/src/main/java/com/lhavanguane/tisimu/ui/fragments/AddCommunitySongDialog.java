package com.lhavanguane.tisimu.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.services.SongFirestoreManager;

public class AddCommunitySongDialog extends BottomSheetDialogFragment {

    private TextInputLayout tilSongTitle;
    private TextInputLayout tilSongLyrics;
    private TextInputEditText etSongTitle;
    private TextInputEditText etSongLyrics;
    private TextInputEditText etSongAuthor;
    private TextInputEditText etSongComposer;

    public static AddCommunitySongDialog newInstance() {
        return new AddCommunitySongDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_community_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilSongTitle = view.findViewById(R.id.tilSongTitle);
        tilSongLyrics = view.findViewById(R.id.tilSongLyrics);
        etSongTitle = view.findViewById(R.id.etSongTitle);
        etSongLyrics = view.findViewById(R.id.etSongLyrics);
        etSongAuthor = view.findViewById(R.id.etSongAuthor);
        etSongComposer = view.findViewById(R.id.etSongComposer);

        MaterialButton btnCancel = view.findViewById(R.id.btnCancelSong);
        MaterialButton btnSubmit = view.findViewById(R.id.btnSubmitSong);

        btnCancel.setOnClickListener(v -> dismiss());
        btnSubmit.setOnClickListener(v -> onSubmit(btnSubmit));
    }

    private void onSubmit(MaterialButton btnSubmit) {
        String title = etSongTitle.getText() != null ? etSongTitle.getText().toString().trim() : "";
        String lyrics = etSongLyrics.getText() != null ? etSongLyrics.getText().toString().trim() : "";
        String author = etSongAuthor.getText() != null ? etSongAuthor.getText().toString().trim() : "";
        String composer = etSongComposer.getText() != null ? etSongComposer.getText().toString().trim() : "";

        if (title.isEmpty()) {
            tilSongTitle.setError(getString(R.string.title_required));
            return;
        }
        if (lyrics.isEmpty()) {
            tilSongLyrics.setError(getString(R.string.lyrics_required_error));
            return;
        }
        tilSongTitle.setError(null);
        tilSongLyrics.setError(null);

        btnSubmit.setEnabled(false);
        SongFirestoreManager.getInstance().submitCommunitySong(
                title, lyrics,
                author.isEmpty() ? null : author,
                composer.isEmpty() ? null : composer,
                new SongFirestoreManager.SongCallback() {
                    @Override public void onSuccess(com.lhavanguane.tisimu.models.SongFirestore song) {
                        Toast.makeText(requireContext(), R.string.song_submitted, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    @Override public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(true);
                    }
                });
    }
}
