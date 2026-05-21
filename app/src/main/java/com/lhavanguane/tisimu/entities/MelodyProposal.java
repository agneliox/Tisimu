package com.lhavanguane.tisimu.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "melody_proposals")
public class MelodyProposal {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int songId;
    private String userId;
    private String userName;
    private String type; // "audio" or "video"
    private String url; // For video links or audio file path
    private String title;
    private String description;
    private long createdAt;
    private int likesCount;

    public MelodyProposal(int songId, String userId, String userName, String type, String url, String title) {
        this.songId = songId;
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.url = url;
        this.title = title;
        this.createdAt = System.currentTimeMillis();
        this.likesCount = 0;
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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
}