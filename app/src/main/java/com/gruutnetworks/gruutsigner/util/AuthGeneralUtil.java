package com.gruutnetworks.gruutsigner.util;

import android.util.Base64;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Calendar;

public class AuthGeneralUtil {

    private static final int MAX_NONCE_LENGTH = 32;

    /**
     * get UNIX timestamp of current time
     */
    public static String getTimestamp() {
        return String.valueOf((Calendar.getInstance().getTimeInMillis() / 1000));
    }

    /**
     * 32 byte Nonce를 Base64로 인코딩하여 반환
     *
     * @return Base64 encoded nonce value
     */
    public static String getNonce() {
        String candidateChars = "abcdef1234567890";

        SecureRandom generator = new SecureRandom();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < MAX_NONCE_LENGTH * 2; i++) {
            char tempChar = candidateChars.charAt(generator.nextInt(candidateChars.length()));
            outputStream.write(tempChar);
        }

        byte[] hexDecodedNonce = Hex.decode(outputStream.toByteArray());
        String base64EncodedNonce = new String(Base64.encode(hexDecodedNonce, Base64.NO_WRAP));
        try {
            outputStream.close();
        } catch (IOException e) {
            return null;
        }

        return base64EncodedNonce;
    }
}
