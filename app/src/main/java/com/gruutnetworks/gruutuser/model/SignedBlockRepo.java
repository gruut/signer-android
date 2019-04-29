package com.gruutnetworks.gruutuser.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import com.gruutnetworks.gruutuser.util.AppDatabase;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SignedBlockRepo {
    private SignedBlockDao blockDao;
    private LiveData<List<SignedBlock>> allBlocks;

    public SignedBlockRepo(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        blockDao = db.blockDao();
        allBlocks = blockDao.findAll();
    }

    public LiveData<List<SignedBlock>> getAllBlocks() {
        return allBlocks;
    }

    public SignedBlock retrieveBlock(String chainId, String height) {
        try {
            return new RetrieveAsyncTask(blockDao, chainId, height).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public void insert(SignedBlock block) {
        new InsertAsyncTask(blockDao).execute(block);
    }
    public void deleteAll() { new DeleteAsyncTask(blockDao).execute(); }

    private static class RetrieveAsyncTask extends AsyncTask<Void, Void, SignedBlock> {

        private WeakReference<SignedBlockDao> asyncTaskDao;
        private String chainId;
        private String height;

        public RetrieveAsyncTask(SignedBlockDao asyncTaskDao, String chainId, String height) {
            this.asyncTaskDao = new WeakReference<>(asyncTaskDao);
            this.chainId = chainId;
            this.height = height;
        }

        @Override
        protected SignedBlock doInBackground(Void... voids) {
            return asyncTaskDao.get().findByPrimaryKey(chainId, height);
        }
    }

    private static class InsertAsyncTask extends AsyncTask<SignedBlock, Void, Void> {

        private WeakReference<SignedBlockDao> asyncTaskDao;

        public InsertAsyncTask(SignedBlockDao asyncTaskDao) {
            this.asyncTaskDao = new WeakReference<>(asyncTaskDao);
        }

        @Override
        protected Void doInBackground(SignedBlock... blocks) {
            asyncTaskDao.get().insertAll(blocks);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<SignedBlockDao> asyncTaskDao;

        public DeleteAsyncTask(SignedBlockDao asyncTaskDao) {
            this.asyncTaskDao = new WeakReference<>(asyncTaskDao);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncTaskDao.get().deleteAll();
            return null;
        }
    }
}
