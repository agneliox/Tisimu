package com.lhavanguane.tisimu.models;

import com.lhavanguane.tisimu.data.database.entities.Section;
import com.lhavanguane.tisimu.data.database.entities.Song;

import java.util.List;

public class SectionWithSongs {
    private Section section;
    private List<Song> songs;
    private boolean isExpanded;

    public SectionWithSongs(Section section, List<Song> songs) {
        this.section = section;
        this.songs = songs;
        this.isExpanded = true;
    }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
