package com.gruutnetworks.gruutsigner.ui.signup;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.model.MessageJoin;
import com.gruutnetworks.gruutsigner.model.MessageResponse1;
import com.gruutnetworks.gruutsigner.model.SignUpResponse;
import com.gruutnetworks.gruutsigner.model.SignUpSourceData;
import com.gruutnetworks.gruutsigner.restApi.GaApi;
import com.gruutnetworks.gruutsigner.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class SignUpViewModel extends AndroidViewModel implements LifecycleObserver {

    private static final String TAG = "SignUpViewModel";

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final SnackbarMessage snackbarMessage = new SnackbarMessage();
    private final SingleLiveEvent navigateToDashboard = new SingleLiveEvent();
    private Call<SignUpResponse> signUpCall;

    public ObservableField<String> phoneNum = new ObservableField<>();
    private KeystoreUtil keystoreUtil;
    private PreferenceUtil preferenceUtil;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        this.keystoreUtil = KeystoreUtil.getInstance();
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
    }

    public void onSignUpClickButton() {
        loading.setValue(true);

        String pubKey = getPublicKey();
        String pid = phoneNum.get();

        if (pubKey == null || pubKey.isEmpty()) {
            snackbarMessage.setValue(R.string.sign_up_error_pubkey);
            loading.setValue(false);
            return;
        }

        if (pid == null || pid.isEmpty()) {
            snackbarMessage.setValue(R.string.sign_up_error_pid);
            loading.setValue(false);
            return;
        }

        SignUpSourceData sourceData = new SignUpSourceData(pid, pubKey);
        signUpCall = GaApi.getInstance().signUp(sourceData);
        signUpCall.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.body() != null) {
                    switch (response.body().getCode()) {
                        case 200:
                            if (storeCertificate(response.body().getPem())) {
                                preferenceUtil.put(PreferenceUtil.Key.SID_INT, response.body().getNid());
                                navigateToDashboard.call();
                            } else {
                                snackbarMessage.setValue(R.string.sign_up_error_cert);
                            }
                            break;
                        case 500:
                            snackbarMessage.setValue(R.string.sign_up_error_internal);
                            break;
                        default:
                            snackbarMessage.setValue(R.string.sign_up_error_unknown);
                            break;
                    }
                }
                loading.setValue(false);
                signUpCall = null;
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.e(TAG, "API Failed... " + t.getMessage());
                snackbarMessage.setValue(R.string.sign_up_error_network);

                loading.setValue(false);
                signUpCall = null;
            }
        });
    }

    public void onJoinClickButton() {
        loading.setValue(true);

        MessageJoin msgJoin = new MessageJoin();
        msgJoin.setSignerNonce(AuthUtil.getNonce());
        msgJoin.setTime(AuthUtil.getTimestamp());
        msgJoin.setSender(preferenceUtil.getString(PreferenceUtil.Key.SID_INT));
        // TODO send join msg

        // TODO get MSG_CHALLENGE

        try {
            KeyPair ecdhKeyPair = keystoreUtil.ecdhKeyGen();
            String pubKey = keystoreUtil.pubkeyToString(ecdhKeyPair.getPublic());
            MessageResponse1 response1 = new MessageResponse1();
            response1.setSig("signature");  // TODO sign with MSG_CHALLENGE data
            response1.setNonceSigner(AuthUtil.getNonce());
            response1.setDh1(pubKey);
            response1.setTime(AuthUtil.getTimestamp());
            response1.setSender(preferenceUtil.getString(PreferenceUtil.Key.SID_INT));
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        // TODO send response 1

        // get MSG_RESPONSE_2
        // validate sig2

        // get HMAC KEY

        // send MSG_SUCCESS
        // get MSG_ACCEPT

        // Add Merger on list


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
            keystoreUtil.updateEntry(cert, KeystoreUtil.SecurityConstants.Alias.GRUUT_AUTH);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    SnackbarMessage getSnackbarMessage() {
        return snackbarMessage;
    }

    SingleLiveEvent getNavigateToDashboard() {
        return navigateToDashboard;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    @Override
    protected void onCleared() {
        if (signUpCall != null) {
            signUpCall.cancel();
            signUpCall = null;
        }
    }
}
