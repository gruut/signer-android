package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.gruutnetworks.gruutsigner.*;
import com.gruutnetworks.gruutsigner.gruut.Merger;
import com.gruutnetworks.gruutsigner.gruut.Message;
import com.gruutnetworks.gruutsigner.gruut.MessageHeader;
import com.gruutnetworks.gruutsigner.model.*;
import com.gruutnetworks.gruutsigner.util.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.MSG_HEADER_LEN;

public class DashboardViewModel extends AndroidViewModel {

    private static final String TAG = "DashboardViewModel";

    private final SnackbarMessage snackbarMessage = new SnackbarMessage();
    MutableLiveData<String> testData = new MutableLiveData<>();

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

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.keystoreUtil = KeystoreUtil.getInstance();
        this.preferenceUtil = PreferenceUtil.getInstance(application.getApplicationContext());
        this.sender = Integer.toString(preferenceUtil.getInt(PreferenceUtil.Key.SID_INT));

        if (!NetworkUtil.isConnected(application.getApplicationContext())) {
            snackbarMessage.setValue(R.string.sign_up_error_network);
            return;
        }

//        channel1 = setChannel(MergerList.MERGER_LIST.get(0));
        // TODO 중간에 null이 나오면 다시 처음부터 시작...
//        GrpcMsgChallenge grpcMsgChallenge = requestJoin(channel1);
        sendSuccess(channel1, null);
    }

    private ManagedChannel setChannel(Merger merger) {
        return ManagedChannelBuilder.forAddress(merger.getUri(), merger.getPort()).usePlaintext().build();
    }

    private GrpcMsgChallenge requestJoin(ManagedChannel channel) {
        MessageJoin messageJoin = new MessageJoin();
        messageJoin.setSender(sender);
        messageJoin.setTime(AuthUtil.getTimestamp());
        messageJoin.setVer(ver);
        messageJoin.setLocalChainId(localChainId);

        MessageHeader header = new MessageHeader.Builder()
                .setMsgType(TypeMsg.MSG_JOIN.getType())
                .setTotalLen(MSG_HEADER_LEN + messageJoin.getJson().length)
                .setLocalChainId(localChainId.getBytes())
                .setSender(sender.getBytes())
                .build();

        Message message = new Message(header, messageJoin.getJson(), null);

        GruutNetworkServiceGrpc.GruutNetworkServiceBlockingStub stub = GruutNetworkServiceGrpc.newBlockingStub(channel);
        GrpcMsgJoin grpcMsgJoin = GrpcMsgJoin.newBuilder()
                .setMessage(ByteString.copyFrom(message.convertToByteArr()))
                .build();

        try {
            return stub.withDeadlineAfter(3, TimeUnit.SECONDS).join(grpcMsgJoin);
        } catch (StatusRuntimeException e) {
            Log.e(TAG, "Timeout... requestJoin");
            return null;
        }
    }

    private GrpcMsgResponse2 sendPublicKey(ManagedChannel channel, GrpcMsgChallenge challenge) {
        Gson gson = new Gson();
        //Message msg = new Message(challenge.getMessage().toByteArray());
        //MessageChallenge messageChallenge = gson.fromJson(new String(msg.getCompressedJsonMsg()), MessageChallenge.class);

        // generate signer nonce
        signerNonce = AuthUtil.getNonce();

        // Test Message
        MessageChallenge messageChallenge = new MessageChallenge();
        messageChallenge.setMergerNonce(AuthUtil.getNonce());
        messageChallenge.setTime(AuthUtil.getTimestamp());
        messageChallenge.setSender("Merger");

        // get merger nonce
        mergerNonce = messageChallenge.getMergerNonce();

        try {
            // generate ecdh key
            if (keyPair == null) {
                keyPair = keystoreUtil.ecdhKeyGen();
            }

            String pubKey = keystoreUtil.pubkeyToString(keyPair.getPublic());
            Log.d(TAG, "[Pubkey]" + pubKey);


            String time = AuthUtil.getTimestamp();
            String signature = keystoreUtil.signData(mergerNonce + signerNonce + pubKey + time);
            String cert = keystoreUtil.getCert(KeystoreUtil.SecurityConstants.Alias.GRUUT_AUTH);

            if (cert.isEmpty()) {
                Log.e(TAG, "There's no X509 certification. Please get a certification via Gruut Auth.");
                return null;
            }

            MessageResponse1 response1 = new MessageResponse1();
            response1.setSignerNonce(signerNonce);
            response1.setSig(signature);
            response1.setDhPubKey(pubKey);
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

            GrpcMsgResponse1 grpcMsgResponse1 = GrpcMsgResponse1.newBuilder()
                    .setMessage(ByteString.copyFrom(message.convertToByteArr()))
                    .build();

            GruutNetworkServiceGrpc.GruutNetworkServiceBlockingStub stub = GruutNetworkServiceGrpc.newBlockingStub(channel);
            return stub.withDeadlineAfter(3, TimeUnit.SECONDS).dHKeyEx(grpcMsgResponse1);
        } catch (StatusRuntimeException e) {
            Log.e(TAG, "Timeout... sendPublicKey");
            return null;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "ECDH Key generate error... sendPublicKey");
            return null;
        } catch (IOException | CertificateException | KeyStoreException | InvalidKeyException | SignatureException | UnrecoverableEntryException e) {
            Log.e(TAG, "Signing error... sendPublicKey");
            return null;
        }
    }

    private GrpcMsgAccept sendSuccess(ManagedChannel channel, GrpcMsgResponse2 response2) {
        Gson gson = new Gson();
        //Message msg = new Message(response2.getMessage().toByteArray());
        //MessageResponse2 messageResponse2 = gson.fromJson(new String(msg.getCompressedJsonMsg()), MessageResponse2.class);

        try {
            // Test Message

            // generate ecdh key
            if (keyPair == null) {
                keyPair = keystoreUtil.ecdhKeyGen();
            }
            String dh2 = new String(keyPair.getPublic().getEncoded());
            String time = AuthUtil.getTimestamp();
            MessageResponse2 messageResponse2 = new MessageResponse2();
            messageResponse2.setCert(keystoreUtil.getCert(KeystoreUtil.SecurityConstants.Alias.GRUUT_AUTH));
            messageResponse2.setDhPubKey(dh2);
            messageResponse2.setSig(keystoreUtil.signData(mergerNonce + signerNonce + dh2 + time)); // Nm Ns DH2 t
            messageResponse2.setTime(time);
            messageResponse2.setSender("Merger");

            String test = mergerNonce + signerNonce + dh2 + time;
            String sig = keystoreUtil.signData(test);
            Log.d(TAG, "without cert: " + keystoreUtil.verifyData(test, sig));
            Log.d(TAG, "with cert: " +keystoreUtil.verifyData(test, sig, keystoreUtil.getCert(KeystoreUtil.SecurityConstants.Alias.GRUUT_AUTH)));

            // signature 검증
            String input = mergerNonce + signerNonce + messageResponse2.getDhPubKey() + messageResponse2.getTime();
            if (!keystoreUtil.verifyData(input, messageResponse2.getSig(), messageResponse2.getCert())) {
                Log.e(TAG, "Signature is invalid.");
                return null;
            }

            // HMAC KEY 계산
            PublicKey mergerPubKey = keystoreUtil.stringToPubkey(messageResponse2.getDhPubKey());
            byte[] hmac = keystoreUtil.doEcdh(keyPair.getPrivate(), mergerPubKey);
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
            byte[] signature = keystoreUtil.getMacSig(preferenceUtil.getString(PreferenceUtil.Key.HMAC_STR),
                    message.convertToByteArrWithoutSig());

            message.setSignature(signature);

            GrpcMsgSuccess grpcMsgSuccess = GrpcMsgSuccess.newBuilder()
                    .setMessage(ByteString.copyFrom(message.convertToByteArr()))
                    .build();

            GruutNetworkServiceGrpc.GruutNetworkServiceBlockingStub stub = GruutNetworkServiceGrpc.newBlockingStub(channel);
            return stub.withDeadlineAfter(3, TimeUnit.SECONDS).keyExFinished(grpcMsgSuccess);
        } catch (Exception e) {
            Log.e(TAG, "ERROR..." + e.getMessage());
            return null;
        }
    }

    public MutableLiveData<String> getTestData() {
        return testData;
    }

}
