package com.lhavanguane.tisimu.data.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lhavanguane.tisimu.data.database.entities.Comment;

import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    void insert(Comment comment);

    @Query("SELECT * FROM comments WHERE songId = :songId ORDER BY createdAt DESC")
    LiveData<List<Comment>> getCommentsBySongId(int songId);

    @Query("UPDATE comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    void likeComment(int commentId);
}