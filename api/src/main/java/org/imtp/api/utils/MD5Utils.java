package org.imtp.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description
 * @Author ys
 * @Date 2025/12/30 11:37
 */
public class MD5Utils {

    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

    // 获取字符串的 MD5 校验和
    public static String getMD5(String input){
        if (input == null) {
            return null;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = input.getBytes();
        byte[] digest = md.digest(bytes);
        return bytesToHex(digest);
    }

    // 获取文件的 MD5 校验和
    public static String getMD5(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            return bytesToHex(digest);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    // 获取字节数组的 MD5 校验和
    public static String getMD5(byte[] inputBytes) {
        if (inputBytes == null || inputBytes.length == 0) {
            return null;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = md.digest(inputBytes);
        return bytesToHex(digest);
    }

    public static String getMD5(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            return bytesToHex(digest);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

}
