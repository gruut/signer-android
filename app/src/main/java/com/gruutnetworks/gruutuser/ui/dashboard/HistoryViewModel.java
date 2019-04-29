package com.gruutnetworks.gruutuser.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import com.gruutnetworks.gruutuser.model.SignedBlock;
import com.gruutnetworks.gruutuser.model.SignedBlockRepo;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel implements LifecycleObserver {

    private SignedBlockRepo blockRepo;

    private LiveData<List<SignedBlock>> allBlocks;
    private MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();
    private ObservableBoolean emptyVisible = new ObservableBoolean();

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

    public ObservableBoolean getEmptyVisible() {
        return emptyVisible;
    }

    public void setEmptyVisible(boolean isEmpty) {
        this.emptyVisible.set(isEmpty);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
