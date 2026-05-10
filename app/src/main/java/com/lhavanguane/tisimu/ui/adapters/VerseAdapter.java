package com.lhavanguane.tisimu.ui.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

public class VerseAdapter extends RecyclerView.Adapter<VerseAdapter.ViewHolder> {

    private List<Verse> verses = new ArrayList<>();
    private OnVerseActionListener listener;
    private int selectedPosition = -1;

    public interface OnVerseActionListener {
        void onVerseLongClick(Verse verse, int position);
        void onVerseClick(Verse verse, int position);
    }

    public void setOnVerseActionListener(OnVerseActionListener listener) {
        this.listener = listener;
    }

    public void setVerses(List<Verse> verses) {
        this.verses = verses;
        notifyDataSetChanged();
    }

    public void copyVerseToClipboard(Context context, int position) {
        if (position >= 0 && position < verses.size()) {
            Verse verse = verses.get(position);
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("verse", verse.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Verse " + verse.getNumber() + " copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyAllVersesToClipboard(Context context) {
        StringBuilder allVerses = new StringBuilder();
        for (Verse verse : verses) {
            allVerses.append("Verse ").append(verse.getNumber()).append(":\n");
            allVerses.append(verse.getText()).append("\n\n");
        }

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("all_verses", allVerses.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "All verses copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public String getAllVersesText() {
        StringBuilder allVerses = new StringBuilder();
        for (Verse verse : verses) {
            allVerses.append("Verse ").append(verse.getNumber()).append(":\n");
            allVerses.append(verse.getText()).append("\n\n");
        }
        return allVerses.toString();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_verse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Verse verse = verses.get(position);
        holder.bind(verse, position);

        // Highlight selected verse
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.color.primary_light);
            holder.itemView.setAlpha(0.9f);
        } else {
            holder.itemView.setBackgroundResource(0);
            holder.itemView.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return verses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llVerseContainer;
        private TextView tvVerseLabel;
        private TextView tvVerseText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            llVerseContainer = itemView.findViewById(R.id.llVerseContainer);
            tvVerseLabel = itemView.findViewById(R.id.tvVerseLabel);
            tvVerseText = itemView.findViewById(R.id.tvVerseText);
        }

        void bind(Verse verse, int position) {
            tvVerseLabel.setText("Verse " + verse.getNumber());
            tvVerseText.setText(verse.getText());

            // Single click - select verse
            llVerseContainer.setOnClickListener(v -> {
                // Update selected position
                int oldPosition = selectedPosition;
                selectedPosition = position;

                // Notify both positions to update highlight
                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition);
                }
                notifyItemChanged(position);

                if (listener != null) {
                    listener.onVerseClick(verse, position);
                }

                // Show toast with verse reference
                Toast.makeText(v.getContext(), "Verse " + verse.getNumber() + " selected. Long press to copy.", Toast.LENGTH_SHORT).show();
            });

            // Long press - copy verse
            llVerseContainer.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onVerseLongClick(verse, position);
                }
                copyVerseToClipboard(v.getContext(), position);
                return true;
            });
        }
    }
}