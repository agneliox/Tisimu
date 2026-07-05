package com.lhavanguane.tisimu.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Community {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private String createdByUserName;
    @ServerTimestamp
    private Date createdAt;
    private boolean isPrivate;
    private String joinCode;
    private String coverImageUrl;
    private int memberCount;
    private String userRole;  // For current user: "member" or "manager"

    public Community() {
        // Default constructor required for Firestore
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedByUserName() { return createdByUserName; }
    public void setCreatedByUserName(String createdByUserName) { this.createdByUserName = createdByUserName; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }

    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    @Exclude
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    @Exclude
    public boolean isUserManager() {
        return "manager".equals(userRole);
    }
}