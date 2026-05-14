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
import com.lhavanguane.tisimu.models.HymnalData;

import java.util.ArrayList;
import java.util.List;

public class VerseAdapter extends RecyclerView.Adapter<VerseAdapter.ViewHolder> {

    private List<HymnalData.LyricsSection> sections = new ArrayList<>();
    private OnVerseActionListener listener;
    private int selectedPosition = -1;

    public interface OnVerseActionListener {
        void onVerseLongClick(HymnalData.LyricsSection section, int position);
        void onVerseClick(HymnalData.LyricsSection section, int position);
    }

    public void setOnVerseActionListener(OnVerseActionListener listener) {
        this.listener = listener;
    }

    public void setSections(List<HymnalData.LyricsSection> sections) {
        this.sections = sections;
        notifyDataSetChanged();
    }

    public void copySectionToClipboard(Context context, int position) {
        if (position >= 0 && position < sections.size()) {
            HymnalData.LyricsSection section = sections.get(position);
            String label = section.getLabel() != null ? section.getLabel() + ":\n" : "";
            String text = label + section.getFormattedText();

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("verse", text);
            clipboard.setPrimaryClip(clip);

            String type = "verse".equals(section.getType()) ? "Verse" : "Chorus";
            Toast.makeText(context, type + " " + section.getLabel() + " copied", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyAllSectionsToClipboard(Context context) {
        StringBuilder allText = new StringBuilder();
        for (int i = 0; i < sections.size(); i++) {
            HymnalData.LyricsSection section = sections.get(i);
            String label = section.getLabel() != null ? section.getLabel() : "";
            if ("chorus".equals(section.getType())) {
                label = "【" + label + "】";
            } else if (i > 0) {
                label = label + ".";
            }
            allText.append(label).append("\n");
            allText.append(section.getFormattedText()).append("\n\n");
        }

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("all_verses", allText.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "All lyrics copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public String getAllSectionsText() {
        StringBuilder allText = new StringBuilder();
        for (HymnalData.LyricsSection section : sections) {
            String label = section.getLabel() != null ? section.getLabel() : "";
            if ("chorus".equals(section.getType())) {
                label = "【" + label + "】";
            }
            allText.append(label).append("\n");
            allText.append(section.getFormattedText()).append("\n\n");
        }
        return allText.toString().trim();
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
        HymnalData.LyricsSection section = sections.get(position);
        holder.bind(section, position);

        // Highlight selected section
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(com.google.android.material.R.color.material_divider_color);
            holder.itemView.setAlpha(0.95f);
        } else {
            holder.itemView.setBackgroundResource(0);
            holder.itemView.setAlpha(1f);
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
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

        void bind(HymnalData.LyricsSection section, int position) {
            // Set label based on section type
            String label = "";
            if ("verse".equals(section.getType())) {
//                label = "Verse " + section.getLabel();
            } else if ("chorus".equals(section.getType())) {
                label = "Chorus: " + section.getLabel();
            } else {
                label = section.getLabel() != null ? section.getLabel() : "";
            }

            tvVerseLabel.setText(label);
            tvVerseText.setText(section.getFormattedText());

            // Single click - select section
            llVerseContainer.setOnClickListener(v -> {
                int oldPosition = selectedPosition;
                selectedPosition = position;

                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition);
                }
                notifyItemChanged(position);

                if (listener != null) {
                    listener.onVerseClick(section, position);
                }
            });

            // Long press - copy section
            llVerseContainer.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onVerseLongClick(section, position);
                }
                copySectionToClipboard(v.getContext(), position);
                return true;
            });
        }
    }
}