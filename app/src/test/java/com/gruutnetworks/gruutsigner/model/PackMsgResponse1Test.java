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
public class PackMsgResponse1Test extends RobolectricTest {

    private PackMsgResponse1 msgResponse1;
    private Gson gson;

    @Before
    public void setUp() throws Exception {
        msgResponse1 = new PackMsgResponse1("UAABACACAAE=", "1543323592",
                "-----BEGIN CERTIFICATE-----\nMIIDLDCCAhQCBgEZlK1CPjA....\n-----END CERTIFICATE-----",
                "luLSQgVDnMyZjkAh8h2HNokaY1Oe9Md6a4VpjcdGgzs=",
                "92943e52e02476bd1a4d74c2498db3b01c204f29a32698495b4ed0a274e12294",
                "96e2d24205439ccc998e4021f21d8736891a63539ef4c77a6b85698dc746833b",
                "QWVMP1UfUJIemaLFqnXvQfGqghVCmYH0yXo1/g5hUWAbouuXdTI/O7Gkgz3C5kXhnIWZ+dHp....");

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
    public void checkMsgResponse1() {
        byte[] bytes = msgResponse1.convertToByteArr();

        MsgPackerTest msgPackerTest = new MsgPackerTest();
        byte[] parsedBody = msgPackerTest.getBodyByteArr(bytes);
        PackMsgResponse1 decomp = gson.fromJson(new String(parsedBody), PackMsgResponse1.class);

        assertThat(decomp.bodyToJson(), is(msgResponse1.bodyToJson()));
    }
}