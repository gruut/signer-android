package com.gruutnetworks.gruutsigner.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SignedBlockDao {

    @Query("SELECT * FROM signedblock")
    LiveData<List<SignedBlock>> findAll();

    @Query("SELECT * FROM signedblock WHERE chain_id = (:chainId) AND block_hgt = (:blockHeight)")
    SignedBlock findByPrimaryKey(String chainId, String blockHeight);

    // TODO: 테스트가 끝난 후에는 중복 된 값에 대해서 Fail 처리 하도록 바꿔야 함
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(SignedBlock... blocks);

}
