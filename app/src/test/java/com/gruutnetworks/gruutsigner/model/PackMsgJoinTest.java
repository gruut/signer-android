package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@PrepareForTest({Base64.class})
public class PackMsgJoinTest extends RobolectricTest {

    private PackMsgJoin msgJoin;
    private Gson gson;

    @Before
    public void setUp() throws Exception {
        msgJoin = new PackMsgJoin("UAABACACAAE=", "1543323592", "1.0.20181127", "AAAAAAAAAAE=");

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

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkMsgJoin() {
        byte[] bytes = msgJoin.convertToByteArr();

        MsgPackerTest msgPackerTest = new MsgPackerTest();
        byte[] parsedBody = msgPackerTest.getBodyByteArr(bytes);
        PackMsgJoin decomp = gson.fromJson(new String(parsedBody), PackMsgJoin.class);

        assertThat(decomp.bodyToJson(), is(msgJoin.bodyToJson()));
    }
}