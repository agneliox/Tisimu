package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Community;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private List<Community> communities = new ArrayList<>();
    private OnCommunityActionListener listener;

    public interface OnCommunityActionListener {
        void onJoinClick(Community community);
        void onViewClick(Community community);
    }

    public void setOnCommunityActionListener(OnCommunityActionListener listener) {
        this.listener = listener;
    }

    public void setCommunities(List<Community> communities) {
        this.communities = communities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Community community = communities.get(position);
        holder.bind(community);
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvName;
        private TextView tvDescription;
        private TextView tvMemberCount;
        private TextView tvCreatedBy;
        private Chip chipVisibility;
        private MaterialButton btnView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.tvCommunityName);
            tvDescription = itemView.findViewById(R.id.tvCommunityDescription);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            chipVisibility = itemView.findViewById(R.id.chipVisibility);
            btnView = itemView.findViewById(R.id.btnView);
        }

        void bind(Community community) {
            tvName.setText(community.getName());
            tvDescription.setText(community.getDescription());
            tvMemberCount.setText(community.getMemberCount() + " members");
            tvCreatedBy.setText("Created by " + community.getCreatedByUserName());

            if (community.isPrivate()) {
                chipVisibility.setText("Private");
                chipVisibility.setChipIconResource(R.drawable.ic_lock);
            } else {
                chipVisibility.setText("Public");
                chipVisibility.setChipIconResource(R.drawable.ic_public);
            }

            btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(community);
                }
            });
        }
    }
}