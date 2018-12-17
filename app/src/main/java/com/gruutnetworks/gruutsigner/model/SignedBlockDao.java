package com.gruutnetworks.gruutsigner.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface SignedBlockDao {

    @Query("SELECT * FROM signedblock WHERE chain_id = (:chainId) AND block_hgt = (:blockHeight)")
    SignedBlock findByPrimaryKey(String chainId, String blockHeight);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertAll(SignedBlock... blocks);

}
