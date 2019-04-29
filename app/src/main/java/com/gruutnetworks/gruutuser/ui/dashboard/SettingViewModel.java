package com.gruutnetworks.gruutuser.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.gruutnetworks.gruutuser.util.PreferenceUtil;

public class SettingViewModel extends AndroidViewModel implements LifecycleObserver {

    private PreferenceUtil preferenceUtil;

    public MutableLiveData<DashboardViewModel.MergerNum> merger = new MutableLiveData<>();
    public MutableLiveData<String> ipAddress = new MutableLiveData<>();
    public MutableLiveData<String> portNumber = new MutableLiveData<>();

    public SettingViewModel(@NonNull Application application) {
        super(application);
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
    }

    void fetchPreference() {
        if (merger.getValue() != null) {
            switch (merger.getValue()) {
                case MERGER_1:
                    ipAddress.setValue(preferenceUtil.getString(PreferenceUtil.Key.IP1_STR));
                    portNumber.setValue(preferenceUtil.getString(PreferenceUtil.Key.PORT1_STR));
                    break;
                case MERGER_2:
                    ipAddress.setValue(preferenceUtil.getString(PreferenceUtil.Key.IP2_STR));
                    portNumber.setValue(preferenceUtil.getString(PreferenceUtil.Key.PORT2_STR));
                    break;
                default:
                    break;
            }
        }
    }

    void pullPreference() {
        if (merger.getValue() != null) {
            switch (merger.getValue()) {
                case MERGER_1:
                    preferenceUtil.put(PreferenceUtil.Key.IP1_STR, ipAddress.getValue());
                    preferenceUtil.put(PreferenceUtil.Key.PORT1_STR, portNumber.getValue());
                    break;
                case MERGER_2:
                    preferenceUtil.put(PreferenceUtil.Key.IP2_STR, ipAddress.getValue());
                    preferenceUtil.put(PreferenceUtil.Key.PORT2_STR, portNumber.getValue());
                    break;
                default:
                    break;
            }
        }
    }

    public void setMerger(DashboardViewModel.MergerNum merger) {
        this.merger.setValue(merger);
    }

    public MutableLiveData<DashboardViewModel.MergerNum> getMerger() {
        return merger;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        preferenceUtil = null;
    }
}
