package com.gruutnetworks.gruutsigner.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import com.gruutnetworks.gruutsigner.model.JoiningResponse;
import com.gruutnetworks.gruutsigner.model.JoiningSourceData;
import com.gruutnetworks.gruutsigner.restApi.GaApi;
import com.gruutnetworks.gruutsigner.util.KeystoreUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.security.PublicKey;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    public ObservableField<String> pid = new ObservableField<>();
    private Call<JoiningResponse> joiningCall;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void onClickButton() {
        join();
    }

    private void join() {
        Log.d(TAG, "start joining");
        KeystoreUtil keystoreUtil = KeystoreUtil.getInstance();
        PublicKey pubKey = null;
        try {
            keystoreUtil.createKeys(getApplication().getApplicationContext());
            pubKey = keystoreUtil.getPublicKey();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        JoiningSourceData sourceData = new JoiningSourceData(pid.get(), pubKey.toString());

        joiningCall = GaApi.getInstance().requestJoining(sourceData);
        joiningCall.enqueue(new Callback<JoiningResponse>() {
            @Override
            public void onResponse(Call<JoiningResponse> call, Response<JoiningResponse> response) {
                switch (response.code()) {
                    case 200:
                        JoiningResponse responseBody = response.body();

                        if (responseBody != null) {
                            Log.d(TAG, "nId: " + responseBody.getNid());
                            Log.d(TAG, "pem: " + responseBody.getPem());
                        }
                        break;
                    default:
                        break;
                }
                joiningCall = null;
            }

            @Override
            public void onFailure(Call<JoiningResponse> call, Throwable t) {
                Log.e(TAG, "Failed... " + t.getMessage());
                joiningCall = null;
            }
        });
    }

    @Override
    protected void onCleared() {
        if (joiningCall != null) {
            joiningCall.cancel();
            joiningCall = null;
        }
    }
}
