package com.gruutnetworks.gruutsigner.ui.join;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.model.JoiningResponse;
import com.gruutnetworks.gruutsigner.model.JoiningSourceData;
import com.gruutnetworks.gruutsigner.restApi.GaApi;
import com.gruutnetworks.gruutsigner.util.KeystoreUtil;
import com.gruutnetworks.gruutsigner.util.SnackbarMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinViewModel extends AndroidViewModel implements LifecycleObserver {

    private static final String TAG = "JoinViewModel";

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final SnackbarMessage snackbarMessage = new SnackbarMessage();
    private Call<JoiningResponse> joiningCall;

    public ObservableField<String> phoneNum = new ObservableField<>();
    private KeystoreUtil keystoreUtil;

    public JoinViewModel(@NonNull Application application) {
        super(application);
        this.keystoreUtil = KeystoreUtil.getInstance();
    }

    public void onClickButton() {
        loading.setValue(true);

        String pubKey = getPublicKey();
        String pid = phoneNum.get();

        if (pubKey == null || pubKey.isEmpty()) {
            snackbarMessage.setValue(R.string.join_error_pubkey);
            loading.setValue(false);
            return;
        }

        if (pid == null || pid.isEmpty()) {
            snackbarMessage.setValue(R.string.join_error_pid);
            loading.setValue(false);
            return;
        }

        JoiningSourceData sourceData = new JoiningSourceData(pid, pubKey);
        joiningCall = GaApi.getInstance().requestJoining(sourceData);
        joiningCall.enqueue(new Callback<JoiningResponse>() {
            @Override
            public void onResponse(Call<JoiningResponse> call, Response<JoiningResponse> response) {
                if (response.body() != null) {
                    switch (response.body().getCode()) {
                        case 200:
                            if (storeCertificate(response.body().getPem())) {
                                snackbarMessage.setValue(R.string.join_success);
                            } else {
                                snackbarMessage.setValue(R.string.join_error_cert);
                            }
                            break;
                        case 500:
                            snackbarMessage.setValue(R.string.join_error_internal);
                            break;
                        default:
                            snackbarMessage.setValue(R.string.join_error_unknown);
                            break;
                    }
                }
                loading.setValue(false);
                joiningCall = null;
            }

            @Override
            public void onFailure(Call<JoiningResponse> call, Throwable t) {
                Log.e(TAG, "API Failed... " + t.getMessage());
                snackbarMessage.setValue(R.string.join_error_network);

                loading.setValue(false);
                joiningCall = null;
            }
        });
    }

    /**
     * Get public key if key pair exists
     * and generate key pair if none exists.
     *
     * @return generated public key with tag
     */
    private String getPublicKey() {
        try {
            if (!keystoreUtil.isKeyPairExist()) {
                keystoreUtil.createKeys(getApplication().getApplicationContext());
            }
            return keystoreUtil.getPublicKey();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    private boolean storeCertificate(String cert) {
        try {
            keystoreUtil.setAlias(KeystoreUtil.SecurityConstants.Alias.GRUUT_AUTH);
            keystoreUtil.updateEntry(cert);

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    SnackbarMessage getSnackbarMessage() {
        return snackbarMessage;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    @Override
    protected void onCleared() {
        if (joiningCall != null) {
            joiningCall.cancel();
            joiningCall = null;
        }
    }
}
