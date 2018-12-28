package com.gruutnetworks.gruutsigner.util;

import android.util.Base64;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.lang.reflect.Method;
import java.security.cert.X509Certificate;

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
    public void setUp() throws Exception {
        authCertUtil = AuthCertUtil.getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void stringToCertificate() {
        String certPem = "-----BEGIN CERTIFICATE-----\n" +
                "MIIBFTCBvKADAgECAgIFOTAKBggqhkjOPQQDAjAUMRIwEAYDVQQDDAlTRUxGX0NFUlQwHhcN" +
                "MTgxMjI4MDIwOTM3WhcNNDgxMjI4MDIwOTM3WjAUMRIwEAYDVQQDDAlTRUxGX0NFUlQwWTAT" +
                "BgcqhkjOPQIBBggqhkjOPQMBBwNCAAT/+CWhnJTsFS/1h8o+ZUbfBcoRhIGXPXJhFJ/0JyWW" +
                "b/7kDiINzTjkKX+phoB3gCnKiArypjFNZTvshJq7/Mm8MAoGCCqGSM49BAMCA0gAMEUCIFzb" +
                "JoTGBKDsPQ7v3gmqGe1aKNUwlHahO12UOeliOGZoAiEA7D3OQiscVKonr1eLli6tgyeXJqQH" +
                "3x6iUgtrNJcTHb8=\n" +
                "-----END CERTIFICATE-----";

        AuthCertUtilTest.invokeMethod(AuthCertUtil.class, "stringToCertificate", X509Certificate.class, certPem);
    }
}