/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gruutnetworks.gruutsigner.util;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import org.jetbrains.annotations.TestOnly;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.util.encoders.Hex;

import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.gruutnetworks.gruutsigner.util.KeystoreUtil.SecurityConstants.*;

/**
 * https://github.com/googlesamples/android-BasicAndroidKeyStore
 */
public class KeystoreUtil {

    private static final String TAG = "KeystoreUtil";

    // You can store multiple key pairs in the Key Store.  The string used to refer to the Key you
    // want to store, or later pull, is referred to as an "alias" in this case, because calling it
    // a key, when you use it to retrieve a key, would just be irritating.
    private String mAlias = SecurityConstants.Alias.SELF_CERT.name();
    private static KeystoreUtil keystoreUtil;

    public static KeystoreUtil getInstance() {
        if (keystoreUtil != null) {
            return keystoreUtil;
        }
        keystoreUtil = new KeystoreUtil();
        return keystoreUtil;
    }

    /**
     * Creates a public and private key and stores it using the Android Key Store, so that only
     * this application will be able to access the keys.
     */
    public PublicKey createKeys(Context context) throws NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // BEGIN_INCLUDE(create_valid_dates)
        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 30);
        //END_INCLUDE(create_valid_dates)

        // BEGIN_INCLUDE(create_keypair)
        // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
        // and the KeyStore.  This example uses the AndroidKeyStore.
        KeyPairGenerator kpGenerator = KeyPairGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        // END_INCLUDE(create_keypair)

        // BEGIN_INCLUDE(create_spec)
        // The KeyPairGeneratorSpec object is how parameters for your key pair are passed
        // to the KeyPairGenerator.
        AlgorithmParameterSpec spec = new KeyGenParameterSpec
                .Builder(mAlias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setCertificateSubject(new X500Principal("CN=" + mAlias))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setCertificateSerialNumber(BigInteger.valueOf(1337))
                .setCertificateNotBefore(start.getTime())
                .setCertificateNotAfter(end.getTime())
                .build();

        kpGenerator.initialize(spec);

        KeyPair kp = kpGenerator.generateKeyPair();
        // END_INCLUDE(create_spec)

        return kp.getPublic();
    }

    /**
     * @return Check if the key pair exists in the alias
     */
    public boolean isKeyPairExist() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);
        return ks.isKeyEntry(mAlias);
    }

    /**
     * String to X509Certificate Converter
     *
     * @param certificateString base64 formatted string
     * @return X509Certificate object
     */
    private static X509Certificate convertToX509Cert(String certificateString) throws CertificateException, NoSuchProviderException {
        X509Certificate certificate = null;
        CertificateFactory cf;
        if (certificateString != null && !certificateString.trim().isEmpty()) {
            certificateString = certificateString.replace("-----BEGIN CERTIFICATE-----", "")
                    .replace("-----END CERTIFICATE-----", ""); // NEED FOR PEM FORMAT CERT STRING
            byte[] certificateData = Base64.decode(certificateString, Base64.NO_WRAP);
            cf = CertificateFactory.getInstance("X509", "SC");
            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificateData));
        }
        return certificate;
    }

    public void updateEntry(String pem, SecurityConstants.Alias alias) throws CertificateException, NoSuchProviderException, KeyStoreException, IOException, NoSuchAlgorithmException {
        X509Certificate certificate = convertToX509Cert(pem);
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
        // to call "load", or it'll crash.
        ks.load(null);

        ks.setCertificateEntry(alias.name(), certificate);
    }

    public String getCert(SecurityConstants.Alias alias) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
        // to call "load", or it'll crash.
        ks.load(null);

        X509Certificate certificate = (X509Certificate) ks.getCertificate(alias.name());

        if (certificate == null) {
            return null;
        }
        return new String(Base64.encode(certificate.getEncoded(), Base64.NO_WRAP));
    }

    /**
     * @return Get Public key with head and footer tags.
     */
    public String getPublicKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        // BEGIN_INCLUDE(sign_load_keystore)
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
        // to call "load", or it'll crash.
        ks.load(null);

        // Load the public from the Android Key Store
        ks.getCertificate(mAlias).getPublicKey();

        String pubKeyWithTag = "-----BEGIN PUBLIC KEY-----\n";
        pubKeyWithTag += Base64.encodeToString(ks.getCertificate(mAlias).getPublicKey().getEncoded(), Base64.NO_WRAP);
        pubKeyWithTag += "\n-----END PUBLIC KEY-----\n";

        return pubKeyWithTag;
    }

    /**
     * Signs the data using the key pair stored in the Android Key Store.  This signature can be
     * used with the data later to verify it was signed by this application.
     *
     * @return A string encoding of the data signature generated
     */
    public String signData(String inputStr) throws KeyStoreException,
            UnrecoverableEntryException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, IOException, CertificateException {
        byte[] data = inputStr.getBytes();

        // BEGIN_INCLUDE(sign_load_keystore)
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
        // to call "load", or it'll crash.
        ks.load(null);

        // Load the key pair from the Android Key Store
        KeyStore.Entry entry = ks.getEntry(mAlias, null);

        /* If the entry is null, keys were never stored under this alias.
         * Debug steps in this situation would be:
         * -Check the list of aliases by iterating over Keystore.aliases(), be sure the alias
         *   exists.
         * -If that's empty, verify they were both stored and pulled from the same keystore
         *   "AndroidKeyStore"
         */
        if (entry == null) {
            Log.w(TAG, "No key found under alias: " + mAlias);
            Log.w(TAG, "Exiting signData()...");
            return null;
        }

        /* If entry is not a KeyStore.PrivateKeyEntry, it might have gotten stored in a previous
         * iteration of your application that was using some other mechanism, or been overwritten
         * by something else using the same keystore with the same alias.
         * You can determine the type using entry.getClass() and debug from there.
         */
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.w(TAG, "Not an instance of a PrivateKeyEntry");
            Log.w(TAG, "Exiting signData()...");
            return null;
        }
        // END_INCLUDE(sign_data)

        // BEGIN_INCLUDE(sign_create_signature)
        // This class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using
        // the specified algorithm.
        Signature s = Signature.getInstance(SecurityConstants.SIGNATURE_SHA256withRSA);

        // Initialize Signature using specified private key
        s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());

        // Sign the data, store the result as a Base64 encoded String.
        s.update(data);
        byte[] signature = s.sign();
        // END_INCLUDE(sign_data)

        return Base64.encodeToString(signature, Base64.NO_WRAP);
    }

    public boolean verifyData(String input, String signatureStr, String certification) throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, KeyStoreException {
        X509Certificate certificate = convertToX509Cert(certification);

        byte[] data = input.getBytes();
        byte[] signature;
        // BEGIN_INCLUDE(decode_signature)

        // Make sure the signature string exists.  If not, bail out, nothing to do.

        if (signatureStr == null) {
            Log.w(TAG, "Invalid signature.");
            Log.w(TAG, "Exiting verifyData()...");
            return false;
        }

        try {
            // The signature is going to be examined as a byte array,
            // not as a base64 encoded string.
            signature = Base64.decode(signatureStr, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            // signatureStr wasn't null, but might not have been encoded properly.
            // It's not a valid Base64 string.
            return false;
        }
        // END_INCLUDE(decode_signature)

        // This class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using
        // the specified algorithm.
        Signature s = Signature.getInstance(SecurityConstants.SIGNATURE_SHA256withRSA);

        // BEGIN_INCLUDE(verify_data)
        // Verify the data.
        Log.d("DashboardViewModel", "with cert pub: " + certificate.getPublicKey());
        s.initVerify(certificate.getPublicKey());
        s.update(data);
        return s.verify(signature);
        // END_INCLUDE(verify_data)
    }

    /**
     * Given some data and a signature, uses the key pair stored in the Android Key Store to verify
     * that the data was signed by this application, using that key pair.
     *
     * @param input        The data to be verified.
     * @param signatureStr The signature provided for the data.
     * @return A boolean value telling you whether the signature is valid or not.
     */
    public boolean verifyData(String input, String signatureStr) throws KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableEntryException, InvalidKeyException, SignatureException {
        byte[] data = input.getBytes();
        byte[] signature;
        // BEGIN_INCLUDE(decode_signature)

        // Make sure the signature string exists.  If not, bail out, nothing to do.

        if (signatureStr == null) {
            Log.w(TAG, "Invalid signature.");
            Log.w(TAG, "Exiting verifyData()...");
            return false;
        }

        try {
            // The signature is going to be examined as a byte array,
            // not as a base64 encoded string.
            signature = Base64.decode(signatureStr, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            // signatureStr wasn't null, but might not have been encoded properly.
            // It's not a valid Base64 string.
            return false;
        }
        // END_INCLUDE(decode_signature)

        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        // Weird artifact of Java API.  If you don't have an InputStream to load, you still need
        // to call "load", or it'll crash.
        ks.load(null);

        // Load the key pair from the Android Key Store
        KeyStore.Entry entry = ks.getEntry(mAlias, null);

        if (entry == null) {
            Log.w(TAG, "No key found under alias: " + mAlias);
            Log.w(TAG, "Exiting verifyData()...");
            return false;
        }

        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.w(TAG, "Not an instance of a PrivateKeyEntry");
            return false;
        }

        // This class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using
        // the specified algorithm.
        Signature s = Signature.getInstance(SecurityConstants.SIGNATURE_SHA256withRSA);

        // BEGIN_INCLUDE(verify_data)
        // Verify the data.
        Log.d("DashboardViewModel", "without cert pub: " + new String(ks.getCertificate(mAlias).getPublicKey().getEncoded()));
        s.initVerify(ks.getCertificate(mAlias).getPublicKey());
        s.update(data);
        return s.verify(signature);
        // END_INCLUDE(verify_data)
    }

    public void setAlias(SecurityConstants.Alias alias) {
        mAlias = alias.name();
    }

    public String pubkeyToString(PublicKey pubKey) {
        ECPublicKey key = (ECPublicKey) pubKey;
        ECPoint pubPoint = key.getW();

        return pubPoint.getAffineX().toString(1) + pubPoint.getAffineY().toString(1);
    }

    /**
     * NOTE: This public key string is came from Cryptopp. So we need some post processing.
     * remove "h" at last
     * insert "0" at first
     *
     * @param string
     * @return
     */
    public PublicKey stringToPubkey(String string) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        if (string.charAt(0) != '0') {
            char[] chars = new char[string.length()];
            chars[0] = '0';
            string.getChars(0, string.length() - 1, chars, 1);
            string = new String(chars);
        }

        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_SECP256R1);
        ECCurve curve = ecParameterSpec.getCurve();

        org.spongycastle.math.ec.ECPoint point = curve.decodePoint(Hex.decode(string));
        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(point, ecParameterSpec);
        KeyFactory kf = KeyFactory.getInstance(TYPE_ECDH, "SC");

        return kf.generatePublic(publicKeySpec);
    }

    @TestOnly
    public PrivateKey stringToPrvKey(String string) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE_SECP256R1);

        byte[] prv = Hex.decode(string.getBytes());
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new BigInteger(1, prv), ecParameterSpec);
        KeyFactory kf = KeyFactory.getInstance(TYPE_ECDH, "SC");
        return kf.generatePrivate(privateKeySpec);
    }

    public KeyPair ecdhKeyGen() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());

        KeyPairGenerator kp = KeyPairGenerator.getInstance(TYPE_ECDH, "SC");
        kp.initialize(new ECGenParameterSpec(CURVE_SECP256R1), new SecureRandom());

        return kp.generateKeyPair();
    }

    public byte[] doEcdh(PrivateKey myPrvKey, PublicKey otherPubKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());

        KeyAgreement ka = KeyAgreement.getInstance(TYPE_ECDH, "SC");
        ka.init(myPrvKey);
        ka.doPhase(otherPubKey, true);
        return encodeSha256(ka.generateSecret());
    }

    private byte[] encodeSha256(byte[] sharedSecretKey) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(TYPE_SHA256);
        md.update(sharedSecretKey);
        return Hex.encode(md.digest());
    }

    public byte[] getMacSig(String key, byte[] data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), TYPE_HMAC);
            sha256Hmac.init(secretKey);

            return sha256Hmac.doFinal(data);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface SecurityConstants {
        String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";

        String PADDING_TYPE = "PKCS1Padding";
        String BLOCKING_MODE = "NONE";

        String TYPE_RSA = "RSA";
        String TYPE_ECDH = "ECDH";
        String TYPE_SHA256 = "SHA-256";
        String TYPE_HMAC = "HmacSHA256";

        String SIGNATURE_SHA256withRSA = "SHA256withRSA";
        String SIGNATURE_SHA512withRSA = "SHA512withRSA";

        String CURVE_SECP256R1 = "secp256r1";

        enum Alias {
            SELF_CERT,
            GRUUT_AUTH
        }
    }
}
