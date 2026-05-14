package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.SongItem;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private List<SongItem> songs = new ArrayList<>();
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(SongItem songItem);
    }

    public void setOnSongClickListener(OnSongClickListener listener) {
        this.listener = listener;
    }

    public void setSongs(List<SongItem> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongItem item = songs.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumber, tvTitle, tvSubtitle;
        private ImageView ivChevron;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvSongNumber);
            tvTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSongSubtitle);
            ivChevron = itemView.findViewById(R.id.ivChevron);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSongClick(songs.get(position));
                }
            });
        }

        void bind(SongItem item) {
            tvNumber.setText(String.format("%03d", item.getSong().getNumber()));
            tvTitle.setText(item.getSong().getTitle());

            if (item.getHymnalName() != null && !item.getHymnalName().isEmpty()) {
                tvSubtitle.setText(item.getHymnalName());
                tvSubtitle.setVisibility(View.VISIBLE);
            } else {
                tvSubtitle.setVisibility(View.GONE);
            }
        }
    }
}