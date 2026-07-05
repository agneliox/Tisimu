package com.lhavanguane.tisimu.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CommunityMember {
    private String userId;
    private String userName;
    private String userEmail;
    private String role;  // "member" or "manager"
    @ServerTimestamp
    private Date joinedAt;
    private String profileImageUrl;

    public CommunityMember() {}

    public CommunityMember(String userId, String userName, String userEmail, String role) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.role = role;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Date joinedAt) { this.joinedAt = joinedAt; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public boolean isManager() {
        return "manager".equals(role);
    }
}