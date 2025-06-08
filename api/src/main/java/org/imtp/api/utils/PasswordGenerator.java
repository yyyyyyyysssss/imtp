package org.imtp.api.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2025/6/6 20:04
 */
public class PasswordGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_+=<>?";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("密码长度不能小于4");
        }

        List<Character> passwordChars = new ArrayList<>();

        // 确保每种类型至少一个字符
        passwordChars.add(randomCharFrom(UPPER));
        passwordChars.add(randomCharFrom(LOWER));
        passwordChars.add(randomCharFrom(DIGITS));
        passwordChars.add(randomCharFrom(SPECIAL));

        // 剩余的用全部字符集填充
        for (int i = 4; i < length; i++) {
            passwordChars.add(randomCharFrom(ALL));
        }

        // 打乱顺序
        Collections.shuffle(passwordChars, RANDOM);

        // 构造字符串
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private static char randomCharFrom(String chars) {
        return chars.charAt(RANDOM.nextInt(chars.length()));
    }

}
