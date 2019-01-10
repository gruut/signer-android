package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.gruutnetworks.gruutsigner.gruut.Merger;
import com.gruutnetworks.gruutsigner.util.PreferenceUtil;

public class PresettingViewModel extends AndroidViewModel implements LifecycleObserver {

    private PreferenceUtil preferenceUtil;

    private MutableLiveData<DashboardViewModel.MergerNum> mergerNum = new MutableLiveData<>();
    private MutableLiveData<String> ipAddress = new MutableLiveData<>();
    private MutableLiveData<String> portNumber = new MutableLiveData<>();

    public PresettingViewModel(@NonNull Application application) {
        super(application);
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
    }

    void fetchPreference() {
        if (mergerNum.getValue() != null) {
            switch (mergerNum.getValue()) {
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
        if (mergerNum.getValue() != null) {
            switch (mergerNum.getValue()) {
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

    void setMergerNum(DashboardViewModel.MergerNum mergerNum) {
        this.mergerNum.setValue(mergerNum);
    }

    MutableLiveData<DashboardViewModel.MergerNum> getMergerNum() {
        return mergerNum;
    }

    public void setMerger(Merger merger) {
        this.ipAddress.setValue(merger.getUri());
        this.portNumber.setValue(Integer.toString(merger.getPort()));
    }

    public Merger getMerger() {
        if (mergerNum.getValue() != null) {
            switch (mergerNum.getValue()) {
                case MERGER_1:
                    return new Merger(preferenceUtil.getString(PreferenceUtil.Key.IP1_STR),
                            Integer.parseInt(preferenceUtil.getString(PreferenceUtil.Key.PORT1_STR)));
                case MERGER_2:
                    return new Merger(preferenceUtil.getString(PreferenceUtil.Key.IP2_STR),
                            Integer.parseInt(preferenceUtil.getString(PreferenceUtil.Key.PORT2_STR)));
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        preferenceUtil = null;
    }
}
