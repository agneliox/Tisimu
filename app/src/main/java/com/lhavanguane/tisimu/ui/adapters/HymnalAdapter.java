package com.lhavanguane.tisimu.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.HymnalManifest;

import java.util.ArrayList;
import java.util.List;

public class HymnalAdapter extends RecyclerView.Adapter<HymnalAdapter.ViewHolder> {
    private static final String TAG = "HymnalAdapter";
    private List<HymnalManifest.HymnalInfo> hymnals = new ArrayList<>();
    private OnHymnalActionListener listener;

    public interface OnHymnalActionListener {
        void onDownloadClick(HymnalManifest.HymnalInfo hymnal);
        void onDeleteClick(HymnalManifest.HymnalInfo hymnal);
        void onSelectClick(HymnalManifest.HymnalInfo hymnal, boolean isSelected);
    }

    public void setOnHymnalActionListener(OnHymnalActionListener listener) {
        this.listener = listener;
    }

    public void setHymnals(List<HymnalManifest.HymnalInfo> hymnals) {
        this.hymnals = hymnals;
        Log.d(TAG, "Setting hymnals: " + (hymnals != null ? hymnals.size() : 0) + " items");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hymnal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HymnalManifest.HymnalInfo hymnal = hymnals.get(position);
        Log.d(TAG, "Binding hymnal at position " + position + ": " + hymnal.getName());
        holder.bind(hymnal);
    }

    @Override
    public int getItemCount() {
        int count = hymnals.size();
        Log.d(TAG, "Item count: " + count);
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView ivCover;
        private TextView tvName, tvDescription, tvDetails;
        private MaterialButton btnAction;
        private MaterialCheckBox cbSelect;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            btnAction = itemView.findViewById(R.id.btnAction);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }

        void bind(HymnalManifest.HymnalInfo hymnal) {
            tvName.setText(hymnal.getName());
            tvDescription.setText(hymnal.getDescription());

            String detailsText = hymnal.getTotalSongs() + " songs";
            if (hymnal.getFileSize() > 0) {
                detailsText += " • " + String.format("%.1f", hymnal.getFileSize() / 1024.0 / 1024.0) + " MB";
            }
            tvDetails.setText(detailsText);
            // Load cover if available
            if (hymnal.getCoverUrl() != null && !hymnal.getCoverUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(hymnal.getCoverUrl())
                        .placeholder(R.drawable.ic_hymn_book)
                        .error(R.drawable.ic_hymn_book)
                        .into(ivCover);
            } else {
                ivCover.setImageResource(R.drawable.ic_hymn_book);
            }

            // Update UI based on download status
            if (hymnal.isDownloaded()) {
                btnAction.setText("DELETE");
                btnAction.setBackgroundTintList(
                        itemView.getContext().getColorStateList(com.google.android.material.R.color.material_divider_color));
                cbSelect.setVisibility(View.VISIBLE);
            } else {
                btnAction.setText("DOWNLOAD");
                btnAction.setBackgroundTintList(
                        itemView.getContext().getColorStateList(R.color.white));
                cbSelect.setVisibility(View.GONE);
            }

            btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    if (hymnal.isDownloaded()) {
                        listener.onDeleteClick(hymnal);
                    } else {
                        listener.onDownloadClick(hymnal);
                    }
                }
            });

            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onSelectClick(hymnal, isChecked);
                }
            });
        }
    }
}