package com.lhavanguane.tisimu.data.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lhavanguane.tisimu.data.database.entities.Comment;
import com.lhavanguane.tisimu.data.database.entities.MelodyProposal;
import com.lhavanguane.tisimu.data.database.entities.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Insert
    void insert(Song song);

    @Query("SELECT * FROM comments WHERE songId = :songId ORDER BY createdAt DESC")
    LiveData<List<Comment>> getCommentsBySongId(int songId);


    @Query("UPDATE comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    void likeComment(int commentId);

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

    @Query("SELECT * FROM songs WHERE id = :songId")
    Song getSongByIdSync(int songId);

    @Query("SELECT * FROM comments WHERE songId = :songId ORDER BY createdAt DESC")
    List<Comment> getCommentsBySongIdSync(int songId);

    @Query("SELECT * FROM melody_proposals WHERE songId = :songId ORDER BY likesCount DESC")
    List<MelodyProposal> getMelodyProposalsBySongIdSync(int songId);
}
