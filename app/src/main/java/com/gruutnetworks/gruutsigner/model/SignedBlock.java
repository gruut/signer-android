package com.gruutnetworks.gruutsigner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import javax.annotation.Nullable;

@Entity(primaryKeys = {"chain_id", "block_hgt"})
public class SignedBlock {
    @NonNull
    @ColumnInfo(name = "chain_id")
    private String chainId;
    @NonNull
    @ColumnInfo(name = "block_hgt")
    private String blockHeight;

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }
}
