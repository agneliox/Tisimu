package com.lhavanguane.tisimu.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SongComment {
    private String id;
    private String userId;
    private String userName;
    private String text;
    private String parentCommentId;
    private int likesCount;
    @ServerTimestamp
    private Date createdAt;

    @Exclude
    private boolean likedByCurrentUser;
    @Exclude
    private List<SongComment> replies = new ArrayList<>();

    public SongComment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(String parentCommentId) { this.parentCommentId = parentCommentId; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Exclude
    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    @Exclude
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }

    @Exclude
    public List<SongComment> getReplies() { return replies; }
    @Exclude
    public void setReplies(List<SongComment> replies) { this.replies = replies; }
}
