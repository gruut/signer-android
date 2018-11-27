package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.*;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.gruutnetworks.gruutsigner.*;
import com.gruutnetworks.gruutsigner.Identity;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.exceptions.AsyncException;
import com.gruutnetworks.gruutsigner.exceptions.AuthUtilException;
import com.gruutnetworks.gruutsigner.exceptions.ErrorMsgException;
import com.gruutnetworks.gruutsigner.gruut.Merger;
import com.gruutnetworks.gruutsigner.gruut.MergerList;
import com.gruutnetworks.gruutsigner.gruut.Message;
import com.gruutnetworks.gruutsigner.gruut.MessageHeader;
import com.gruutnetworks.gruutsigner.model.*;
import com.gruutnetworks.gruutsigner.util.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.MSG_HEADER_LEN;
import static com.gruutnetworks.gruutsigner.model.TypeMsg.MSG_JOIN;

public class DashboardViewModel extends AndroidViewModel implements LifecycleObserver {

    private static final String TAG = "DashboardViewModel";

    private final SnackbarMessage snackbarMessage = new SnackbarMessage();
    public MutableLiveData<String> testData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isChannel1Set = new MutableLiveData<>();

    private KeystoreUtil keystoreUtil;
    private PreferenceUtil preferenceUtil;

    private ManagedChannel channel1;
    private ManagedChannel channel2;

    private String sender;
    private String localChainId = "12345";
    private String ver = "1";

    private String signerNonce;
    private String mergerNonce;

    private KeyPair keyPair;

    private Gson gson = new Gson();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.keystoreUtil = KeystoreUtil.getInstance();
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
        this.sender = Integer.toString(preferenceUtil.getInt(PreferenceUtil.Key.SID_INT));

