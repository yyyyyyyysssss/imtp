package org.imtp.web.utils;

import java.security.SecureRandom;
import java.util.Random;


public class VerificationCodeUtil {

    private static final char[] META_DATA = {
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y',
            'Z'
    };

    private static final Random RANDOM = new SecureRandom();

    public static String genVerificationCode(){
        return genVerificationCode(6);
    }

    public static String genVerificationCode(int digit){
        char[] chars = new char[digit];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = META_DATA[RANDOM.nextInt(META_DATA.length)];
        }
        return new String(chars);
    }

}
