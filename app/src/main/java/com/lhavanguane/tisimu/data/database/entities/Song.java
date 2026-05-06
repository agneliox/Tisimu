package com.lhavanguane.tisimu.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hymnalId;
    private int sectionId;
    private int number;
    private String title;
    private String lyricsText;  // could be replaced by verses relationship
    private boolean isOfficial;
    private Integer userId;  // null if official

    public Song(int id, int hymnalId, int sectionId, int number, String title, String lyricsText, boolean isOfficial, Integer userId) {
        this.id = id;
        this.hymnalId = hymnalId;
        this.sectionId = sectionId;
        this.number = number;
        this.title = title;
        this.lyricsText = lyricsText;
        this.isOfficial = isOfficial;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHymnalId() {
        return hymnalId;
    }

    public void setHymnalId(int hymnalId) {
        this.hymnalId = hymnalId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLyricsText() {
        return lyricsText;
    }

    public void setLyricsText(String lyricsText) {
        this.lyricsText = lyricsText;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public void setOfficial(boolean official) {
        isOfficial = official;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
// Constructor, getters, setters...
}
