package com.gruutnetworks.gruutsigner.util;

import android.util.Base64;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.lang.reflect.Method;
import java.security.cert.X509Certificate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@PrepareForTest({Base64.class})
public class AuthCertUtilTest extends RobolectricTest {

    private AuthCertUtil authCertUtil;

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Class<?> clazz, String methodName, Class<T> returnType, Object... args) {
        Class<?>[] parameters = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            parameters[i] = args[i].getClass();
        }
        try {
            Object obj = clazz.newInstance();
            Method method = obj.getClass().getDeclaredMethod(methodName, parameters);
            method.setAccessible(true);
            return (T) method.invoke(obj, args);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void setUp() {
        authCertUtil = AuthCertUtil.getInstance();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void stringToCertificate() {
        String certPem = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBFTCBvKADAgECAgIFOTAKBggqhkjOPQQDAjAUMRIwEAYDVQQDDAlTRUxGX0NF\n" +
                "UlQwHhcNMTgxMjI4MDIwOTM3WhcNNDgxMjI4MDIwOTM3WjAUMRIwEAYDVQQDDAlT\n" +
                "RUxGX0NFUlQwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAT/+CWhnJTsFS/1h8o+\n" +
                "ZUbfBcoRhIGXPXJhFJ/0JyWWb/7kDiINzTjkKX+phoB3gCnKiArypjFNZTvshJq7\n" +
                "/Mm8MAoGCCqGSM49BAMCA0gAMEUCIFzbJoTGBKDsPQ7v3gmqGe1aKNUwlHahO12U\n" +
                "OeliOGZoAiEA7D3OQiscVKonr1eLli6tgyeXJqQH3x6iUgtrNJcTHb8=\n-----END CERTIFICATE-----\n";

        String expected = certPem.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));

        X509Certificate cert = AuthCertUtilTest.invokeMethod(AuthCertUtil.class,
                "stringToCertificate",
                X509Certificate.class, certPem);

        try {
            String pemStr = AuthCertUtilTest.invokeMethod(AuthCertUtil.class,
                    "bytesToPemString", String.class, "CERTIFICATE", cert.getEncoded());

            assertThat(pemStr, is(expected));
        } catch (Exception e) {
            Assert.fail("Exception: " + e);
        }
    }
}