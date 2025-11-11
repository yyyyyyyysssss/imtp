package org.imtp.api.utils;

import groovy.lang.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class RSAUtilTest {

    private static final String TARGET_DATA = "{\"id\":123,\"name\":\"xxx\"}";

    @Test
    void getKeyPair() throws Exception {
        RSAUtils.KeyPairValue keyPairValue = RSAUtils.generateKeyPair();
        String publicKey = keyPairValue.getPublicKey();
        String privateKey = keyPairValue.getPrivateKey();
        log.info("publicKey: {}",publicKey);
        log.info("privateKey: {}",privateKey);
        String encrypt = RSAUtils.encrypt(TARGET_DATA, publicKey, RSAUtils.PaddingMode.OAEP_SHA1);
        String decrypt = RSAUtils.decrypt(encrypt, privateKey, RSAUtils.PaddingMode.OAEP_SHA1);
        assertEquals(decrypt,TARGET_DATA);
    }

    @Test
    void encrypt() throws Exception {
        String encrypt = RSAUtils.encrypt(TARGET_DATA);
        log.info("encrypt : {}",encrypt);
        assertNotNull(encrypt);
    }

    @Test
    void decrypt() throws Exception {
        int keySize = 1024;
        RSAUtils.KeyPairValue keyPairValue = RSAUtils.generateKeyPair(keySize);
        String publicKey = keyPairValue.getPublicKey();
        String privateKey = keyPairValue.getPrivateKey();
        String encrypt = RSAUtils.encrypt(TARGET_DATA,publicKey,RSAUtils.PaddingMode.OAEP_MD5,keySize);
        String decrypt = RSAUtils.decrypt(encrypt,privateKey,RSAUtils.PaddingMode.OAEP_MD5,keySize);
        log.info("decrypt : {}",decrypt);
        assertNotNull(decrypt);
    }

    @Test
    void sign() throws Exception {
        String sign = RSAUtils.sign(TARGET_DATA);
        log.info("sign : {}",sign);
        assertNotNull(sign);
    }

    @Test
    void verify() throws Exception {
        String sign = RSAUtils.sign(TARGET_DATA);
        boolean verify = RSAUtils.verify(TARGET_DATA, sign);
        log.info("verify : {}",verify);
        assertTrue(verify);
    }
}