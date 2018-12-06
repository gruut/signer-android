package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import androidx.test.core.app.ApplicationProvider;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import com.gruutnetworks.gruutsigner.util.PreferenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@PrepareForTest(Base64.class)
public class UnpackMsgChallengeTest extends RobolectricTest {

    @Before
    public void setUp() throws Exception {
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(ApplicationProvider.getApplicationContext());
        preferenceUtil.put(PreferenceUtil.Key.HMAC_STR, "a9b56e68ccbfe9bdcb5dbc82e00859421abd6bfe3c28ee0e7d751e32baf1e65b");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parseMsgChallenge() {
        /*
         * "sender" : "AAAAAAAAAAE="
         * "time" : "1543323592"
         * "mN" : "Z+twlKQjBGwezre8aTFJcrC47BgiX9HLMR8piqNGArY="
         *
         * [header]
         * compression type : LZ4
         * local chain id : "AAAAAAAAAAE="
         */
        String sampleMsg = "RwBV/wQAAAAAgwAAAAAAAAABAAAAAAAAAAEAAAAAAAD0N3sibU4iOiJaK3R3bEtRakJHd2V6cmU4YVRGSm" +
                "NyQzQ3QmdpWDlITE1SOHBpcU5HQXJZXHUwMDNkIiwic2VuZGVyIjoiQUECABVFHQDwBHRpbWUiOiIxNTQzMzIzNTkyIn0=";

        UnpackMsgChallenge resMsgChallenge = new UnpackMsgChallenge(Base64.decode(sampleMsg, Base64.NO_WRAP));

        assertThat(resMsgChallenge.header.getMsgType(), is(TypeMsg.MSG_CHALLENGE));
        assertThat(resMsgChallenge.header.getCompressType(), is(TypeComp.LZ4));
        assertThat(resMsgChallenge.getMergerNonce(), is("Z+twlKQjBGwezre8aTFJcrC47BgiX9HLMR8piqNGArY="));
    }
}