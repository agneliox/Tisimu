package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.data.database.entities.Hymnal;

import java.util.ArrayList;
import java.util.List;

public class HymnalAdapter extends RecyclerView.Adapter<HymnalAdapter.HymnalViewHolder> {
    private List<Hymnal> hymnals = new ArrayList<>();
    private OnHymnalSelectionListener selectionListener;

    public HymnalAdapter() {

    }

    public void setOnHymnalSelectionListener(OnHymnalSelectionListener listener) {
        this.selectionListener = listener;
    }

    public interface OnHymnalSelectionListener {
        void onHymnalSelected(Hymnal hymnalId, boolean isSelected);
    }

    public void setHymnals(List<Hymnal> hymnals) {
        this.hymnals = hymnals;
        notifyDataSetChanged();
    }
    public HymnalAdapter(OnHymnalSelectionListener listener) {
        selectionListener = listener;
    }
    @NonNull
    @Override
    public HymnalAdapter.HymnalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hymnal, parent, false);
        return new HymnalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HymnalAdapter.HymnalViewHolder holder, int position) {
        Hymnal hymnal = hymnals.get(position);
        holder.bind(hymnal);
    }

    @Override
    public int getItemCount() {
        return hymnals.size();
    }

    public class HymnalViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivHymnalIcon;
        private TextView tvHymnalName;
        private TextView tvHymnalDescription;
        private TextView tvSongCount;
        private MaterialCheckBox cbSelectHymnal;
        public HymnalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHymnalIcon = itemView.findViewById(R.id.ivHymnalIcon);
            tvHymnalName = itemView.findViewById(R.id.tvHymnalName);
            tvHymnalDescription = itemView.findViewById(R.id.tvHymnalDescription);
            tvSongCount = itemView.findViewById(R.id.tvSongCount);
            cbSelectHymnal = itemView.findViewById(R.id.cbSelectHymnal);
        }

        public void bind(Hymnal hymnal) {
            tvHymnalName.setText(hymnal.getName());
            tvHymnalDescription.setText(hymnal.getDescription());
            tvSongCount.setText(hymnal.getTotalSongs() + " songs");
            cbSelectHymnal.setChecked(hymnal.isSelected());

            cbSelectHymnal.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (selectionListener != null) {
                    selectionListener.onHymnalSelected(hymnal, isChecked);
                }
            });

            // Make entire card clickable to toggle selection
            itemView.setOnClickListener(v -> {
                boolean newState = !cbSelectHymnal.isChecked();
                cbSelectHymnal.setChecked(newState);
                if (selectionListener != null) {
                    selectionListener.onHymnalSelected(hymnal, newState);
                }
            });
        }
    }
}
