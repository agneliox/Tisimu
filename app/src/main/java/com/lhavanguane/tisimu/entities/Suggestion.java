package com.lhavanguane.tisimu.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "suggestions")
public class Suggestion {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int songId;
    private String userId;
    private String userName;
    private int verseNumber;
    private String currentText;
    private String suggestedText;
    private String justification;
    private String status; // "pending", "approved", "rejected"
    private long createdAt;

    public Suggestion(int songId, String userId, String userName, int verseNumber,
                      String currentText, String suggestedText, String justification) {
        this.songId = songId;
        this.userId = userId;
        this.userName = userName;
        this.verseNumber = verseNumber;
        this.currentText = currentText;
        this.suggestedText = suggestedText;
        this.justification = justification;
        this.status = "pending";
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getVerseNumber() { return verseNumber; }
    public void setVerseNumber(int verseNumber) { this.verseNumber = verseNumber; }

    public String getCurrentText() { return currentText; }
    public void setCurrentText(String currentText) { this.currentText = currentText; }

    public String getSuggestedText() { return suggestedText; }
    public void setSuggestedText(String suggestedText) { this.suggestedText = suggestedText; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
