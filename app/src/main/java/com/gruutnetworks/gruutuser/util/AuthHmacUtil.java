package com.gruutnetworks.gruutuser.util;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.util.encoders.Hex;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static com.gruutnetworks.gruutuser.util.SecurityConstants.*;

public class AuthHmacUtil {

    private static final String TAG = "AuthHmacUtil";

    private static AuthHmacUtil authHmacUtil;

    public static AuthHmacUtil getInstance() {
        if (authHmacUtil != null) {
            return authHmacUtil;
        }
        authHmacUtil = new AuthHmacUtil();

        // Add Provider
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        return authHmacUtil;
    }

    /**
     * Extract X point from  public key
     *
     * @param pubKey public key
     * @return X point value on EC (hex)
     */
    public byte[] pubToXpoint(PublicKey pubKey) {
        ECPublicKey key = (ECPublicKey) pubKey;
        byte[] bytes = key.getW().getAffineX().toByteArray();

        // BigInteger를 byte array로 변환 하면 자동으로 맨 앞에 0을 붙여서 출력하므로 제거함
        if (bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }

        return Hex.encode(bytes);
    }

    /**
     * Extract Y point from  public key
     *
     * @param pubKey public key
     * @return Y point value on EC (hex)
     */
    public byte[] pubToYpoint(PublicKey pubKey) {
        ECPublicKey key = (ECPublicKey) pubKey;
        byte[] bytes = key.getW().getAffineY().toByteArray();

        // BigInteger를 byte array로 변환 하면 자동으로 맨 앞에 0을 붙여서 출력하므로 제거함
        if (bytes[0] == 0) {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }

        return Hex.encode(bytes);
    }

    /**
     * Decode Public Key From EC Point Value
     *
     * @param x hex value
     * @param y hex value
     * @return public key
     */
    public PublicKey pointToPub(String x, String y)
            throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_SECP256R1);
        ECCurve curve = ecParameterSpec.getCurve();

        String str = "04" + x + y; // 04 for encoding type
        org.spongycastle.math.ec.ECPoint point = curve.decodePoint(Hex.decode(str));

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);
        KeyFactory kf = KeyFactory.getInstance(TYPE_ECDH, "SC");

        return kf.generatePublic(publicKeySpec);
    }

    /**
     * Generate ECDH key pair on secp256r1 Curve
     *
     * @return generated ECDH key pair
     */
    public KeyPair generateEcdhKeys()
            throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator kp = KeyPairGenerator.getInstance(TYPE_ECDH, "SC");
        kp.initialize(new ECGenParameterSpec(CURVE_SECP256R1), new SecureRandom());

        return kp.generateKeyPair();
    }

    /**
     * Generate DH shared secret mac key
     *
     * @param myPrvKey     my private key
     * @param othersPubKey other's public key
     * @return shared secret key(SHA256 encoded byte array)
     */
    public byte[] getSharedSecreyKey(PrivateKey myPrvKey, PublicKey othersPubKey)
            throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement ka = KeyAgreement.getInstance(TYPE_ECDH, "SC");
        ka.init(myPrvKey);
        ka.doPhase(othersPubKey, true);
        return encodeSha256(ka.generateSecret());
    }

    /**
     * @param bytes to decode into SHA256
     * @return encoded SHA256 byte array
     */
    private byte[] encodeSha256(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(TYPE_SHA256);
        md.update(bytes);
        return Hex.encode(md.digest());
    }

    /**
     * Generates HMAC signature
     *
     * @param id    merger`s id (Base64)
     * @param data 인증 할 Data
     * @return HMAC signature
     */
    public static byte[] getHmacSignature(String id, byte[] data) {
        try {
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance();
            // HMAC sign할 때 쓸 key(DH Key교환으로 생성한 shared secret key
            String key = preferenceUtil.getValue(id);

            if (key == null || key.isEmpty()) {
                // Shared secret key not found.
                return null;
            }

            Mac sha256Hmac = Mac.getInstance(TYPE_HMAC);
            SecretKeySpec secretKey = new SecretKeySpec(Hex.decode(key.getBytes()), TYPE_HMAC);
            sha256Hmac.init(secretKey);

            return sha256Hmac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verify HMAC signature
     *
     * @param id    merger`s id (Base64)
     * @param data verify 할 Data
     * @param mac 전송 받은 Message Auth Code
     * @return mac 검증 결과 반환
     */
    public static boolean verifyHmacSignature(String id, byte[] data, byte[] mac) {
        return Arrays.equals(getHmacSignature(id, data), mac);
    }

}
