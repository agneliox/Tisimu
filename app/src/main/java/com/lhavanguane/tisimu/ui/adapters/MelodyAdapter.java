package com.lhavanguane.tisimu.ui.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.data.database.entities.MelodyProposal;

import java.util.ArrayList;
import java.util.List;

public class MelodyAdapter extends RecyclerView.Adapter<MelodyAdapter.MelodyViewHolder> {

    private List<MelodyProposal> melodies = new ArrayList<>();
    private OnMelodyActionListener actionListener;
    private ExoPlayer exoPlayer;
    private int currentlyPlayingPosition = -1;

    public interface OnMelodyActionListener {
        void onLikeClick(MelodyProposal melody);
        void onPlayAudio(MelodyProposal melody, int position);
        void onVideoClick(MelodyProposal melody);
    }

    public void setOnMelodyActionListener(OnMelodyActionListener listener) {
        this.actionListener = listener;
    }

    public void setMelodies(List<MelodyProposal> melodies) {
        this.melodies = melodies;
        notifyDataSetChanged();
    }

    public void setExoPlayer(ExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    public void stopCurrentPlayback() {
        if (exoPlayer != null && currentlyPlayingPosition != -1) {
            exoPlayer.stop();
            notifyItemChanged(currentlyPlayingPosition);
            currentlyPlayingPosition = -1;
        }
    }

    @NonNull
    @Override
    public MelodyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_melody, parent, false);
        return new MelodyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MelodyViewHolder holder, int position) {
        MelodyProposal melody = melodies.get(position);
        holder.bind(melody, position);
    }

    @Override
    public int getItemCount() {
        return melodies.size();
    }

    class MelodyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProposerName;
        private TextView tvLikes;
        private TextView tvMelodyTitle;
        private TextView tvDescription;
        private LinearLayout llAudioPlayer;
        private LinearLayout llVideoPlayer;
        private com.google.android.material.button.MaterialButton btnPlayAudio;
        private SeekBar seekBar;
        private com.google.android.material.button.MaterialButton btnLike;
        private com.google.android.material.card.MaterialCardView cvVideoThumbnail;
        private ImageView ivThumbnail;

        MelodyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProposerName = itemView.findViewById(R.id.tvProposerName);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvMelodyTitle = itemView.findViewById(R.id.tvMelodyTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            llAudioPlayer = itemView.findViewById(R.id.llAudioPlayer);
            cvVideoThumbnail = itemView.findViewById(R.id.cvVideoThumbnail);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
            seekBar = itemView.findViewById(R.id.seekBar);
            btnLike = itemView.findViewById(R.id.btnLike);
        }

        void bind(MelodyProposal melody, int position) {
            tvProposerName.setText(melody.getUserName());
            tvLikes.setText(melody.getLikesCount() + " likes");
            tvMelodyTitle.setText(melody.getTitle());

            if (melody.getDescription() != null && !melody.getDescription().isEmpty()) {
                tvDescription.setText(melody.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Format time
            long now = System.currentTimeMillis();
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    melody.getCreatedAt(), now, DateUtils.MINUTE_IN_MILLIS);

            if (melody.getType().equals("audio")) {
                llAudioPlayer.setVisibility(View.VISIBLE);
                cvVideoThumbnail.setVisibility(View.GONE);
                setupAudioPlayer(melody, position);
            } else {
                llAudioPlayer.setVisibility(View.GONE);
                cvVideoThumbnail.setVisibility(View.VISIBLE);
                setupVideoThumbnail(melody);

                cvVideoThumbnail.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onVideoClick(melody);
                    }
                });
            }

            btnLike.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onLikeClick(melody);
                }
            });
        }

        private void setupAudioPlayer(MelodyProposal melody, int position) {
            btnPlayAudio.setOnClickListener(v -> {
                if (actionListener != null) {
                    if (currentlyPlayingPosition == position && exoPlayer != null && exoPlayer.isPlaying()) {
                        exoPlayer.pause();
                        btnPlayAudio.setText("▶");
                    } else {
                        stopCurrentPlayback();
                        actionListener.onPlayAudio(melody, position);
                        currentlyPlayingPosition = position;
                        btnPlayAudio.setText("⏸");
                    }
                }
            });
        }

        private void setupVideoThumbnail(MelodyProposal melody) {
            // Extract YouTube video ID from URL
            String videoId = extractYouTubeId(melody.getUrl());
            if (videoId != null) {
                String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                Glide.with(itemView.getContext())
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.ic_hymn_book)
                        .into(ivThumbnail);
            }
        }

        private String extractYouTubeId(String url) {
            String pattern = "(?<=v=)[\\w-]+|(?<=be/)[\\w-]+";
            java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = compiledPattern.matcher(url);
            if (matcher.find()) {
                return matcher.group();
            }
            return null;
        }
    }
}