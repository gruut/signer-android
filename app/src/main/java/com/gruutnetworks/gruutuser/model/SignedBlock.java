package com.gruutnetworks.gruutuser.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(primaryKeys = {"chain_id", "block_hgt"})
public class SignedBlock {
    @NonNull
    @ColumnInfo(name = "chain_id")
    private String chainId;
    @NonNull
    @ColumnInfo(name = "block_hgt")
    private String blockHeight;
    @NonNull
    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @NonNull
    public String getChainId() {
        return chainId;
    }

    public void setChainId(@NonNull String chainId) {
        this.chainId = chainId;
    }

    @NonNull
    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(@NonNull String blockHeight) {
        this.blockHeight = blockHeight;
    }

    @NonNull
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeFormatted() {
        String formatString = DateFormat.getBestDateTimePattern(
                Locale.getDefault(), "yyyyMMdd HH:mm:ss"
        );
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatString, Locale.getDefault());

        return dateFormat.format(new Date(timestamp));
    }
}
