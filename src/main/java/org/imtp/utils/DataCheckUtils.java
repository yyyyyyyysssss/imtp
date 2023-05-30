package org.imtp.utils;

/**
 * @Description 用于数据校验
 * @Author ys
 * @Date 2023/4/3 15:20
 */
public class DataCheckUtils {

    public static void getChecksum(byte[] bytes) {
        int sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            sum += bytes[i];
        }
        sum = sum % 256;
        sum = ~sum;
    }

}
