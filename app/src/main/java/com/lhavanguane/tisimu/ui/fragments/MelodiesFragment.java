package com.lhavanguane.tisimu.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.data.database.entities.MelodyProposal;
import com.lhavanguane.tisimu.ui.adapters.MelodyAdapter;
import com.lhavanguane.tisimu.viewmodels.SongDetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MelodiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MelodiesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvMelodies;
    private FloatingActionButton fabAddMelody;
    private SongDetailViewModel viewModel;
    private MelodyAdapter melodyAdapter;
    private ExoPlayer exoPlayer;

    public MelodiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MelodiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MelodiesFragment newInstance(String param1, String param2) {
        MelodiesFragment fragment = new MelodiesFragment();
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

        View view = inflater.inflate(R.layout.fragment_melodies, container, false);

        rvMelodies = view.findViewById(R.id.rvMelodies);
        fabAddMelody = view.findViewById(R.id.fabAddMelody);

        viewModel = new ViewModelProvider(requireActivity()).get(SongDetailViewModel.class);

        setupExoPlayer();
        setupRecyclerView();
        observeData();
        setupListeners();

        return view;
    }

    private void setupExoPlayer() {
        exoPlayer = new ExoPlayer.Builder(requireContext()).build();
    }

    private void setupRecyclerView() {
        melodyAdapter = new MelodyAdapter();
        melodyAdapter.setExoPlayer(exoPlayer);
        rvMelodies.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMelodies.setAdapter(melodyAdapter);

        melodyAdapter.setOnMelodyActionListener(new MelodyAdapter.OnMelodyActionListener() {


            @Override
            public void onLikeClick(MelodyProposal melody) {
                viewModel.likeMelody(melody);
            }

            @Override
            public void onPlayAudio(MelodyProposal melody, int position) {
                exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(melody.getUrl())));
                exoPlayer.prepare();
                exoPlayer.play();
            }

            @Override
            public void onVideoClick(com.lhavanguane.tisimu.data.database.entities.MelodyProposal melody) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(melody.getUrl()));
                startActivity(intent);
            }
        });
    }

    private void observeData() {
        viewModel.getMelodyProposals().observe(getViewLifecycleOwner(), melodies -> {
            if (melodies != null) {
                melodyAdapter.setMelodies(melodies);
            }
        });
    }

    private void setupListeners() {
        fabAddMelody.setOnClickListener(v -> showAddMelodyDialog());
    }

    private void showAddMelodyDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_melody, null);

        com.google.android.material.textfield.TextInputEditText etTitle = dialogView.findViewById(R.id.etTitle);
        com.google.android.material.textfield.TextInputEditText etVideoUrl = dialogView.findViewById(R.id.etVideoUrl);
        com.google.android.material.textfield.TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        com.google.android.material.chip.ChipGroup chipGroupType = dialogView.findViewById(R.id.chipGroupType);
        com.google.android.material.chip.Chip chipAudio = dialogView.findViewById(R.id.chipAudio);
        com.google.android.material.chip.Chip chipVideo = dialogView.findViewById(R.id.chipVideo);
        View llAudioInput = dialogView.findViewById(R.id.llAudioInput);
        View llVideoInput = dialogView.findViewById(R.id.llVideoInput);

        chipGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAudio) {
                llAudioInput.setVisibility(View.VISIBLE);
                llVideoInput.setVisibility(View.GONE);
            } else if (checkedId == R.id.chipVideo) {
                llAudioInput.setVisibility(View.GONE);
                llVideoInput.setVisibility(View.VISIBLE);
            }
        });

        builder.setView(dialogView);
        builder.setCancelable(true);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnCancelMelody).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSubmitMelody).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedTypeId = chipGroupType.getCheckedChipId();
            if (selectedTypeId == R.id.chipAudio) {
                // For demo, use a sample audio URL
                String audioUrl = "https://www.sample.com/audio.mp3";
                String userName = FirebaseAuth.getInstance().getCurrentUser() != null ?
                        FirebaseAuth.getInstance().getCurrentUser().getEmail() : "Anonymous";
                viewModel.addMelodyProposal(title, "audio", audioUrl, description, userName);
                Toast.makeText(requireContext(), "Audio melody proposed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (selectedTypeId == R.id.chipVideo) {
                String videoUrl = etVideoUrl.getText().toString().trim();
                if (videoUrl.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter video URL", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userName = FirebaseAuth.getInstance().getCurrentUser() != null ?
                        FirebaseAuth.getInstance().getCurrentUser().getEmail() : "Anonymous";
                viewModel.addMelodyProposal(title, "video", videoUrl, description, userName);
                Toast.makeText(requireContext(), "Video melody proposed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Please select a type (Audio or Video)", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            exoPlayer.pause();
            if (melodyAdapter != null) {
                melodyAdapter.stopCurrentPlayback();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }
}