package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.AgendaItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder> {

    private List<AgendaItem> items = new ArrayList<>();
    private OnAgendaActionListener listener;

    public interface OnAgendaActionListener {
        void onItemClick(AgendaItem item);
        void onDeleteClick(AgendaItem item);
    }

    public void setOnAgendaActionListener(OnAgendaActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<AgendaItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agenda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AgendaItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvTitle;
        private final TextView tvContentPreview;
        private final TextView tvCreatedBy;
        private final TextView tvDate;
        private final MaterialButton btnDelete;
        private final View clickOverlay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContentPreview = itemView.findViewById(R.id.tvContentPreview);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            clickOverlay = itemView.findViewById(R.id.clickOverlay);
        }

        void bind(AgendaItem item) {
            tvTitle.setText(item.getTitle());

            // Show preview of content (first 100 characters)
            String content = item.getContent();
            if (content.length() > 100) {
                tvContentPreview.setText(content.substring(0, 100) + "...");
            } else {
                tvContentPreview.setText(content);
            }

            tvCreatedBy.setText("Added by " + item.getCreatedByUserName());

            if (item.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                tvDate.setText(sdf.format(item.getCreatedAt()));
            }

            // Click on card to open detail
            clickOverlay.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            // Delete button
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}