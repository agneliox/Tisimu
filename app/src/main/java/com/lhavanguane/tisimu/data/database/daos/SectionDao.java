package com.lhavanguane.tisimu.data.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lhavanguane.tisimu.data.database.entities.Section;

import java.util.List;

@Dao
public interface SectionDao {
    @Insert
    void insert(Section section);

    @Insert
    void insertAll(List<Section> sections);

    @Query("SELECT * FROM sections WHERE hymnalId = :hymnalId ORDER BY sequenceNumber ASC")
    LiveData<List<Section>> getSectionsByHymnal(int hymnalId);

    @Query("SELECT * FROM sections WHERE hymnalId IN (:hymnalIds) ORDER BY hymnalId, sequenceNumber ASC")
    LiveData<List<Section>> getSectionsForSelectedHymnals(List<Integer> hymnalIds);

    @Query("SELECT * FROM sections WHERE id = :sectionId")
    LiveData<Section> getSectionById(int sectionId);
}
