package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.gruutnetworks.gruutsigner.util.PreferenceUtil;

import static com.gruutnetworks.gruutsigner.ui.dashboard.SettingFragment.MERGER_1;
import static com.gruutnetworks.gruutsigner.ui.dashboard.SettingFragment.MERGER_2;

public class SettingViewModel extends AndroidViewModel implements LifecycleObserver {

    private PreferenceUtil preferenceUtil;

    public MutableLiveData<String> merger = new MutableLiveData<>();
    public MutableLiveData<String> ipAddress = new MutableLiveData<>();
    public MutableLiveData<String> portNumber = new MutableLiveData<>();

    public SettingViewModel(@NonNull Application application) {
        super(application);
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
    }

    void fetchPreference() {
        if (merger.getValue().equals(MERGER_1)) {
            ipAddress.setValue(preferenceUtil.getString(PreferenceUtil.Key.IP1_STR));
            portNumber.setValue(preferenceUtil.getString(PreferenceUtil.Key.PORT1_STR));
        } else if (merger.getValue().equals(MERGER_2)) {
            ipAddress.setValue(preferenceUtil.getString(PreferenceUtil.Key.IP2_STR));
            portNumber.setValue(preferenceUtil.getString(PreferenceUtil.Key.PORT2_STR));
        }
    }

    void pullPreference() {
        if (merger.getValue().equals(MERGER_1)) {
            preferenceUtil.put(PreferenceUtil.Key.IP1_STR, ipAddress.getValue());
            preferenceUtil.put(PreferenceUtil.Key.PORT1_STR, portNumber.getValue());
        } else if (merger.getValue().equals(MERGER_2)) {
            preferenceUtil.put(PreferenceUtil.Key.IP2_STR, ipAddress.getValue());
            preferenceUtil.put(PreferenceUtil.Key.PORT2_STR, portNumber.getValue());
        }
    }

    public void setMerger(String merger) {
        this.merger.setValue(merger);
    }

    public MutableLiveData<String> getMerger() {
        return merger;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        preferenceUtil = null;
    }
}
