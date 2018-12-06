package com.gruutnetworks.gruutsigner.util;

import android.util.Base64;
import androidx.test.core.app.ApplicationProvider;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

@PrepareForTest({Base64.class})
public class KeystoreUtilTest extends RobolectricTest {

    private KeystoreUtil keystoreUtil;
    private String x = "AFD2927DB5BA6F149558084DC768245948A1645FA67D33CB8A688F98E8BB558A";
    private String y = "7C73B867F1B14EEC14B5442FB3309D450C9762FF915430FAE0299199B7F6744E";

    @Before
    public void setUp() throws Exception {
        keystoreUtil = KeystoreUtil.getInstance();

        // preference init
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(ApplicationProvider.getApplicationContext());
        preferenceUtil.put(PreferenceUtil.Key.HMAC_STR, "a9b56e68ccbfe9bdcb5dbc82e00859421abd6bfe3c28ee0e7d751e32baf1e65b");
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

    @Test
    public void generateHmac() {
        byte[] originalData = ("471058f104000000005747454e54455354310000000000003132000000000000f0267b2273656e646572223a224d54495c7530303364222c2274696d65223a2231353434303634323132222c2276616c223a747275657d").getBytes();
        byte[] hmac = KeystoreUtil.getHmacSignature(Hex.decode(originalData));

        assertThat(new String(Hex.encode(hmac)), is("440a8b1845499f90a65217ee4517ab983a30f5d6bd9778778351f805b8d2fa38"));
    }
}