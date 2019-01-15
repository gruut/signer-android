package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.gruutnetworks.gruutsigner.model.SignedBlock;
import com.gruutnetworks.gruutsigner.model.SignedBlockRepo;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel implements LifecycleObserver {

    private SignedBlockRepo blockRepo;

    private LiveData<List<SignedBlock>> allBlocks;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        blockRepo = new SignedBlockRepo(application);

        fetchBlocks();
    }

    private void fetchBlocks() {
        allBlocks = blockRepo.getAllBlocks();
    }

    public LiveData<List<SignedBlock>> getAllBlocks() {
        return allBlocks;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
