package com.gruutnetworks.gruutsigner.ui.signup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.gson.Gson;
import com.gruutnetworks.gruutsigner.R;
import com.gruutnetworks.gruutsigner.gruut.Message;
import com.gruutnetworks.gruutsigner.gruut.MessageHeader;
import com.gruutnetworks.gruutsigner.model.TypeComp;
import com.gruutnetworks.gruutsigner.model.TypeMac;
import com.gruutnetworks.gruutsigner.model.MessageJoin;
import com.gruutnetworks.gruutsigner.model.TypeMsg;
import com.gruutnetworks.gruutsigner.util.AuthUtil;
import org.jetbrains.annotations.TestOnly;

import java.nio.ByteBuffer;

import static com.gruutnetworks.gruutsigner.gruut.MessageHeader.MSG_HEADER_LEN;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SignUpFragment.newInstance())
                    .commitNow();
        }
        msgParsingTest();
    }

    @TestOnly
    private void msgParsingTest() {
        Gson gson = new Gson();

        String sender = "1234";
        String localChainId = "12345";
        String ver = "1";

        MessageJoin messageJoin = new MessageJoin();
        messageJoin.setSender(sender);
        messageJoin.setTime(AuthUtil.getTimestamp() + "");
        messageJoin.setLocalChainId(localChainId);
        messageJoin.setVer(ver);

        byte[] json = gson.toJson(messageJoin).getBytes();
        MessageHeader header = new MessageHeader.Builder()
                .setMainVersion((byte) 0x04)
                .setSubVersion((byte) 0x0A)
                .setMsgType(TypeMsg.MSG_JOIN.getType())
                .setMacType(TypeMac.HMAC_SHA256.getType())
                .setCompressionType(TypeComp.NONE.getType())
                .setSender(sender.getBytes())
                .setLocalChainId(localChainId.getBytes())
                .setTotalLen(ByteBuffer.allocate(4).putInt(MSG_HEADER_LEN + json.length).array())
                .build();

        Message msg = new Message(header, json, "Signature".getBytes());
        Log.d(TAG, "[Generated msg] " + msg.toString());

        Message msg2 = new Message(msg.covertToByteArr());
        Log.d(TAG, "[Generated byte array] " + msg2.toString());
    }
}