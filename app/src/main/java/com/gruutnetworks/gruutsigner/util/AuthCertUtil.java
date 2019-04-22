package com.gruutnetworks.gruutsigner.util;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.pkcs.Attribute;
import org.spongycastle.asn1.pkcs.CertificationRequest;
import org.spongycastle.asn1.pkcs.CertificationRequestInfo;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.sec.SECObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.X500NameBuilder;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.crypto.util.SubjectPublicKeyInfoFactory;
import org.spongycastle.util.encoders.Hex;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.gruutnetworks.gruutsigner.util.SecurityConstants.Alias.GRUUT_AUTH;
import static com.gruutnetworks.gruutsigner.util.SecurityConstants.*;

public class AuthCertUtil {

    private static final String TAG = "AuthCertUtil";

    // You can store multiple key pairs in the Key Store.  The string used to refer to the Key you
    // want to store, or later pull, is referred to as an "alias" in this case, because calling it
    // a key, when you use it to retrieve a key, would just be irritating.
    private String mAlias = SecurityConstants.Alias.SELF_CERT.name();
    private static AuthCertUtil authCertUtil;

    public static AuthCertUtil getInstance() {
        if (authCertUtil != null) {
            return authCertUtil;
        }
        authCertUtil = new AuthCertUtil();

        // Add Provider
        Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
        return authCertUtil;
    }

