package com.lhavanguane.tisimu.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Announcement {
    private String id;
    private String communityId;
    private String title;
    private String content;
    private String createdBy;
    private String createdByUserName;
    @ServerTimestamp
    private Date createdAt;
    private boolean isImportant;

    public Announcement() {}

    public Announcement(String communityId, String title, String content, String createdBy, String createdByUserName) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdByUserName = createdByUserName;
        this.isImportant = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCommunityId() { return communityId; }
    public void setCommunityId(String communityId) { this.communityId = communityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedByUserName() { return createdByUserName; }
    public void setCreatedByUserName(String createdByUserName) { this.createdByUserName = createdByUserName; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean important) { isImportant = important; }
}