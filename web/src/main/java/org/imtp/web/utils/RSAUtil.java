package org.imtp.web.utils;

import groovy.lang.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description RSA java默认的填充模式为 RSA/None/PKCS1Padding 最大加密大小需要减去11字节 OAEP需要减41个字节
 * @Author ys
 * @Date 2024/3/1 10:49
 */
@Slf4j
public class RSAUtil {

    private static final int KEY_SIZE = 2048;

    public static final String KEY_TYPE = "RSA";

    /**
     * 最大加密长度 OAEP填充 减41个字节  其它填充减11个字节  向下兼容(比如某个填充需要减11个字节 则减41个字节也不会有问题)
     */
    private static final int MAX_ENCRYPT_BLOCK = (KEY_SIZE >> 3) - 41;

    /**
     * RSA最大解密密文大小  KEY_SIZE/8  转为字节
     */
    private static final int MAX_DECRYPT_BLOCK = KEY_SIZE >> 3;
    /**
     * 编码
     */
    private static final String charset = "utf-8";

    private static final ConcurrentMap<String,String> CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 获取密钥对
     *
     * @return 密钥对
     */
    public static Tuple2<String, String> getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_TYPE);
        generator.initialize(KEY_SIZE);
        KeyPair keyPair = generator.generateKeyPair();
        String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
        String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
        return new Tuple2<>(publicKey, privateKey);
    }

    /**
     * 解码Base64获取私钥
     *
     * @param privateKey 私钥字符串
     * @return
     */
    public static PrivateKey loadPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes(charset));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PrivateKey loadLocalPrivateKey() throws Exception {
        String localPrivateKeyStr = loadLocalPrivateKeyStr();
        return loadPrivateKey(localPrivateKeyStr);
    }


    /**
     * 解码Base64获取公钥
     *
     * @param publicKey 公钥字符串
     * @return
     */
    public static PublicKey loadPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes(charset));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    public static PublicKey loadLocalPublicKey() throws Exception {
        String loadLocalPublicKeyStr = loadLocalPublicKeyStr();
        if (loadLocalPublicKeyStr == null){
            throw new IOException("本地公钥文件不存在");
        }
        return loadPublicKey(loadLocalPublicKeyStr);
    }


    //加密
    public static String encrypt(String context) throws Exception{
        PublicKey publicKey = loadLocalPublicKey();
        return encrypt(context,publicKey,PaddingMode.OAEP_SHA1);
    }

    public static String encrypt(String context, String publicKey,PaddingMode paddingMode) throws Exception {
        PublicKey pk = loadPublicKey(publicKey);
        return encrypt(context, pk,paddingMode);
    }

    public static String encrypt(String context, PublicKey publicKey,PaddingMode paddingMode) throws Exception {
        Cipher cipher = Cipher.getInstance(paddingMode.getCode());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = context.getBytes(StandardCharsets.UTF_8);
        byte[] crypt = crypt(bytes, cipher, MAX_ENCRYPT_BLOCK);
        return Base64.encodeBase64String(crypt);
    }


    //解密
    public static String decrypt(String context) throws Exception{
        PrivateKey privateKey = loadLocalPrivateKey();
        return decrypt(context,privateKey,PaddingMode.OAEP_SHA1);
    }

    public static String decrypt(String context, String privateKey,PaddingMode paddingMode) throws Exception {
        PrivateKey pk = loadPrivateKey(privateKey);
        return decrypt(context, pk,paddingMode);
    }

    public static String decrypt(String context, PrivateKey privateKey,PaddingMode paddingMode) throws Exception {
        Cipher cipher = Cipher.getInstance(paddingMode.getCode());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = Base64.decodeBase64(context);
        byte[] crypt = crypt(bytes, cipher, MAX_DECRYPT_BLOCK);
        return new String(crypt,StandardCharsets.UTF_8);
    }

    private static byte[] crypt(byte[] dataBytes,Cipher cipher,int maxBlock) throws Exception{
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offset > 0) {
                if (inputLen - offset > maxBlock) {
                    cache = cipher.doFinal(dataBytes, offset, maxBlock);
                } else {
                    cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * maxBlock;
            }
            return out.toByteArray();
        }finally {
            out.close();
        }
    }


    public static String sign(String context) throws Exception{
        PrivateKey privateKey = loadLocalPrivateKey();
        return sign(context,privateKey,SignAlgorithm.SHA256withRSA);
    }

    public static String sign(String context, String privateKey,SignAlgorithm signAlgorithm) throws Exception {
        PrivateKey pk = loadPrivateKey(privateKey);
        return sign(context, pk,signAlgorithm);
    }

    public static String sign(String context, PrivateKey privateKey,SignAlgorithm signAlgorithm) throws Exception {
        byte[] bytes = context.getBytes(charset);
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance(signAlgorithm.getCode());
        signature.initSign(key);
        signature.update(bytes);
        return new String(Base64.encodeBase64(signature.sign()), charset);
    }

    public static boolean verify(String context,String sign) throws Exception{
        PublicKey publicKey = loadLocalPublicKey();
        return verify(context,publicKey,sign,SignAlgorithm.SHA256withRSA);
    }

    public static boolean verify(String context, String publicKey, String sign,SignAlgorithm signAlgorithm) throws Exception {
        PublicKey pk = loadPublicKey(publicKey);
        return verify(context, pk, sign,signAlgorithm);
    }

    public static boolean verify(String context, PublicKey publicKey, String sign,SignAlgorithm signAlgorithm) throws Exception {
        byte[] srcBytes = context.getBytes(charset);
        byte[] signBytes = sign.getBytes(charset);
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(signAlgorithm.getCode());
        signature.initVerify(key);
        signature.update(srcBytes);
        return signature.verify(Base64.decodeBase64(signBytes));
    }

    public static String loadLocalPublicKeyStr(){
        String cacheKey ="public_key";
        if (CACHE_MAP.containsKey(cacheKey)){
            return CACHE_MAP.get(cacheKey);
        }
        ClassPathResource classPathResource = new ClassPathResource("rsa/public_key.pem");
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int n;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            String publicKeyStr = output.toString()
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "");
            CACHE_MAP.put(cacheKey,publicKeyStr);
            return publicKeyStr;
        } catch (IOException e) {
            log.error("loadLocalPublicKeyStr Error : ",e);
            throw new RuntimeException("load publicKey error :" + e.getMessage());
        }
    }

    public static String loadLocalPrivateKeyStr(){
        String cacheKey ="private_key";
        if (CACHE_MAP.containsKey(cacheKey)){
            return CACHE_MAP.get(cacheKey);
        }
        ClassPathResource classPathResource = new ClassPathResource("rsa/private_key.pem");
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int n;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            String privateKeyStr = output.toString()
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");
            CACHE_MAP.put(cacheKey,privateKeyStr);
            return privateKeyStr;
        } catch (IOException e) {
            log.error("loadLocalPrivateKeyStr Error : ",e);
            throw new RuntimeException("load privateKey error :" + e.getMessage());
        }
    }


    public static enum PaddingMode{
        OAEP_MD5("RSA/ECB/OAEPWithMD5AndMGF1Padding"),
        OAEP_SHA1("RSA/ECB/OAEPWithSHA1AndMGF1Padding"),
        PKCS1_PADDING("RSA/ECB/PKCS1Padding"),
        ;
        private String code;

        public String getCode() {
            return code;
        }

        PaddingMode(String code){
            this.code = code;
        }
    }


    public static enum SignAlgorithm{
        SHA1withRSA("SHA1withRSA"),
        SHA256withRSA("SHA256withRSA"),
        MD5withRSA("MD5withRSA")
        ;
        private String code;

        public String getCode() {
            return code;
        }

        SignAlgorithm(String code){
            this.code = code;
        }
    }


}
