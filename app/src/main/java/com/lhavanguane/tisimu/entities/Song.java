package com.lhavanguane.tisimu.entities;

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
    private String lyrics;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private String author;
    private String composer;
    private boolean isOfficial;
    private Integer userId;  // null if official
    private String createdAt;

    public Song(int hymnalId, int sectionId, int number, String title, String lyrics,
                String author, String composer, boolean isOfficial, Integer userId, String createdAt) {
        this.hymnalId = hymnalId;
        this.sectionId = sectionId;
        this.number = number;
        this.title = title;
        this.lyrics = lyrics;
        this.author = author;
        this.composer = composer;
        this.isOfficial = isOfficial;
        this.userId = userId;
        this.createdAt = createdAt;
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

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyricsText) {
        this.lyrics = lyricsText;
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
