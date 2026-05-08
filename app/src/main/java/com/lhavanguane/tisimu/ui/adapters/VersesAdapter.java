package com.lhavanguane.tisimu.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lhavanguane.tisimu.R;
import com.lhavanguane.tisimu.models.Verse;

import java.util.ArrayList;
import java.util.List;

public class VersesAdapter extends RecyclerView.Adapter<VersesAdapter.VerseViewHolder>{

    private List<Verse> verses = new ArrayList<>();
    private OnVerseClickListener verseClickListener;

    public interface OnVerseClickListener {
        void onVerseClick(Verse verse, int position);
    }

    public void setOnVerseClickListener(OnVerseClickListener listener) {
        this.verseClickListener = listener;
    }

    public void setVerses(List<Verse> verses) {
        this.verses = verses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VerseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_verse, parent, false);
        return new VerseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerseViewHolder holder, int position) {
        Verse verse = verses.get(position);
        holder.bind(verse, position);
    }

    @Override
    public int getItemCount() {
        return verses.size();
    }

    class VerseViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llVerseContainer;
        private TextView tvVerseLabel;
        private TextView tvVerseText;

        VerseViewHolder(@NonNull View itemView) {
            super(itemView);
            llVerseContainer = itemView.findViewById(R.id.llVerseContainer);
            tvVerseLabel = itemView.findViewById(R.id.tvVerseLabel);
            tvVerseText = itemView.findViewById(R.id.tvVerseText);
        }

        void bind(Verse verse, int position) {
            tvVerseLabel.setText("Verse " + (position + 1));
            tvVerseText.setText(verse.getText());

            llVerseContainer.setOnClickListener(v -> {
                if (verseClickListener != null) {
                    verseClickListener.onVerseClick(verse, position);
                }
                // Show toast with the verse text for demonstration
                Toast.makeText(v.getContext(), "Verse " + (position + 1) + " selected", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
