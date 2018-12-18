package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import androidx.test.core.app.ApplicationProvider;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import com.gruutnetworks.gruutsigner.util.AuthGeneralUtil;
import com.gruutnetworks.gruutsigner.util.PreferenceUtil;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@PrepareForTest({Base64.class})
public class PackMsgSignatureTest extends RobolectricTest {

    private PackMsgSignature msgSignature;
    private Gson gson;
    private String mergerId = "TUVSR0VSLTE=";

    @Before
    public void setUp() throws Exception {
        msgSignature = new PackMsgSignature("MDAwMDAwMDE=", AuthGeneralUtil.getTimestamp(), "signature!");

        // preference init
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(ApplicationProvider.getApplicationContext());
        preferenceUtil.put(mergerId, "a9b56e68ccbfe9bdcb5dbc82e00859421abd6bfe3c28ee0e7d751e32baf1e65b");

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