package org.imtp.common.utils;

import java.nio.charset.StandardCharsets;

/**
 * @Description
 * @Author ys
 * @Date 2023/4/4 10:50
 */
public class CRC16Utils {

    private static final int POLY = 0x1021; // CRC-16-CCITT 多项式
    private static final int INITIAL_VALUE = 0xFFFF;

    public static short calculateCRC(byte[] data) {
        int crc = INITIAL_VALUE;
        for (byte b : data) {
            crc ^= (b << 8) & 0xFFFF;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLY;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }
        return (short) crc;
    }

}
