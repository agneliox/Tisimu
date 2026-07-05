package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.SongFirestore;

import java.util.ArrayList;
import java.util.List;

public class CommunitySongAdapter extends RecyclerView.Adapter<CommunitySongAdapter.ViewHolder> {

    private List<SongFirestore> songs = new ArrayList<>();
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(SongFirestore song);
    }

    public void setOnSongClickListener(OnSongClickListener listener) {
        this.listener = listener;
    }

    public void setSongs(List<SongFirestore> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSongTitle;
        private final TextView tvAuthorName;
        private final TextView tvLikesCount;
        private final TextView tvCommentsCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);
        }

        void bind(SongFirestore song) {
            tvSongTitle.setText(song.getTitle() != null ? song.getTitle() : "");

            String author = song.getAuthorName();
            tvAuthorName.setText(author != null && !author.isEmpty()
                    ? itemView.getContext().getString(R.string.by_author, author) : "");

            tvLikesCount.setText(song.getLikesCount() + " ♥");
            tvCommentsCount.setText(song.getCommentCount() + " 💬");

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onSongClick(song);
            });
        }
    }
}
