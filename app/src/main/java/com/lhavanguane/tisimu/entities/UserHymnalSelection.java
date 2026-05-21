package com.lhavanguane.tisimu.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_hymnal_selections")
public class UserHymnalSelection {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;
    private int hymnalId;
    private boolean isSelected;
    private long selectedAt;


    public UserHymnalSelection(String userId, int hymnalId, boolean isSelected) {
        this.userId = userId;
        this.hymnalId = hymnalId;
        this.isSelected = isSelected;
        this.selectedAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getHymnalId() { return hymnalId; }
    public void setHymnalId(int hymnalId) { this.hymnalId = hymnalId; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public long getSelectedAt() { return selectedAt; }
    public void setSelectedAt(long selectedAt) { this.selectedAt = selectedAt; }

}
