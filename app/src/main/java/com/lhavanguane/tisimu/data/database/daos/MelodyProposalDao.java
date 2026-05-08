package com.lhavanguane.tisimu.data.database.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lhavanguane.tisimu.data.database.entities.MelodyProposal;

import java.util.List;

@Dao
public interface MelodyProposalDao {
    @Insert
    void insert(MelodyProposal proposal);

    @Query("SELECT * FROM melody_proposals WHERE songId = :songId ORDER BY likesCount DESC")
    LiveData<List<MelodyProposal>> getMelodyProposalsBySongId(int songId);

    @Query("UPDATE melody_proposals SET likesCount = likesCount + 1 WHERE id = :proposalId")
    void likeProposal(int proposalId);
}
