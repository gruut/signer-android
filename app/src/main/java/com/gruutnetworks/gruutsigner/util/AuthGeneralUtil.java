package com.gruutnetworks.gruutsigner.util;

import android.util.Base64;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Calendar;

import static com.gruutnetworks.gruutsigner.util.SecurityConstants.MSG_EXPIRATION_TIME;

public class AuthGeneralUtil {

    private static final int MAX_NONCE_LENGTH = 32;

    /**
     * get UNIX timestamp of current time
     */
    public static String getTimestamp() {
        return String.valueOf((Calendar.getInstance().getTimeInMillis() / 1000));
    }

    /**
     * 메세지의 timestamp를 기준으로 하여 유효한 메세지인지 확인한다.
     *
     * @param msgTimestamp 메세지에 포함된 timestamp(UNIX timestamp)
     * @return 메세지의 validity
     */
    public static boolean isMsgInTime(String msgTimestamp) {
        int time = Integer.parseInt(msgTimestamp);
        int current = (int) (Calendar.getInstance().getTimeInMillis() / 1000);
        return (time + MSG_EXPIRATION_TIME > current);
    }

    /**
     * get 64 byte random string array
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
