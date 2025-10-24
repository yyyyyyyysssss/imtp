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
        Tuple2<String, String> keyPair = RSAUtils.getKeyPair();
        String publicKey = keyPair.getV1();
        String privateKey = keyPair.getV2();
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
        String encrypt = RSAUtils.encrypt(TARGET_DATA);
        String decrypt = RSAUtils.decrypt(encrypt);
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