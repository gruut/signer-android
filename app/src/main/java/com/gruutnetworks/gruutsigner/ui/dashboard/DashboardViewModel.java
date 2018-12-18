package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.*;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.protobuf.ByteString;
import com.gruutnetworks.gruutsigner.*;
import com.gruutnetworks.gruutsigner.Identity;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.exceptions.AsyncException;
import com.gruutnetworks.gruutsigner.exceptions.AuthUtilException;
import com.gruutnetworks.gruutsigner.exceptions.ErrorMsgException;
import com.gruutnetworks.gruutsigner.gruut.GruutConfigs;
import com.gruutnetworks.gruutsigner.model.*;
import com.gruutnetworks.gruutsigner.util.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DashboardViewModel extends AndroidViewModel implements LifecycleObserver {

    private static final String TAG = "DashboardViewModel";

    public enum MergerNum {
        MERGER_1, MERGER_2
    }

    public MutableLiveData<String> logMerger1 = new MutableLiveData<>();
    public MutableLiveData<String> logMerger2 = new MutableLiveData<>();
    public MutableLiveData<String> ipMerger1 = new MutableLiveData<>();
    public MutableLiveData<String> ipMerger2 = new MutableLiveData<>();
    public MutableLiveData<String> portMerger1 = new MutableLiveData<>();
    public MutableLiveData<String> portMerger2 = new MutableLiveData<>();
    public MutableLiveData<Boolean> errorMerger1 = new MutableLiveData<>();
    public MutableLiveData<Boolean> errorMerger2 = new MutableLiveData<>();
    private final SingleLiveEvent refreshMerger1 = new SingleLiveEvent();
    private final SingleLiveEvent refreshMerger2 = new SingleLiveEvent();
    private final SingleLiveEvent openSetting1Dialog = new SingleLiveEvent();
    private final SingleLiveEvent openSetting2Dialog = new SingleLiveEvent();

    private AuthCertUtil authCertUtil;
    private AuthHmacUtil authHmacUtil;
    private PreferenceUtil preferenceUtil;
    private SignedBlockDao blockDao;

    private ManagedChannel channel1;
    private ManagedChannel channel2;

    private String sender;
    private String signerNonce;
    private Map<String, String> mergerNonceMap = new HashMap<>();

    private KeyPair keyPair;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.authCertUtil = AuthCertUtil.getInstance();
        this.authHmacUtil = AuthHmacUtil.getInstance();
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
        this.sender = preferenceUtil.getString(PreferenceUtil.Key.SID_STR);

        blockDao = AppDatabase.getDatabase(application).blockDao();

        if (!NetworkUtil.isConnected(application.getApplicationContext())) {
            SnackbarMessage snackbarMessage = new SnackbarMessage();
            snackbarMessage.postValue(R.string.sign_up_error_network);
        }
    }

    /**
     * 화면이 다 그려졌을 때 join 시작
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        refreshMerger1();
        refreshMerger2();
    }

    public void refreshMerger1() {
        refreshMerger1.call();
        errorMerger1.setValue(false);

        ipMerger1.setValue(preferenceUtil.getString(PreferenceUtil.Key.IP1_STR));
        portMerger1.setValue(preferenceUtil.getString(PreferenceUtil.Key.PORT1_STR));

        if (ipMerger1.getValue() != null && portMerger1.getValue() != null &&
                !ipMerger1.getValue().isEmpty() && !portMerger1.getValue().isEmpty()) {
            channel1 = ManagedChannelBuilder
                    .forAddress(ipMerger1.getValue(), Integer.parseInt(portMerger1.getValue()))
                    .usePlaintext()
                    .build();
            logMerger1.postValue("[Channel Setting]" + ipMerger1.getValue() + ":" + portMerger1.getValue());

            startJoining(logMerger1, errorMerger1);
        } else {
            logMerger1.postValue("Please set merger's ip address first.");
        }
    }

    public void refreshMerger2() {
        refreshMerger2.call();
        errorMerger2.setValue(false);

        ipMerger2.setValue(preferenceUtil.getString(PreferenceUtil.Key.IP2_STR));
        portMerger2.setValue(preferenceUtil.getString(PreferenceUtil.Key.PORT2_STR));

        if (ipMerger2.getValue() != null && portMerger2.getValue() != null &&
                !ipMerger2.getValue().isEmpty() && !portMerger2.getValue().isEmpty()) {
            channel2 = ManagedChannelBuilder
                    .forAddress(ipMerger2.getValue(), Integer.parseInt(portMerger2.getValue()))
                    .usePlaintext()
                    .build();
            logMerger2.postValue("[Channel Setting]" + ipMerger2.getValue() + ":" + portMerger2.getValue());

            startJoining(logMerger2, errorMerger2);
        } else {
            logMerger2.postValue("Please set merger's ip address first.");
        }
    }

    void startJoining(MutableLiveData<String> log, MutableLiveData<Boolean> error) {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                try {
                    UnpackMsgChallenge challenge = requestJoin(channel1, log);
                    UnpackMsgResponse2 response2 = sendPublicKey(channel1, challenge, log);
                    UnpackMsgAccept accept = sendSuccess(channel1, response2, log);
                    if (accept.isVal()) {
                        standBy(channel1, log, error);
                    }
                } catch (ErrorMsgException e) {
                    log.postValue("[ERROR]" + e.getMessage());
                    error.postValue(true);
                } catch (AuthUtilException e) {
                    log.postValue("[CRYPTO_ERROR]" + e.getMessage());
                    error.postValue(true);
                }
            }
        }.start();
    }

    public void openAddressSetting(int mergerNum) {
        if (mergerNum == 1) {
            openSetting1Dialog.call();
        } else if (mergerNum == 2) {
            openSetting2Dialog.call();
        }
    }

    /**
     * Start joining request
     * SEND MSG_JOIN to merger
     *
     * @param channel target merger
     * @return received MSG_CHALLENGE
     * @throws StatusRuntimeException on GRPC error
     */
    private UnpackMsgChallenge requestJoin(ManagedChannel channel, MutableLiveData<String> log) throws StatusRuntimeException {
        log.postValue("START requestJoin...");

        PackMsgJoin packMsgJoin = new PackMsgJoin(
                sender,
                AuthGeneralUtil.getTimestamp(),
                GruutConfigs.ver,
                GruutConfigs.localChainId
        );

        MsgUnpacker receivedMsg = null;
        try {
            log.postValue("[SEND]" + "MSG_JOIN");
            receivedMsg = new GrpcTask(channel).execute(packMsgJoin).get();
        } catch (InterruptedException | ExecutionException | StatusRuntimeException e) {
            throw new AsyncException();
        }

        // Check received message's type
        if (receivedMsg == null) {
            // This error message may be caused by a timeout.
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_NOT_FOUND);
        } else if (receivedMsg.getMessageType() == TypeMsg.MSG_ERROR) {
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_ERR_RECEIVED);
        }

        log.postValue("[RECEIVE]" + "MSG_CHALLENGE");
        return (UnpackMsgChallenge) receivedMsg;
    }

    /**
     * Start to exchange dh key
     * SEND MSG_RESPONSE_1 to merger
     *
     * @param channel          target merger
     * @param messageChallenge received MSG_CHALLENGE
     * @return received MSG_RESPONSE_2
     * @throws StatusRuntimeException on GRPC error
     */
    private UnpackMsgResponse2 sendPublicKey(ManagedChannel channel, UnpackMsgChallenge messageChallenge, MutableLiveData<String> log) throws StatusRuntimeException {
        log.postValue("START sendPublicKey...");

        // generate signer nonce
        signerNonce = AuthGeneralUtil.getNonce();

        // get merger nonce
        mergerNonceMap.put(messageChallenge.getSender(), messageChallenge.getMergerNonce());

        if (!AuthGeneralUtil.isMsgInTime(messageChallenge.getTime())) {
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_EXPIRED);
        }

        if (keyPair == null) {
            // generate ecdh key
            log.postValue("Generate ECDH key pair");
            try {
                keyPair = authHmacUtil.generateEcdhKeys();
            } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new AuthUtilException(AuthUtilException.AuthErr.KEY_GEN_ERROR);
            }
        } else {
            log.postValue("Already have an ECDH key");
        }

        String x = new String(authHmacUtil.pubToXpoint(keyPair.getPublic()));
        String y = new String(authHmacUtil.pubToYpoint(keyPair.getPublic()));
        String time = AuthGeneralUtil.getTimestamp();
        String signature = null;
        try {
            signature = authCertUtil.signMsgResponse1(messageChallenge.getMergerNonce(), signerNonce, x, y, time);
        } catch (Exception e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.SIGNING_ERROR);
        }

        // Get Certificate issued by GA
        String cert = null;
        try {
            cert = authCertUtil.getCert(SecurityConstants.Alias.GRUUT_AUTH);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.GET_CERT_ERROR);
        }

        if (cert.isEmpty()) {
            throw new AuthUtilException(AuthUtilException.AuthErr.NO_CERT_ERROR);
        }

        PackMsgResponse1 msgResponse1 = new PackMsgResponse1(
                sender,
                time,
                cert,
                signerNonce,
                x,  /* HEX */
                y,  /* HEX */
                signature /* BASE64 */
        );

        MsgUnpacker receivedMsg = null;
        try {
            log.postValue("[SEND]" + "MSG_RESPONSE_1");
            receivedMsg = new GrpcTask(channel).execute(msgResponse1).get();
        } catch (InterruptedException | ExecutionException | StatusRuntimeException e) {
            throw new AsyncException();
        }

        // Check received message's type
        if (receivedMsg == null) {
            // This error message may be caused by a timeout.
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_NOT_FOUND);
        } else if (receivedMsg.getMessageType() == TypeMsg.MSG_ERROR) {
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_ERR_RECEIVED);
        }

        log.postValue("[RECEIVED]" + "MSG_RESPONSE_2");
        return (UnpackMsgResponse2) receivedMsg;
    }

    /**
     * Finishing joining request
     * SEND MSG_SUCCESS to merger
     *
     * @param channel          target merger
     * @param messageResponse2 received MSG_RESPONSE_2
     * @return received MSG_ACCEPT
     * @throws StatusRuntimeException on GRPC error
     */
    private UnpackMsgAccept sendSuccess(ManagedChannel channel, UnpackMsgResponse2 messageResponse2, MutableLiveData<String> log) throws StatusRuntimeException {

        try {
            // 서명 검증
            String mergerNonce = mergerNonceMap.get(messageResponse2.getSender());
            if (!authCertUtil.verifyMsgResponse2(messageResponse2.getSig(), messageResponse2.getCert(),
                    mergerNonce, signerNonce, messageResponse2.getDhPubKeyX(), messageResponse2.getDhPubKeyY(), messageResponse2.getTime())) {
                throw new AuthUtilException(AuthUtilException.AuthErr.INVALID_SIGNATURE);
            }
        } catch (Exception e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.VERIFYING_ERROR);
        }

        // X,Y 좌표로부터 Pulbic key get
        PublicKey mergerPubKey = null;
        try {
            mergerPubKey = authHmacUtil.pointToPub(messageResponse2.getDhPubKeyX(), messageResponse2.getDhPubKeyY());
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.KEY_GEN_ERROR);
        }

        // HMAC KEY 계산
        byte[] hmacKey;
        try {
            hmacKey = authHmacUtil.getSharedSecreyKey(keyPair.getPrivate(), mergerPubKey);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.HMAC_KEY_GEN_ERROR);
        }

        // HMAC KEY 저장
        preferenceUtil.put(messageResponse2.getSender(), new String(hmacKey));

        PackMsgSuccess msgSuccess = new PackMsgSuccess(
                sender,
                AuthGeneralUtil.getTimestamp(),
                true
        );
        msgSuccess.setDestinationId(messageResponse2.getSender());

        MsgUnpacker receivedMsg = null;
        try {
            log.postValue("[SEND]" + "MSG_SUCCESS");
            receivedMsg = new GrpcTask(channel).execute(msgSuccess).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AsyncException();
        } catch (StatusRuntimeException e) {
            throw e;
        }

        // Check received message's type
        if (receivedMsg == null) {
            // This error message may be caused by a timeout.
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_NOT_FOUND);
        } else if (receivedMsg.getMessageType() == TypeMsg.MSG_ERROR) {
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_ERR_RECEIVED);
        } else if (!receivedMsg.isMacValid()) {
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_INVALID_HMAC);
        }

        log.postValue("[RECEIVED]" + "MSG_ACCEPT");
        return (UnpackMsgAccept) receivedMsg;
    }

    private void standBy(ManagedChannel channel, MutableLiveData<String> log, MutableLiveData<Boolean> error) {
        GruutNetworkServiceGrpc.GruutNetworkServiceStub stub = GruutNetworkServiceGrpc.newStub(channel);
        StreamObserver<Identity> standBy = stub.openChannel(new StreamObserver<GrpcMsgReqSsig>() {
            @Override
            public void onNext(GrpcMsgReqSsig value) {
                // Signature request from Merger
                log.postValue("I've got MSG_REQ_SSIG!");
                try {
                    sendSignature(channel, value, log);
                } catch (ErrorMsgException e) {
                    log.postValue("[ERROR]" + e.getMessage());
                    error.postValue(true);
                } catch (AuthUtilException e) {
                    log.postValue("[CRYPTO_ERROR]" + e.getMessage());
                    error.postValue(true);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.postValue("This Merger is DEAD... Now Dobby is free!");
                error.postValue(true);
            }

            @Override
            public void onCompleted() {
                log.postValue("GRPC stream onComplete()");
                error.postValue(true);
            }
        });

        standBy.onNext(Identity.newBuilder().setSender(ByteString.copyFrom(sender.getBytes())).build());
        log.postValue("Streaming channel opened...standby for signature request");
        Log.d(TAG, "Streaming channel opened...standby for signature request");
    }

    private void sendSignature(ManagedChannel channel, GrpcMsgReqSsig grpcMsgReqSsig, MutableLiveData<String> log) throws StatusRuntimeException {
        UnpackMsgRequestSignature msgRequestSignature
                = new UnpackMsgRequestSignature(grpcMsgReqSsig.getMessage().toByteArray());

        if (!msgRequestSignature.isMacValid()) {
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_INVALID_HMAC);
        }

        if (!AuthGeneralUtil.isBlockInTime(msgRequestSignature.getTime())) {
            throw new ErrorMsgException(ErrorMsgException.MsgErr.MSG_EXPIRED);
        }

        String time = AuthGeneralUtil.getTimestamp();
        String signature;
        try {
            SignedBlock block = new SignedBlock();
            block.setChainId(msgRequestSignature.getChainId());
            block.setBlockHeight(msgRequestSignature.getBlockHeight());
            blockDao.insertAll();

            signature = authCertUtil.generateSupportSignature(sender, time, msgRequestSignature.getmID(), GruutConfigs.localChainId,
                    msgRequestSignature.getBlockHeight(), msgRequestSignature.getTransaction());
            log.postValue("Signature generated!");
        } catch (Exception e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.SIGNING_ERROR);
        }

        PackMsgSignature msgSignature = new PackMsgSignature(
                sender,
                time,
                signature
        );
        msgSignature.setDestinationId(msgRequestSignature.getmID());

        log.postValue("[SEND]" + "MSG_SSIG");
        try {
            new GrpcTask(channel).execute(msgSignature);
        } catch (StatusRuntimeException e) {
            throw e;
        }

    }

    public MutableLiveData<String> getLogMerger1() {
        return logMerger1;
    }

    public MutableLiveData<String> getLogMerger2() {
        return logMerger2;
    }

    public MutableLiveData<String> getIpMerger1() {
        return ipMerger1;
    }

    public MutableLiveData<String> getIpMerger2() {
        return ipMerger2;
    }

    public MutableLiveData<String> getPortMerger1() {
        return portMerger1;
    }

    public MutableLiveData<String> getPortMerger2() {
        return portMerger2;
    }

    public SingleLiveEvent getRefreshMerger1() {
        return refreshMerger1;
    }

    public SingleLiveEvent getRefreshMerger2() {
        return refreshMerger2;
    }

    public SingleLiveEvent getOpenSetting1Dialog() {
        return openSetting1Dialog;
    }

    public SingleLiveEvent getOpenSetting2Dialog() {
        return openSetting2Dialog;
    }

    @Override
    protected void onCleared() {
        try {
            if (channel1 != null && !channel1.isShutdown()) {
                channel1.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            }

            if (channel2 != null && !channel2.isShutdown()) {
                channel2.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    private static class GrpcTask extends AsyncTask<MsgPacker, Void, MsgUnpacker> {

        private long start;
        private ManagedChannel channel;

        private GrpcTask(ManagedChannel channel) {
            this.channel = channel;
        }

        @Override
        protected MsgUnpacker doInBackground(MsgPacker... msgPackers) {
            MsgPacker msg = msgPackers[0];

            GruutNetworkServiceGrpc.GruutNetworkServiceBlockingStub stub = GruutNetworkServiceGrpc.newBlockingStub(channel);
            start = System.currentTimeMillis();

            try {
                switch (msg.getMessageType()) {
                    case MSG_JOIN:
                        GrpcMsgJoin grpcMsgJoin = GrpcMsgJoin.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();
                        GrpcMsgChallenge grpcMsgChallenge = stub.withDeadlineAfter(GruutConfigs.GRPC_TIMEOUT, TimeUnit.SECONDS).join(grpcMsgJoin);
                        return new UnpackMsgChallenge(grpcMsgChallenge.getMessage().toByteArray());
                    case MSG_RESPONSE_1:
                        GrpcMsgResponse1 grpcMsgResponse1 = GrpcMsgResponse1.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();
                        GrpcMsgResponse2 grpcMsgResponse2 = stub.withDeadlineAfter(GruutConfigs.GRPC_TIMEOUT, TimeUnit.SECONDS).dhKeyEx(grpcMsgResponse1);
                        return new UnpackMsgResponse2(grpcMsgResponse2.getMessage().toByteArray());
                    case MSG_SUCCESS:
                        GrpcMsgSuccess grpcMsgSuccess = GrpcMsgSuccess.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();
                        GrpcMsgAccept grpcMsgAccept = stub.withDeadlineAfter(GruutConfigs.GRPC_TIMEOUT, TimeUnit.SECONDS).keyExFinished(grpcMsgSuccess);
                        return new UnpackMsgAccept(grpcMsgAccept.getMessage().toByteArray());
                    case MSG_SSIG:
                        GrpcMsgSsig grpcMsgSsig = GrpcMsgSsig.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();
                        stub.withDeadlineAfter(GruutConfigs.GRPC_TIMEOUT, TimeUnit.SECONDS).sigSend(grpcMsgSsig);
                        return null;
                    default:
                        return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MsgUnpacker result) {
            Log.d(TAG, "Result: " + result);
            Log.d(TAG, "Response Time: " + (System.currentTimeMillis() - start));
        }
    }
}
