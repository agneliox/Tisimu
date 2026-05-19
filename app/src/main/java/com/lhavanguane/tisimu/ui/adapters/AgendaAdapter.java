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
        private CardView cardView;
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvCreatedBy;
        private TextView tvDate;
        private MaterialButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(AgendaItem item) {
            tvTitle.setText(item.getTitle());
            tvContent.setText(item.getContent());
            tvCreatedBy.setText("Added by " + item.getCreatedByUserName());

            if (item.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                tvDate.setText(sdf.format(item.getCreatedAt()));
            }

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}