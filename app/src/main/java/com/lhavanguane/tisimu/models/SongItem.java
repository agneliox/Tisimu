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
}