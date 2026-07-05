package com.lhavanguane.tisimu.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SongFirestore {
    private String id;
    private String title;
    private String hymnalId;
    private int songNumber;
    private String authorUid;
    private String authorName;
    private String lyrics;
    private boolean isCommunitySong;
    private int likesCount;
    private int commentCount;
    @ServerTimestamp
    private Date createdAt;

    @Exclude
    private boolean likedByCurrentUser;

    public SongFirestore() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getHymnalId() { return hymnalId; }
    public void setHymnalId(String hymnalId) { this.hymnalId = hymnalId; }

    public int getSongNumber() { return songNumber; }
    public void setSongNumber(int songNumber) { this.songNumber = songNumber; }

    public String getAuthorUid() { return authorUid; }
    public void setAuthorUid(String authorUid) { this.authorUid = authorUid; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getLyrics() { return lyrics; }
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

    public boolean isCommunitySong() { return isCommunitySong; }
    public void setCommunitySong(boolean communitySong) { isCommunitySong = communitySong; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Exclude
    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    @Exclude
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}
