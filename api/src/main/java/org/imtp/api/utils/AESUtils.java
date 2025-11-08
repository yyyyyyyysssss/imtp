package org.imtp.api.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AESUtils {

    private static final String ALGORITHM = "AES";

    private static final int GCM_TAG_LENGTH = 128;

    private static final int IV_LENGTH = 12;

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static final int KEY_SIZE = 256;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        SecretKey secretKey = keyGen.generateKey();
        return EncryptUtils.base64Encode(secretKey.getEncoded());
    }

    public static String encrypt(String context, String base64Key) throws Exception {

        return encrypt(context,base64Key,Transformation.AES_CBC_PKCS5);
    }

    public static String encrypt(String context, String base64Key, Transformation mode) throws Exception {
        byte[] key = EncryptUtils.base64DecodeBytes(base64Key);

        Cipher cipher = Cipher.getInstance(mode.value());

        byte[] iv = new byte[mode == Transformation.AES_GCM_NO_PADDING ? IV_LENGTH : 16];
        secureRandom.nextBytes(iv);

        if (mode == Transformation.AES_GCM_NO_PADDING) {
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM), spec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ALGORITHM), new IvParameterSpec(iv));
        }

        byte[] encrypted = cipher.doFinal(context.getBytes(StandardCharsets.UTF_8));

        // 输出格式：Base64(IV + 密文)
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

        return EncryptUtils.base64Encode(result);
    }

    /**
     * AES 解密
     */

    public static String decrypt(String ciphertext, String base64Key) throws Exception {

        return decrypt(ciphertext,base64Key,Transformation.AES_CBC_PKCS5);
    }

    public static String decrypt(String ciphertext, String base64Key, Transformation mode) throws Exception {
        byte[] content = EncryptUtils.base64DecodeBytes(ciphertext);
        byte[] key = EncryptUtils.base64DecodeBytes(base64Key);

        int ivLength = mode == Transformation.AES_GCM_NO_PADDING ? IV_LENGTH : 16;
        byte[] iv = new byte[ivLength];
        byte[] encrypted = new byte[content.length - ivLength];

        System.arraycopy(content, 0, iv, 0, ivLength);
        System.arraycopy(content, ivLength, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(mode.value());

        if (mode == Transformation.AES_GCM_NO_PADDING) {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ALGORITHM), new IvParameterSpec(iv));
        }

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public enum Transformation {
        AES_GCM_NO_PADDING("AES/GCM/NoPadding"),
        AES_CBC_PKCS5("AES/CBC/PKCS5Padding");

        private final String value;

        Transformation(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

}
