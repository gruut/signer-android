package com.gruutnetworks.gruutsigner.util;

import org.junit.Before;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

public class KeystoreUtilTest {

    private KeystoreUtil keystoreUtil;
    private String x = "AFD2927DB5BA6F149558084DC768245948A1645FA67D33CB8A688F98E8BB558A";
    private String y = "7C73B867F1B14EEC14B5442FB3309D450C9762FF915430FAE0299199B7F6744E";

    @Before
    public void setUp() throws Exception {
        keystoreUtil = KeystoreUtil.getInstance();
    }

    @Test
    public void convertPointToPub() {
        try {
            PublicKey publicKey = keystoreUtil.pointToPub(x, y);
            String extractX = new String(keystoreUtil.pubToXpoint(publicKey));
            String extractY = new String(keystoreUtil.pubToYpoint(publicKey));

            assertThat(extractX, is(equalToIgnoringCase(x)));
            assertThat(extractY, is(equalToIgnoringCase(y)));
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}