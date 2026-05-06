package com.lhavanguane.tisimu.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "hymnals")
public class Hymnal {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private String language;
    private int totalSongs;
    private boolean isActive;
    private boolean isCollaborative;

    public Hymnal(int id, String name, String description, String language, int totalSongs, boolean isActive, boolean isCollaborative) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.language = language;
        this.totalSongs = totalSongs;
        this.isActive = isActive;
        this.isCollaborative = isCollaborative;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isCollaborative() {
        return isCollaborative;
    }

    public void setCollaborative(boolean collaborative) {
        isCollaborative = collaborative;
    }
// Getters and setters
}
