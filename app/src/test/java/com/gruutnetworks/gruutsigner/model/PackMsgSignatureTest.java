package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import androidx.test.core.app.ApplicationProvider;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import com.gruutnetworks.gruutsigner.util.AuthUtil;
import com.gruutnetworks.gruutsigner.util.PreferenceUtil;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@PrepareForTest({Base64.class})

public class PackMsgSignatureTest extends RobolectricTest {

    private PackMsgSignature msgSignature;
    private Gson gson;

    @Before
    public void setUp() throws Exception {
        msgSignature = new PackMsgSignature("14", AuthUtil.getTimestamp(), "signature!");

        // preference init
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(ApplicationProvider.getApplicationContext());
        preferenceUtil.put(PreferenceUtil.Key.HMAC_STR, "test_key");

        gson = new GsonBuilder().addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(MsgPacker.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
    }

    @Test
    public void checkHmac() {
        byte[] bytes = msgSignature.convertToByteArr();

        MsgPackerTest msgPackerTest = new MsgPackerTest();
        byte[] parsedBody = msgPackerTest.getBodyByteArr(bytes);
        PackMsgSignature decomp = gson.fromJson(new String(parsedBody), PackMsgSignature.class);

        assertThat(decomp.bodyToJson(), is(msgSignature.bodyToJson()));
        assertThat(msgPackerTest.checkMacValidity(msgPackerTest.header, msgPackerTest.compressedMsg, msgPackerTest.mac),
                is(true));
    }
}