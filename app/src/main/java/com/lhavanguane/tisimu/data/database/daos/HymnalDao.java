package com.lhavanguane.tisimu.data.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lhavanguane.tisimu.data.database.entities.Hymnal;

import java.util.List;

@Dao
public interface HymnalDao {
    @Insert
    void insert(Hymnal hymnal);

    @Insert
    void insertAll(List<Hymnal> hymnals);

    @Update
    void update(Hymnal hymnal);

    @Query("SELECT * FROM hymnals ORDER BY name ASC")
    LiveData<List<Hymnal>> getAllHymnals();

    @Query("SELECT * FROM hymnals WHERE isSelected = 1 ORDER BY name ASC")
    LiveData<List<Hymnal>> getSelectedHymnals();

    @Query("SELECT * FROM hymnals WHERE id = :hymnalId")
    LiveData<Hymnal> getHymnalById(int hymnalId);

    @Query("UPDATE hymnals SET isSelected = :isSelected WHERE id = :hymnalId")
    void updateSelection(int hymnalId, boolean isSelected);

    @Query("SELECT COUNT(*) FROM hymnals WHERE isSelected = 1")
    LiveData<Integer> getSelectedCount();

    // Add this synchronous method for checking data
    @Query("SELECT COUNT(*) FROM hymnals")
    int getCountSync();

    @Query("DELETE FROM hymnals")
    void deleteAll();
}
