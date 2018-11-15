package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.SystemClock;

public class DashboardViewModel extends ViewModel {

    MutableLiveData<String> testData = new MutableLiveData<>();
    int counter;

    public DashboardViewModel() {
        counter = 0;

        new Thread() {
            @Override
            public void run() {
                while (counter < 100) {
                    SystemClock.sleep(1000 * 1);
                    ++counter;
                    testData.postValue("count... " + counter + "\n");
                }
            }
        }.start();
    }

    public MutableLiveData<String> getTestData() {
        return testData;
    }
}
