package org.imtp.api.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @Description
 * @Author ys
 * @Date 2025/11/13 17:25
 */
public class CaptchaUtils {

    private static final String CAPTCHA_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // 验证码图像的宽度
    private static final int WIDTH = 120;
    // 验证码图像的高度
    private static final int HEIGHT = 40;
    // 验证码的字符长度
    private static final int CAPTCHA_LENGTH = 4;

    /**
     * 生成图形验证码
     *
     * @return 图形验证码的图片（BufferedImage）
     */
    public static BufferedImage generateCaptchaImage() {
        String captchaText = generateCaptchaText();
        return generateCaptchaImage(captchaText);
    }

    public static BufferedImage generateCaptchaImage(String captchaText) {
        // 创建画布，设置宽度和高度
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(getRandomColor(200, 250));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体样式
        g.setFont(new Font("Arial", Font.PLAIN, 30));

        // 添加干扰线
        addRandomNoise(g);

        // 绘制验证码字符
        g.setColor(getRandomColor(50, 150));
        g.drawString(captchaText, 20, 30);

        // 释放资源
        g.dispose();
        return image;
    }

    /**
     * 生成随机验证码字符
     *
     * @return 随机生成的验证码字符串
     */
    public static String generateCaptchaText() {
        Random rand = new Random();
        StringBuilder captchaText = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captchaText.append(CAPTCHA_CHARSET.charAt(rand.nextInt(CAPTCHA_CHARSET.length())));
        }
        return captchaText.toString();
    }

    /**
     * 获取随机颜色
     *
     * @param minColor 最小颜色值
     * @param maxColor 最大颜色值
     * @return 随机颜色
     */
    private static Color getRandomColor(int minColor, int maxColor) {
        Random rand = new Random();
        int r = minColor + rand.nextInt(maxColor - minColor);
        int g = minColor + rand.nextInt(maxColor - minColor);
        int b = minColor + rand.nextInt(maxColor - minColor);
        return new Color(r, g, b);
    }

    /**
     * 添加干扰线
     *
     * @param g Graphics2D 对象
     */
    private static void addRandomNoise(Graphics2D g) {
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            g.setColor(getRandomColor(100, 255));
            g.drawLine(rand.nextInt(WIDTH), rand.nextInt(HEIGHT),
                    rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        }
    }

}
