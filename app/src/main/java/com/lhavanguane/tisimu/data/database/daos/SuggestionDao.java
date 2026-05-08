package com.lhavanguane.tisimu.data.database.daos;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lhavanguane.tisimu.data.database.entities.Suggestion;

@Dao
public interface SuggestionDao {
    @Insert
    void insert(Suggestion suggestion);
}