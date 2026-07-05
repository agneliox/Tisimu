package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.CommunityMember;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private List<CommunityMember> members = new ArrayList<>();

    public void setMembers(List<CommunityMember> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommunityMember member = members.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvName;
        private final TextView tvEmail;
        private final Chip chipRole;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_Member);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            chipRole = itemView.findViewById(R.id.chipRole);
        }

        void bind(CommunityMember member) {
            tvName.setText(member.getUserName());
            tvEmail.setText(member.getUserEmail());

            if (member.isManager()) {
                chipRole.setText("Manager");
            } else {
                chipRole.setVisibility(View.GONE);

            }
        }
    }
}