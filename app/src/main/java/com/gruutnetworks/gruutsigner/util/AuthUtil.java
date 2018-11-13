package com.gruutnetworks.gruutsigner.util;

import java.security.SecureRandom;
import java.util.Calendar;

public class AuthUtil {

    private static final int MAX_NONCE_LENGTH = 64;

    /**
     * get UNIX timestamp of current time
     */
    public static int getTimestamp() {
        return (int) (Calendar.getInstance().getTimeInMillis() / 1000);
    }

    /**
     * get 64 byte random string array
     * @return nonce value
     */
    public static String getNonce() {
        String candidateChars = "ABCDEFGHIZKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

        SecureRandom generator = new SecureRandom();
        StringBuilder randomStringBuilder = new StringBuilder();
        for (int i = 0; i < MAX_NONCE_LENGTH; i++){
            char tempChar = candidateChars.charAt(generator.nextInt(candidateChars.length()));
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
