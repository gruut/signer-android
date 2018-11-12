package com.gruutnetworks.gruutsigner.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
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

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

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
        String pubKey = null;
        try {
            keystoreUtil.createKeys(getApplication().getApplicationContext());
            pubKey = keystoreUtil.getPublicKey();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        JoiningSourceData sourceData = new JoiningSourceData(pid.get(), pubKey);

        Log.d(TAG, "###### REQUEST START ######");
        Log.d(TAG, "publicKey: " + pubKey);
        Log.d(TAG, "###### REQUEST END ######");

        joiningCall = GaApi.getInstance().requestJoining(sourceData);
        joiningCall.enqueue(new Callback<JoiningResponse>() {
            @Override
            public void onResponse(Call<JoiningResponse> call, Response<JoiningResponse> response) {
                switch (response.code()) {
                    case 200:
                        JoiningResponse responseBody = response.body();

                        if (responseBody != null) {

                            Log.d(TAG, "###### RESPONSE START ######");
                            Log.d(TAG, "nId: " + responseBody.getNid());
                            Log.d(TAG, "pem: " + responseBody.getPem());
                            Log.d(TAG, "###### RESPONSE END ######");

                            try {

                                Log.d(TAG, "###### VERIFY START ######");
                                keystoreUtil.setAlias(KeystoreUtil.SecurityConstants.Alias.GRUUT_AUTH);
                                keystoreUtil.updateEntry(responseBody.getPem());
                                Log.d(TAG, "publicKey: " + keystoreUtil.getPublicKey());
                                Log.d(TAG, "###### VERIFY END ######");

                                Log.d(TAG, "###### CHECK START ######");
                                keystoreUtil.setAlias(KeystoreUtil.SecurityConstants.Alias.SELF_CERT);
                                String test = "Test String is here! 동해물과백두산이 마르고닳도록 하느님이보우하사 우리나라만세";
                                String signed = keystoreUtil.signData(test);
                                Log.d(TAG, "is it verified? : " + keystoreUtil.verifyData(test, signed));
                                Log.d(TAG, "###### CHECK START ######");

                            } catch (KeyStoreException e) {
                                e.printStackTrace();
                            } catch (CertificateException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (UnrecoverableEntryException e) {
                                e.printStackTrace();
                            } catch (NoSuchProviderException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (SignatureException e) {
                                e.printStackTrace();
                            }
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
