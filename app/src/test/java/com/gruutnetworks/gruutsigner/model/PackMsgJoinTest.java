package com.gruutnetworks.gruutsigner.model;

import android.util.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Base64.class})
public class PackMsgJoinTest {

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
    public void generateMsgJoin() {
        PackMsgJoin msgJoin = new PackMsgJoin("UAABACACAAE=", "1543323592", "1.0.20181127", "AAAAAAAAAAE=");
    }
}