        if (!NetworkUtil.isConnected(application.getApplicationContext())) {
            snackbarMessage.postValue(R.string.sign_up_error_network);
            return;
        }
    }

    /**
     * 화면이 다 그려졌을 때 join 시작
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        Merger merger = MergerList.MERGER_LIST.get(0);
        channel1 = setChannel(merger);
        testData.postValue("[Channel Setting]" + merger.getUri() + ":" + merger.getPort());
        isChannel1Set.postValue(channel1 != null);
    }

    void startJoining() {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                try {
                    MessageChallenge challenge = requestJoin(channel1);
                    MessageResponse2 response2 = sendPublicKey(channel1, challenge);
                    MessageAccept accept = sendSuccess(channel1, response2);
                    if (accept.isVal()) {
                        standBy(channel1);
                    }
                } catch (ErrorMsgException e) {
                    testData.postValue("[ERROR]" + "MSG_ERROR received...");
                } catch (AuthUtilException e) {
                    testData.postValue("[CRYPTO_ERROR]" + e.getMessage());
                }
            }
        }.start();
    }



    private ManagedChannel setChannel(Merger merger) {
        return ManagedChannelBuilder.forAddress(merger.getUri(), merger.getPort()).usePlaintext().build();
    }

    /**
     * Start joining request
     * SEND MSG_JOIN to merger
     *
     * @param channel target merger
     * @return received MSG_CHALLENGE
     * @throws StatusRuntimeException on GRPC error
     */
    private MessageChallenge requestJoin(ManagedChannel channel) throws StatusRuntimeException {
        testData.postValue("START requestJoin...");
        MessageJoin messageJoin = new MessageJoin();
        messageJoin.setSender(sender);
        messageJoin.setTime(AuthUtil.getTimestamp());
        messageJoin.setVer(ver);
        messageJoin.setLocalChainId(localChainId);

        MessageHeader header = new MessageHeader.Builder()
                .setMsgType(MSG_JOIN.getType())
                .setTotalLen(MSG_HEADER_LEN + messageJoin.getJson().length)
                .setLocalChainId(localChainId.getBytes())
                .setSender(sender.getBytes())
                .build();

        Message message = new Message(header, messageJoin.getJson(), null);
        Message receivedMsg = null;
        try {
            testData.postValue("[SEND]" + "MSG_JOIN");
            receivedMsg = new GrpcTask(channel).execute(message).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AsyncException();
        } catch (StatusRuntimeException e) {
            throw e;
        }

        // Check received message's type
        if (receivedMsg == null || receivedMsg.getHeader().getMsgType() == TypeMsg.MSG_ERROR) {
            throw new ErrorMsgException();
        }

        testData.postValue("[RECEIVE]" + "MSG_CHALLENGE");
        return gson.fromJson(new String(receivedMsg.getBody()), MessageChallenge.class);
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
    private MessageResponse2 sendPublicKey(ManagedChannel channel, MessageChallenge messageChallenge) throws StatusRuntimeException {
        testData.postValue("START sendPublicKey...");

        // generate signer nonce
        signerNonce = AuthUtil.getNonce();

        // get merger nonce
        mergerNonce = messageChallenge.getMergerNonce();

        if (keyPair == null) {
            // generate ecdh key
            testData.postValue("Generate ECDH key pair");
            try {
                keyPair = keystoreUtil.generateEcdhKeys();
            } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new AuthUtilException(AuthUtilException.AuthErr.KEY_GEN_ERROR);
            }
        }

        String x = new String(keystoreUtil.pubToXpoint(keyPair.getPublic()));
        String y = new String(keystoreUtil.pubToYpoint(keyPair.getPublic()));
        String time = AuthUtil.getTimestamp();
        String sigTarget = mergerNonce + signerNonce + x + y + time;

        // Generate Signature
        String signature = null;
        try {
            signature = keystoreUtil.signData(sigTarget);
        } catch (KeyStoreException | UnrecoverableEntryException | NoSuchAlgorithmException
                | SignatureException | InvalidKeyException | CertificateException | IOException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.SIGNING_ERROR);
        }

        // Get Certificate issued by GA
        String cert = null;
        try {
            cert = keystoreUtil.getCert(KeystoreUtil.SecurityConstants.Alias.GRUUT_AUTH);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.GET_CERT_ERROR);
        }

        if (cert.isEmpty()) {
            throw new AuthUtilException(AuthUtilException.AuthErr.NO_CERT_ERROR);
        }

        MessageResponse1 response1 = new MessageResponse1();
        response1.setSignerNonce(signerNonce);
        response1.setSig(signature);
        response1.setDhPubKeyX(x);
        response1.setDhPubKeyY(y);
        response1.setSender(sender);
        response1.setTime(time);
        response1.setCert(cert);

        MessageHeader header = new MessageHeader.Builder()
                .setMsgType(TypeMsg.MSG_RESPONSE_1.getType())
                .setTotalLen(MSG_HEADER_LEN + response1.getJson().length)
                .setLocalChainId(localChainId.getBytes())
                .setSender(sender.getBytes())
                .build();

        Message message = new Message(header, response1.getJson(), null);
        Message receivedMsg = null;
        try {
            testData.postValue("[SEND]" + "MSG_RESPONSE_1");
            receivedMsg = new GrpcTask(channel).execute(message).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AsyncException();
        } catch (StatusRuntimeException e) {
            throw e;
        }


        // Check received message's type
        if (receivedMsg == null || receivedMsg.getHeader().getMsgType() == TypeMsg.MSG_ERROR) {
            throw new ErrorMsgException();
        }

        testData.postValue("[RECEIVED]" + "MSG_RESPONSE_2");
        return gson.fromJson(new String(receivedMsg.getBody()), MessageResponse2.class);
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
    private MessageAccept sendSuccess(ManagedChannel channel, MessageResponse2 messageResponse2) throws StatusRuntimeException {
        // signature 검증
        String input = mergerNonce + signerNonce + messageResponse2.getDhPubKeyX() +
                messageResponse2.getDhPubKeyY() + messageResponse2.getTime();
        boolean isSigValid = false;
        try {
            isSigValid = keystoreUtil.verifyData(input, messageResponse2.getSig(), messageResponse2.getCert());
        } catch (CertificateException | NoSuchAlgorithmException | InvalidKeyException |
                SignatureException | NoSuchProviderException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.VERIFYING_ERROR);
        }

        if (!isSigValid) {
            throw new AuthUtilException(AuthUtilException.AuthErr.INVALID_SIGNATURE);
        }

        // X,Y 좌표로부터 Pulbic key get
        PublicKey mergerPubKey = null;
        try {
            mergerPubKey = keystoreUtil.pointToPub(messageResponse2.getDhPubKeyX(), messageResponse2.getDhPubKeyY());
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.KEY_GEN_ERROR);
        }

        // HMAC KEY 계산
        byte[] hmac;
        try {
            hmac = keystoreUtil.getSharedSecreyKey(keyPair.getPrivate(), mergerPubKey);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new AuthUtilException(AuthUtilException.AuthErr.HMAC_KEY_GEN_ERROR);
        }

        // HMAC KEY 저장
        preferenceUtil.put(PreferenceUtil.Key.HMAC_STR, new String(hmac));

        // 성공 메세지 송신
        MessageSuccess messageSuccess = new MessageSuccess();
        messageSuccess.setSender(sender);
        messageSuccess.setTime(AuthUtil.getTimestamp());
        messageSuccess.setVal(true);

        MessageHeader header = new MessageHeader.Builder()
                .setMsgType(TypeMsg.MSG_SUCCESS.getType())
                .setMacType(TypeMac.HMAC_SHA256.getType())
                .setTotalLen(MSG_HEADER_LEN + messageSuccess.getJson().length)
                .setLocalChainId(localChainId.getBytes())
                .setSender(sender.getBytes())
                .build();

        Message message = new Message(header, messageSuccess.getJson(), null);
        byte[] mac = keystoreUtil.getHmacSignature(preferenceUtil.getString(PreferenceUtil.Key.HMAC_STR),
                message.convertToByteArrWithoutSig());

        message.setMac(mac);

        Message receivedMsg = null;
        try {
            testData.postValue("[SEND]" + "MSG_SUCCESS");
            receivedMsg = new GrpcTask(channel).execute(message).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AsyncException();
        } catch (StatusRuntimeException e) {
            throw e;
        }


        // Check received message's type
        if (receivedMsg == null || receivedMsg.getHeader().getMsgType() == TypeMsg.MSG_ERROR) {
            throw new ErrorMsgException();
        }

        testData.postValue("[RECEIVED]" + "MSG_ACCEPT");
        return gson.fromJson(new String(receivedMsg.getBody()), MessageAccept.class);
    }

    private void standBy(ManagedChannel channel) {
        GruutNetworkServiceGrpc.GruutNetworkServiceStub stub = GruutNetworkServiceGrpc.newStub(channel);
        StreamObserver<Identity> standBy = stub.openChannel(new StreamObserver<GrpcMsgReqSsig>() {
            @Override
            public void onNext(GrpcMsgReqSsig value) {
                // Signature request from Merger
                testData.postValue("I've got MSG_REQ_SSIG!");
                try {
                    sendSignature(channel, value);
                } catch (StatusRuntimeException e) {
                    testData.postValue("[ERROR]" + "Timeout... I gave 5 seconds of mercy.");
                }
            }

            @Override
            public void onError(Throwable t) {
                testData.postValue("This Merger is DEAD... Now Dobby is free!");
            }

            @Override
            public void onCompleted() {

            }
        });

        standBy.onNext(Identity.newBuilder().setSender(ByteString.copyFrom(sender.getBytes())).build());
        testData.postValue("Streaming channel opened...standby for signature request");
        Log.d(TAG, "Streaming channel opened...standby for signature request");
    }

    private void sendSignature(ManagedChannel channel, GrpcMsgReqSsig grpcMsgReqSsig) throws StatusRuntimeException {
        Message msg = new Message(grpcMsgReqSsig.getMessage().toByteArray());
        String signature;
        try {
            // TODO 임시블록 검증하는 부분이 필요함.
            MessageRequestSignature requestSignature = gson.fromJson(new String(msg.getBody()), MessageRequestSignature.class);

            // TODO hard coded. check this out later
            ByteBuffer buffer = ByteBuffer.allocate(72);
            buffer.putInt(Integer.parseInt(sender));
            buffer.putInt(Integer.parseInt(requestSignature.getTime()));
            buffer.put(Base64.decode(requestSignature.getmID(), Base64.NO_WRAP));
            buffer.putInt(Integer.parseInt(requestSignature.getBlockHeight()));
            buffer.put(Base64.decode(requestSignature.getTransaction(), Base64.NO_WRAP));

            signature = keystoreUtil.signData(new String(buffer.array()));
            buffer.clear();

            testData.postValue("Signature generated!");
        } catch (Exception e) {
            testData.postValue("Error... signing...\n" + e.getMessage());
            Log.e(TAG, "Error... signing...\n" + e.getMessage());
            return;
        }

        MessageSignature messageSignature = new MessageSignature();
        messageSignature.setSid(sender);
        messageSignature.setTime(AuthUtil.getTimestamp());
        messageSignature.setSignature(signature);

        MessageHeader header = new MessageHeader.Builder()
                .setMsgType(TypeMsg.MSG_SSIG.getType())
                .setMacType(TypeMac.HMAC_SHA256.getType())
                .setTotalLen(MSG_HEADER_LEN + messageSignature.getJson().length)
                .setLocalChainId(localChainId.getBytes())
                .setSender(sender.getBytes())
                .build();

        Message message = new Message(header, messageSignature.getJson(), null);
        byte[] macSig = keystoreUtil.getHmacSignature(preferenceUtil.getString(PreferenceUtil.Key.HMAC_STR),
                message.convertToByteArrWithoutSig());

        message.setMac(macSig);
        testData.postValue("[SEND]" + "MSG_JOIN");
        try {
            new GrpcTask(channel).execute(message);
        } catch (StatusRuntimeException e) {
            throw e;
        }

    }

    public MutableLiveData<String> getTestData() {
        return testData;
    }

    public MutableLiveData<Boolean> getIsChannel1Set() {
        return isChannel1Set;
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

    private static class GrpcTask extends AsyncTask<Message, Void, Message> {

        private static final int TIME_OUT = 5;
        private long start;
        private ManagedChannel channel;

        private GrpcTask(ManagedChannel channel) {
            this.channel = channel;
        }

        @Override
        protected Message doInBackground(Message... messages) {
            Message msg = messages[0];

            GruutNetworkServiceGrpc.GruutNetworkServiceBlockingStub stub = GruutNetworkServiceGrpc.newBlockingStub(channel);

            start = System.currentTimeMillis();
            try {
                switch (msg.getHeader().getMsgType()) {
                    case MSG_JOIN:
                        GrpcMsgJoin grpcMsgJoin = GrpcMsgJoin.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();

                        GrpcMsgChallenge grpcMsgChallenge = stub.withDeadlineAfter(TIME_OUT, TimeUnit.SECONDS).join(grpcMsgJoin);
                        return new Message(grpcMsgChallenge.getMessage().toByteArray());
                    case MSG_RESPONSE_1:
                        GrpcMsgResponse1 grpcMsgResponse1 = GrpcMsgResponse1.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();

                        GrpcMsgResponse2 grpcMsgResponse2 = stub.withDeadlineAfter(TIME_OUT, TimeUnit.SECONDS).dhKeyEx(grpcMsgResponse1);
                        return new Message(grpcMsgResponse2.getMessage().toByteArray());
                    case MSG_SUCCESS:
                        GrpcMsgSuccess grpcMsgSuccess = GrpcMsgSuccess.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();
                        GrpcMsgAccept grpcMsgAccept = stub.withDeadlineAfter(TIME_OUT, TimeUnit.SECONDS).keyExFinished(grpcMsgSuccess);
                        return new Message(grpcMsgAccept.getMessage().toByteArray());
                    case MSG_SSIG:
                        GrpcMsgSsig grpcMsgSsig = GrpcMsgSsig.newBuilder()
                                .setMessage(ByteString.copyFrom(msg.convertToByteArr()))
                                .build();
                        stub.withDeadlineAfter(TIME_OUT, TimeUnit.SECONDS).sigSend(grpcMsgSsig);
                        return null;
                    default:
                        return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Message result) {
            Log.d(TAG, "Result: " + result);
            Log.d(TAG, "Response Time: " + (System.currentTimeMillis() - start));
        }
    }
}
