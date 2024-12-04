package org.imtp.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class EncryptUtil {

    public static String sha256(String... data) {
        String waitEncryptStr = getWaitEncryptStr(data);
        MessageDigest messageDigest;
        String shaStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(waitEncryptStr.getBytes(StandardCharsets.UTF_8));
            shaStr = Hex.encodeHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("getSHA256Str error: ", e);
        }
        return shaStr;
    }

    public static String base64Encode(String... data) {
        String waitEncryptStr = getWaitEncryptStr(data);
        byte[] encode = Base64.getEncoder().encode(waitEncryptStr.getBytes(StandardCharsets.UTF_8));
        return new String(encode,StandardCharsets.UTF_8);
    }

    public static String base64Decode(String data) {
        byte[] decode = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
        return new String(decode,StandardCharsets.UTF_8);
    }

    private static String getWaitEncryptStr(String... data){
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder waitEncrypt = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (i != data.length - 1) {
                waitEncrypt.append(data[i]).append(":");
            } else {
                waitEncrypt.append(data[i]);
            }
        }
        return waitEncrypt.toString();
    }

}
