package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import com.gruutnetworks.gruutsigner.model.SignedBlock;
import com.gruutnetworks.gruutsigner.model.SignedBlockDao;
import com.gruutnetworks.gruutsigner.util.AppDatabase;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel implements LifecycleObserver {

    private static SignedBlockDao blockDao;
    private LiveData<List<SignedBlock>> allBlocks;

    public HistoryViewModel(@NonNull Application application) {
        super(application);

        blockDao = AppDatabase.getDatabase(application).blockDao();
        allBlocks = blockDao.findAll();
        List<SignedBlock> tmp = allBlocks.getValue();
    }

    public LiveData<List<SignedBlock>> getAllBlocks() {
        return allBlocks;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
