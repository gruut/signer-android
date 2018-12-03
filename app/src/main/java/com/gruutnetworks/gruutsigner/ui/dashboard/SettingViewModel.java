package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.*;
import android.support.annotation.NonNull;
import com.gruutnetworks.gruutsigner.util.PreferenceUtil;

public class SettingViewModel extends AndroidViewModel implements LifecycleObserver {

    private PreferenceUtil preferenceUtil;

    public MutableLiveData<String> ipAddress = new MutableLiveData<>();
    public MutableLiveData<String> portNumber = new MutableLiveData<>();

    public SettingViewModel(@NonNull Application application) {
        super(application);
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void fetchPreference() {
        ipAddress.setValue(preferenceUtil.getString(PreferenceUtil.Key.IP_STR));
        portNumber.setValue(preferenceUtil.getString(PreferenceUtil.Key.PORT_STR));
    }

    void pullPreference() {
        preferenceUtil.put(PreferenceUtil.Key.IP_STR, ipAddress.getValue());
        preferenceUtil.put(PreferenceUtil.Key.PORT_STR, portNumber.getValue());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        preferenceUtil = null;
    }
}