    /**
     * Generates a public and private key and stores it using the Android Key Store
     */
    public void generateKeyPair()
            throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // Create a start and end time, for the validity range of the key pair that's about to be generated.
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 30);

        KeyPairGenerator kpGenerator = KeyPairGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        AlgorithmParameterSpec spec = new KeyGenParameterSpec
                .Builder(mAlias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(new ECGenParameterSpec(CURVE_SECP256R1))
                .setCertificateSubject(new X500Principal("CN=" + mAlias))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setCertificateSerialNumber(BigInteger.valueOf(1337))
                .setCertificateNotBefore(start.getTime())
                .setCertificateNotAfter(end.getTime())
                .build();

        kpGenerator.initialize(spec);

        KeyPair kp = kpGenerator.generateKeyPair();
        kp.getPublic();
    }

    /**
     * Delete key pair from Android Key Store
     */
    public void deleteKeyPair() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);

        ks.deleteEntry(mAlias);
        ks.deleteEntry(GRUUT_AUTH.name());
    }

    /**
     * Generates CSR(Certificate Signing Request) with stored key
     *
     * @return CSR pem string
     */
    public String generateCsr()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);

        byte[] pubKey = ks.getCertificate(mAlias).getPublicKey().getEncoded();

        X500NameBuilder nameBuilder = new X500NameBuilder(X500Name.getDefaultStyle());
        nameBuilder.addRDN(BCStyle.CN, GRUUT_AUTH.name());

        AsymmetricKeyParameter pubKeyParam = PublicKeyFactory.createKey(pubKey);
        SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(pubKeyParam);
        CertificationRequestInfo requestInfo =
                new CertificationRequestInfo(nameBuilder.build(), publicKeyInfo, null);

        AlgorithmIdentifier signatureAi = new AlgorithmIdentifier(SECObjectIdentifiers.secp256r1);
        Attribute attribute = new Attribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, new DERSet());

        CertificationRequest certificationRequest
                = new CertificationRequest(requestInfo, signatureAi, new DERBitString(attribute));

        // CSR pem 형식으로 생성
        return bytesToPemString("CERTIFICATE REQUEST", certificationRequest.getEncoded());
    }

    /**
     * @return Check if the key pair exists in the alias
     */
    public boolean isKeyPairExist()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);
        return ks.isKeyEntry(mAlias);
    }

    /**
     * String to X509Certificate Converter
     *
     * @param certificateString base64 formatted string without tag
     * @return X509Certificate object
     */
    private X509Certificate stringToCertificate(String certificateString)
            throws CertificateException, NoSuchProviderException {
        X509Certificate certificate = null;
        CertificateFactory cf;
        if (certificateString != null && !certificateString.trim().isEmpty()) {

            PemObject pemObject = new PemObject("CERTIFICATE", certificateString.getBytes());
            cf = CertificateFactory.getInstance("X509", "BC");
            certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(pemObject.getContent()));
        }
        return certificate;
    }

    /**
     * Byte Array를 Pem String으로 변환
     *
     * @param pemType 출력할 Pem 형식
     * @param bytes   original data
     * @return PEM String
     */
    private String bytesToPemString(String pemType, byte[] bytes) throws IOException {
        PemObject pemObject = new PemObject(pemType, bytes);
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        stringWriter.close();

        return stringWriter.toString();
    }

    /**
     * Pem 형식으로 받은 인증서를 Android Keystore에 alias로 저장.
     *
     * @param certificateString certificate
     * @param alias             인증서를 저장 할 alias
     */
    public void storeCert(String certificateString, SecurityConstants.Alias alias)
            throws CertificateException, NoSuchProviderException, KeyStoreException, IOException, NoSuchAlgorithmException {
        X509Certificate certificate = stringToCertificate(certificateString);

        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);

        ks.setCertificateEntry(alias.name(), certificate);
    }

    /**
     * Android Keystore에서 해당 alias에 있는 인증서 가져오기
     *
     * @param alias 조회할 alias
     * @return certificate Base64 format
     */
    public String getCert(SecurityConstants.Alias alias)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);

        X509Certificate certificate = (X509Certificate) ks.getCertificate(alias.name());

        if (certificate == null) {
            // 저장 된 certificate 없을 경우 null 반환
            return null;
        }

        return bytesToPemString("CERTIFICATE", certificate.getEncoded());
    }

    /**
     * Generate a signature for MSG_RESPONSE1
     *
     * @param mNonce merger's nonce
     * @param sNonce signer's nonce
     * @param x      signer's dh x hex value
     * @param y      singer's dh y hex value
     * @param time   timestamp
     * @return signature string
     */
    public String signMsgResponse1(String mNonce, String sNonce, String x, String y, String time)
            throws IOException, CertificateException, InvalidKeyException, NoSuchAlgorithmException,
            KeyStoreException, SignatureException, UnrecoverableEntryException {

        byte[] sigTime = ByteBuffer.allocate(8).putLong(Integer.parseInt(time)).array();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Base64.decode(mNonce, Base64.NO_WRAP));  // 256 bit
        outputStream.write(Base64.decode(sNonce, Base64.NO_WRAP));  // 256 bit
        outputStream.write(Hex.decode(x));                          // 256 bit
        outputStream.write(Hex.decode(y));                          // 256 bit
        outputStream.write(sigTime);                                // 64 bit

        String signature = sign(outputStream.toByteArray());
        outputStream.close();

        return signature;
    }

    /**
     * Verifying the signature of MSG_RESPONSE2 sent from Merger.
     *
     * @param signature 검증 할 signature
     * @param cert      검증에 쓸 merger의 certificate
     * @param mNonce    merger's nonce
     * @param sNonce    singer's nonce
     * @param x         merger's dh x hex value
     * @param y         merger's dh y hex value
     * @param time      전송 받은 timestamp
     * @return A boolean value telling you whether the signature is valid or not.
     */
    public boolean verifyMsgResponse2(String signature, String cert, String mNonce, String sNonce,
                                      String x, String y, String time)
            throws IOException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException,
            InvalidKeyException, SignatureException {

        byte[] sigTime = ByteBuffer.allocate(8).putLong(Integer.parseInt(time)).array();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Base64.decode(mNonce, Base64.NO_WRAP));  // 256 bit
        outputStream.write(Base64.decode(sNonce, Base64.NO_WRAP));  // 256 bit
        outputStream.write(Hex.decode(x));                          // 256 bit
        outputStream.write(Hex.decode(y));                          // 256 bit
        outputStream.write(sigTime);                                // 64 bit

        byte[] mergerSig = outputStream.toByteArray();
        outputStream.close();

        return authCertUtil.verifySender(mergerSig, signature, cert);
    }

    /**
     * MSG_REQ_SSIG에 대한 지지서명 생성
     *
     * @param bId       요청한 Block의 id (Base58)
     * @param txRoot    Merkle root of transactions   (Base64)
     * @param usRoot    user scope root       (Base64)
     * @param csRoot    contract scope root   (Base64)
     * @return support signature string
     */
    public String generateSupportSignature(String bId, String txRoot, String usRoot, String csRoot)
            throws IOException, CertificateException, InvalidKeyException, NoSuchAlgorithmException,
            KeyStoreException, SignatureException, UnrecoverableEntryException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(Base58.decode(bId));                     // 256 bits
        outputStream.write(Base64.decode(txRoot, Base64.NO_WRAP));     // 256 bits
        outputStream.write(Base64.decode(usRoot, Base64.NO_WRAP));     // 256 bits
        outputStream.write(Base64.decode(csRoot, Base64.NO_WRAP));      // 256 bits

        String signature = authCertUtil.sign(outputStream.toByteArray());
        outputStream.close();

        return signature;
    }

    /**
     * Signs the data using the key pair stored in the Android Key Store.
     * This signature can be used with the data later to verify it was signed by this application.
     *
     * @return A string encoding of the data signature generated
     */
    private String sign(byte[] data)
            throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException,
            InvalidKeyException, SignatureException, IOException, CertificateException {
        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);

        // Load the key pair from the Android Key Store
        KeyStore.Entry entry = ks.getEntry(mAlias, null);

        // If the entry is null, keys were never stored under this alias.
        if (entry == null) {
            Log.w(TAG, "No key found under alias: " + mAlias);
            Log.w(TAG, "Exiting sign()...");
            return null;
        }

        /* If entry is not a KeyStore.PrivateKeyEntry, it might have gotten stored in a previous
         * iteration of your application that was using some other mechanism, or been overwritten
         * by something else using the same keystore with the same alias.
         * You can determine the type using entry.getClass() and debug from there.
         */
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.w(TAG, "Not an instance of a PrivateKeyEntry");
            Log.w(TAG, "Exiting sign()...");
            return null;
        }

        // This class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using
        // the specified algorithm.
        Signature s = Signature.getInstance(SHA256withECDSA);

        // Initialize Signature using specified private key
        s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());

        // Sign the data, store the result as a Base64 encoded String.
        s.update(data);
        byte[] signature = s.sign();

        return Base64.encodeToString(signature, Base64.NO_WRAP);
    }

    /**
     * Given some data and a signature, uses the X509Certification stored in the Android Key Store to verify
     * that the data was signed by this certification, using that public key.
     *
     * @param input         The data to be verified.
     * @param signatureStr  The signature provided for the data.
     * @param certification The certification stored in the Android Keystore
     * @return A boolean value telling you whether the signature is valid or not.
     */
    private boolean verifySender(byte[] input, String signatureStr, String certification)
            throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        X509Certificate certificate = stringToCertificate(certification);

        byte[] signature;

        // Make sure the signature string exists.  If not, bail out, nothing to do.
        if (signatureStr == null) {
            Log.w(TAG, "Invalid signature.");
            Log.w(TAG, "Exiting verifySender()...");
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

        // This class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using
        // the specified algorithm.
        Signature s = Signature.getInstance(SHA256withECDSA);

        // Verify the data.
        s.initVerify(certificate.getPublicKey());
        s.update(input);
        return s.verify(signature);
    }

    /**
     * Given some data and a signature, uses the key pair stored in the Android Key Store to verify
     * that the data was signed by this application, using that key pair.
     *
     * @param input        The data to be verified.
     * @param signatureStr The signature provided for the data.
     * @return A boolean value telling you whether the signature is valid or not.
     */
    private boolean verifySelf(String input, String signatureStr)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableEntryException, InvalidKeyException, SignatureException {
        byte[] data = input.getBytes();
        byte[] signature;

        // Make sure the signature string exists.  If not, bail out, nothing to do.
        if (signatureStr == null) {
            Log.w(TAG, "Invalid signature.");
            Log.w(TAG, "Exiting verifySender()...");
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

        KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
        ks.load(null);

        // Load the key pair from the Android Key Store
        KeyStore.Entry entry = ks.getEntry(mAlias, null);

        if (entry == null) {
            Log.w(TAG, "No key found under alias: " + mAlias);
            Log.w(TAG, "Exiting verifySender()...");
            return false;
        }

        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            Log.w(TAG, "Not an instance of a PrivateKeyEntry");
            return false;
        }

        // This class doesn't actually represent the signature,
        // just the engine for creating/verifying signatures, using
        // the specified algorithm.
        Signature s = Signature.getInstance(SHA256withECDSA);

        // Verify the data.
        s.initVerify(ks.getCertificate(mAlias).getPublicKey());
        s.update(data);
        return s.verify(signature);
    }
}
