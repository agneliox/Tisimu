package com.lhavanguane.tisimu.models;

public class SongItem {
    private String hymnalId;
    private String hymnalName;
    private HymnalData.Song song;

    public SongItem(String hymnalId, String hymnalName, HymnalData.Song song) {
        this.hymnalId = hymnalId;
        this.hymnalName = hymnalName;
        this.song = song;
    }

    public String getHymnalId() { return hymnalId; }
    public void setHymnalId(String hymnalId) { this.hymnalId = hymnalId; }

    public String getHymnalName() { return hymnalName; }
    public void setHymnalName(String hymnalName) { this.hymnalName = hymnalName; }

    public HymnalData.Song getSong() { return song; }
    public void setSong(HymnalData.Song song) { this.song = song; }

    public String getDisplayTitle() {
        return song.getTitle();
    }

    public String getDisplayInfo() {
        return hymnalName + " • Hymn " + String.format("%03d", song.getNumber());
    }

    // Helper to get formatted lyrics for display
    public String getFormattedLyrics() {
        StringBuilder sb = new StringBuilder();
        for (HymnalData.LyricsSection section : song.getVerses()) {
            // Add section label with styling
            if (section.getLabel() != null) {
                if ("chorus".equals(section.getType())) {
                    sb.append("【").append(section.getLabel()).append("】\n");
                } else {
                    sb.append(section.getLabel()).append(".\n");
                }
            }

            // Add lines
            for (String line : section.getLines()) {
                sb.append(line).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // Helper to get plain lyrics for sharing
    public String getPlainLyrics() {
        StringBuilder sb = new StringBuilder();
        for (HymnalData.LyricsSection section : song.getVerses()) {
            if (section.getLabel() != null) {
                sb.append(section.getLabel());
                if ("chorus".equals(section.getType())) {
                    sb.append(" (Coro)");
                }
                sb.append(":\n");
            }
            for (String line : section.getLines()) {
                sb.append(line).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

}