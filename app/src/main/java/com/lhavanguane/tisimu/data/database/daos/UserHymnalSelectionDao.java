package com.lhavanguane.tisimu.data.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lhavanguane.tisimu.data.database.entities.UserHymnalSelection;

import java.util.List;

@Dao
public interface UserHymnalSelectionDao {
    @Insert
    void insert(UserHymnalSelection selection);

    @Query("SELECT * FROM user_hymnal_selections WHERE userId = :userId")
    LiveData<List<UserHymnalSelection>> getUserSelections(String userId);

    @Query("SELECT hymnalId FROM user_hymnal_selections WHERE userId = :userId AND isSelected = 1")
    LiveData<List<Integer>> getSelectedHymnalIds(String userId);

    @Query("DELETE FROM user_hymnal_selections WHERE userId = :userId")
    void clearUserSelections(String userId);
}
