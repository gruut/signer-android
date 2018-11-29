package com.gruutnetworks.gruutsigner.gruut;

import android.util.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Base64.class})
public class ResMsgChallengeTest {

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Base64.class);
        when(Base64.encode(any(), anyInt())).thenAnswer(invocation -> java.util.Base64.getEncoder().encode((byte[]) invocation.getArguments()[0]));
        when(Base64.decode(anyString(), anyInt())).thenAnswer(invocation -> java.util.Base64.getMimeDecoder().decode((String) invocation.getArguments()[0]));
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
         * compression type : LZ4
         * local chain id : "AAAAAAAAAAE="
         */
        String sampleMsg = "RwBV/wQAAAAAgwAAAAAAAAABAAAAAAAAAAEAAAAAAAD0N3sibU4iOiJaK3R3bEtRakJHd2V6cmU4YVRGSm" +
                "NyQzQ3QmdpWDlITE1SOHBpcU5HQXJZXHUwMDNkIiwic2VuZGVyIjoiQUECABVFHQDwBHRpbWUiOiIxNTQzMzIzNTkyIn0=";

        ResMsgChallenge resMsgChallenge = new ResMsgChallenge(Base64.decode(sampleMsg, Base64.NO_WRAP));
    }
}