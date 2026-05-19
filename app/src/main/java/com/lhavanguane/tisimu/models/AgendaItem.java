package com.lhavanguane.tisimu.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class AgendaItem {
    private String id;
    private String communityId;
    private String title;
    private String content;
    private String createdBy;
    private String createdByUserName;
    @ServerTimestamp
    private Date createdAt;
    private String date;

    public AgendaItem() {}

    public AgendaItem(String communityId, String title, String content, String createdBy, String createdByUserName) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdByUserName = createdByUserName;
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

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}