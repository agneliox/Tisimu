package com.lhavanguane.tisimu.data.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lhavanguane.tisimu.data.database.entities.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Insert
    void insert(Song song);

    @Insert
    void insertAll(List<Song> songs);

    @Query("SELECT * FROM songs WHERE hymnalId = :hymnalId ORDER BY number ASC")
    LiveData<List<Song>> getSongsByHymnal(int hymnalId);

    @Query("SELECT * FROM songs WHERE hymnalId IN (:hymnalIds) ORDER BY number ASC")
    LiveData<List<Song>> getSongsForSelectedHymnals(List<Integer> hymnalIds);

    @Query("SELECT * FROM songs WHERE sectionId = :sectionId ORDER BY number ASC")
    LiveData<List<Song>> getSongsBySection(int sectionId);

    @Query("SELECT * FROM songs WHERE id = :songId")
    LiveData<Song> getSongById(int songId);

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR number LIKE '%' || :query || '%'")
    LiveData<List<Song>> searchSongs(String query);

    @Query("SELECT * FROM songs WHERE (title LIKE '%' || :query || '%' OR number LIKE '%' || :query || '%') AND hymnalId IN (:hymnalIds)")
    LiveData<List<Song>> searchSongsInSelectedHymnals(String query, List<Integer> hymnalIds);
}
