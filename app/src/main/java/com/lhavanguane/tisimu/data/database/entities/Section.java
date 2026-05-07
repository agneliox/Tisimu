package com.lhavanguane.tisimu.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sections")
public class Section {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int hymnalId;
    private int sequenceNumber;
    private String name;
    private int startNumber;
    private int endNumber;

    // Constructor
    public Section(int hymnalId, int sequenceNumber, String name, int startNumber, int endNumber) {
        this.hymnalId = hymnalId;
        this.sequenceNumber = sequenceNumber;
        this.name = name;
        this.startNumber = startNumber;
        this.endNumber = endNumber;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHymnalId() { return hymnalId; }
    public void setHymnalId(int hymnalId) { this.hymnalId = hymnalId; }

    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getStartNumber() { return startNumber; }
    public void setStartNumber(int startNumber) { this.startNumber = startNumber; }

    public int getEndNumber() { return endNumber; }
    public void setEndNumber(int endNumber) { this.endNumber = endNumber; }
}