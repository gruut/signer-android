package com.gruutnetworks.gruutsigner.util;

import android.util.Base64;
import androidx.test.core.app.ApplicationProvider;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import junit.framework.Assert;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.security.KeyPair;
import java.security.PublicKey;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

@PrepareForTest({Base64.class})
public class AuthHmacUtilTest extends RobolectricTest {

    private AuthHmacUtil authHmacUtil;
    private PreferenceUtil preferenceUtil;
    private KeyPair keyPair;

    @Before
    public void setUp() {
        authHmacUtil = AuthHmacUtil.getInstance();

        // preference init
        preferenceUtil = PreferenceUtil.getInstance(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void convertTest() {
        try {
            keyPair = authHmacUtil.generateEcdhKeys();
        } catch (Exception e){
            Assert.fail("Key Gen Exception: " + e);
        }

        PublicKey publicKey = keyPair.getPublic();
        String x = new String(authHmacUtil.pubToXpoint(publicKey));
        String y = new String(authHmacUtil.pubToYpoint(publicKey));

        try {
            PublicKey convertedKey = authHmacUtil.pointToPub(x, y);

            String extractX = new String(authHmacUtil.pubToXpoint(convertedKey));
            String extractY = new String(authHmacUtil.pubToYpoint(convertedKey));

            assertThat(extractX, is(equalToIgnoringCase(x)));
            assertThat(extractY, is(equalToIgnoringCase(y)));
        } catch (Exception e){
            Assert.fail("Exception: " + e);
        }
    }

    @Test
    public void getSharedSecretKey() {
        try {
            keyPair = authHmacUtil.generateEcdhKeys();
        } catch (Exception e){
            Assert.fail("Key Gen Exception: " + e);
        }

        String otherX = "AFD2927DB5BA6F149558084DC768245948A1645FA67D33CB8A688F98E8BB558A";
        String otherY = "7C73B867F1B14EEC14B5442FB3309D450C9762FF915430FAE0299199B7F6744E";
        try {
            PublicKey othersPubKey = authHmacUtil.pointToPub(otherX, otherY);

            byte[] hmacKey = authHmacUtil.getSharedSecreyKey(keyPair.getPrivate(), othersPubKey);
            preferenceUtil.put(PreferenceUtil.Key.HMAC_STR, new String(hmacKey));
        } catch (Exception e){
            Assert.fail("Exception: " + e);
        }
    }

    @Test
    public void hmacTest() {
        // generate hmac
        byte[] hexConvertedData = ("471058f104000000005747454e54455354310000000000003132000000000000f0267b2273656e646572223a224d54495c7530303364222c2274696d65223a2231353434303634323132222c2276616c223a747275657d").getBytes();
        byte[] hmac = AuthHmacUtil.getHmacSignature(Hex.decode(hexConvertedData));

        // verify hmac
        assertThat(AuthHmacUtil.verifyHmacSignature(Hex.decode(hexConvertedData), hmac), is(true));
    }
}