package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.data.database.entities.Song;
import com.lhavanguane.tisimu.models.SectionWithSongs;

import java.util.ArrayList;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SECTION_HEADER = 0;
    private static final int TYPE_SONG = 1;

    private List<SectionWithSongs> sections = new ArrayList<>();
    private OnSongClickListener songClickListener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public void setOnSongClickListener(OnSongClickListener listener) {
        this.songClickListener = listener;
    }

    public void setSections(List<SectionWithSongs> sections) {
        this.sections = sections;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int cumulativePosition = 0;
        for (SectionWithSongs section : sections) {
            if (position == cumulativePosition) {
                return TYPE_SECTION_HEADER;
            }
            cumulativePosition++;

            if (section.isExpanded()) {
                if (position < cumulativePosition + section.getSongs().size()) {
                    return TYPE_SONG;
                }
                cumulativePosition += section.getSongs().size();
            }
        }
        return TYPE_SONG;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_section_header, parent, false);
            return new SectionHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new SongViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int cumulativePosition = 0;
        for (SectionWithSongs section : sections) {
            if (holder instanceof SectionHeaderViewHolder && position == cumulativePosition) {
                ((SectionHeaderViewHolder) holder).bind(section, position);
                return;
            }
            cumulativePosition++;

            if (section.isExpanded()) {
                int songIndex = position - cumulativePosition;
                if (songIndex >= 0 && songIndex < section.getSongs().size()) {
                    if (holder instanceof SongViewHolder) {
                        ((SongViewHolder) holder).bind(section.getSongs().get(songIndex));
                        return;
                    }
                }
                cumulativePosition += section.getSongs().size();
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (SectionWithSongs section : sections) {
            count++; // Section header
            if (section.isExpanded()) {
                count += section.getSongs().size();
            }
        }
        return count;
    }

    class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llSectionHeader;
        private TextView tvSectionIcon;
        private TextView tvSectionName;
        private TextView tvSongRange;
        private TextView tvSongCount;

        SectionHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            llSectionHeader = itemView.findViewById(R.id.llSectionHeader);
            tvSectionIcon = itemView.findViewById(R.id.tvSectionIcon);
            tvSectionName = itemView.findViewById(R.id.tvSectionName);
            tvSongRange = itemView.findViewById(R.id.tvSongRange);
            tvSongCount = itemView.findViewById(R.id.tvSongCount);
        }

        void bind(SectionWithSongs section, int position) {
            tvSectionIcon.setText(section.isExpanded() ? "▼" : "►");
            tvSectionName.setText(section.getSection().getName());
            tvSongRange.setText(section.getSection().getStartNumber() + " - " + section.getSection().getEndNumber());
            tvSongCount.setText(section.getSongs().size() + " songs");

            llSectionHeader.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSectionClick(position);
                }
            });
        }
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private CardView llSongItem;
        private TextView tvSongNumber;
        private TextView tvSongTitle;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            llSongItem = itemView.findViewById(R.id.cardViewSongItem);
            tvSongNumber = itemView.findViewById(R.id.tvSongNumber);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
        }

        void bind(Song song) {
            tvSongNumber.setText(String.format("%03d", song.getNumber()));
            tvSongTitle.setText(song.getTitle());

            llSongItem.setOnClickListener(v -> {
                if (songClickListener != null) {
                    songClickListener.onSongClick(song);
                }
            });
        }
    }

    private OnSectionClickListener listener;

    public interface OnSectionClickListener {
        void onSectionClick(int position);
    }

    public void setOnSectionClickListener(OnSectionClickListener listener) {
        this.listener = listener;
    }

}
