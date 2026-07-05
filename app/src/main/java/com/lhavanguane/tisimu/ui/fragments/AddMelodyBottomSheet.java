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

public class AddMelodyBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SONG_ID = "songId";

    private String songId;
    private TextInputLayout tilMelodyTitle;
    private TextInputLayout tilMelodyUrl;
    private TextInputEditText etTitle;
    private TextInputEditText etUrl;
    private TextInputEditText etDescription;

    public static AddMelodyBottomSheet newInstance(String songId) {
        AddMelodyBottomSheet sheet = new AddMelodyBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SONG_ID, songId);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songId = getArguments().getString(ARG_SONG_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_melody, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilMelodyTitle = view.findViewById(R.id.tilMelodyTitle);
        tilMelodyUrl = view.findViewById(R.id.tilMelodyUrl);
        etTitle = view.findViewById(R.id.etTitle);
        etUrl = view.findViewById(R.id.etUrl);
        etDescription = view.findViewById(R.id.etDescription);
        MaterialButton btnSubmit = view.findViewById(R.id.btnSubmitMelody);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancelMelody);

        btnCancel.setOnClickListener(v -> dismiss());
        btnSubmit.setOnClickListener(v -> onSubmit());
    }

    private void onSubmit() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String url = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
        String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

        if (title.isEmpty()) {
            tilMelodyTitle.setError(getString(R.string.title_required));
            return;
        }
        if (url.isEmpty()) {
            tilMelodyUrl.setError(getString(R.string.url_required));
            return;
        }
        tilMelodyTitle.setError(null);
        tilMelodyUrl.setError(null);

        String type = detectType(url);
        SongFirestoreManager.getInstance().addMelody(
                songId, title, url, type,
                desc.isEmpty() ? null : desc,
                new SongFirestoreManager.VoidCallback() {
                    @Override public void onSuccess() {
                        Toast.makeText(requireContext(), R.string.melody_added, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    @Override public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String detectType(String url) {
        String lower = url.toLowerCase();
        if (lower.contains("youtube.com") || lower.contains("youtu.be")
                || lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".webm")) {
            return "video";
        }
        return "audio";
    }
}
