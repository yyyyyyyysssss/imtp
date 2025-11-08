package org.imtp.api.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class AESUtilsTest {

    private static final String TARGET_DATA = "{\"id\":123,\"name\":\"xxx\"}";

    @Test
    void getKeyPair() throws Exception {
        String key = AESUtils.generateRandomKey();
        log.info("aes key: {}",key);
    }

    @Test
    void encrypt() throws Exception {
        String key = AESUtils.generateRandomKey();
        String encrypt = AESUtils.encrypt(TARGET_DATA, key);
        log.info("encrypt : {}",encrypt);
    }

    @Test
    void decrypt() throws Exception {
        String key = AESUtils.generateRandomKey();
        String encrypt = AESUtils.encrypt(TARGET_DATA, key);
        String decrypt = AESUtils.decrypt(encrypt, key);
        log.info("decrypt : {}",decrypt);
    }

}
