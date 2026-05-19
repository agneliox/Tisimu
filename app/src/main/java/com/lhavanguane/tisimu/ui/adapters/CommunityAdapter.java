package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Community;

import java.util.ArrayList;
import java.util.List;

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
        private final TextView tvMemberCount;
        private final TextView tvCreatedBy;
        private final ImageView iconVisibility;
        private final MaterialButton btnJoin;
        private final MaterialButton btnView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.tvCommunityName);
            tvDescription = itemView.findViewById(R.id.tvCommunityDescription);
            tvMemberCount = itemView.findViewById(R.id.tvMemberCount);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            iconVisibility = itemView.findViewById(R.id.iconCommunityVisibility);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            btnView = itemView.findViewById(R.id.btnView);
        }

        void bind(Community community) {
            tvName.setText(community.getName());
            tvDescription.setText(community.getDescription());
            tvMemberCount.setText(community.getMemberCount() + " members");
            tvCreatedBy.setText("Created by " + community.getCreatedByUserName());

            if (community.isPrivate()) {
                iconVisibility.setImageResource(R.drawable.ic_lock);
            } else {

                iconVisibility.setImageResource(R.drawable.ic_people);
            }

            // Hide join button for joined communities (this fragment only shows joined communities)
            btnJoin.setVisibility(View.GONE);

            btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(community);
                }
            });
        }
    }
}