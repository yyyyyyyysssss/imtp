package org.imtp.api.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @Description
 * @Author ys
 * @Date 2025/11/7 9:13
 */
public class AvatarGeneratorUtils {

    // 头像图片的大小
    private static final int IMAGE_SIZE = 200;
    // 圆角半径
    private static final int BORDER_RADIUS = 35;
    // 字体
    private static final Font FONT = new Font("Microsoft YaHei", Font.BOLD, IMAGE_SIZE / 2);
    // 背景颜色列表
    private static final Color[] BACKGROUND_COLORS = {
            new Color(0x2C3E50), // 深蓝色
            new Color(0x34495E), // 深灰色
            new Color(0x8E44AD), // 紫色
            new Color(0x27AE60), // 深绿色
            new Color(0x2980B9), // 蓝色
            new Color(0xF39C12), // 金色
            new Color(0xE74C3C), // 红色
            new Color(0x1ABC9C)  // 青绿色
    };

    public static BufferedImage generateAvatar(String name) {
        String initial = getInitial(name);
        // 创建头像
        return createAvatarImage(initial);
    }

    public static BufferedImage mergeAvatar(List<String> imageUrls, int imagesPerRow) throws IOException {
        BufferedImage[] images = new BufferedImage[imageUrls.size()];
        // 下载所有头像图片
        for (int i = 0; i < imageUrls.size(); i++) {
            images[i] = downloadImage(imageUrls.get(i));
        }
        // 计算合成图的列数和行数
        int totalImages = imageUrls.size();
        int rows = (int) Math.ceil((double) totalImages / imagesPerRow);
        int imageWidth = IMAGE_SIZE / imagesPerRow;  // 每个头像的宽度
        int imageHeight = IMAGE_SIZE / rows;        // 每个头像的高度

        // 创建合成头像
        BufferedImage mergedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = mergedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return null;
    }

    // 缩放图片到指定的宽高
    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        // 计算缩放比例
        double ratio = Math.min((double) width / originalImage.getWidth(), (double) height / originalImage.getHeight());
        int newWidth = (int) (originalImage.getWidth() * ratio);
        int newHeight = (int) (originalImage.getHeight() * ratio);

        // 缩放图片
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedScaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedScaledImage.createGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        return bufferedScaledImage;
    }

    private static BufferedImage downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    private static BufferedImage createAvatarImage(String initial) {
        // 随机选择一个背景颜色
        Random random = new Random();
        Color backgroundColor = BACKGROUND_COLORS[random.nextInt(BACKGROUND_COLORS.length)];
        // 创建一个新的图像，正方形头像
        BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        // 启用抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 填充背景色（圆角矩形）
        g.setColor(backgroundColor);
        g.fillRoundRect(0, 0, IMAGE_SIZE, IMAGE_SIZE, BORDER_RADIUS, BORDER_RADIUS); // 圆角矩形背景
        // 设置字体颜色和样式
        g.setColor(Color.WHITE);
        Font font = FONT;
        // 如果是中文且长度大于等于2，调整字体大小以适应两个字
        if (isChinese(initial) && initial.length() >= 2) {
            int fontSize = FONT.getSize() / 2;
            font = FONT.deriveFont((float) fontSize);
        }
        g.setFont(font);
        // 计算文本的绘制位置
        FontMetrics metrics = g.getFontMetrics(font);
        int textWidth = metrics.stringWidth(initial);
        int textHeight = metrics.getHeight();
        // 水平居中：计算 x 坐标
        int x = (IMAGE_SIZE - textWidth) / 2;

        // 垂直居中：计算 y 坐标，y坐标是基线的位置，文本的基线需要在中心位置
        int y = (IMAGE_SIZE - textHeight) / 2 + metrics.getAscent();
        // 绘制首字母
        g.drawString(initial, x, y);
        // 释放资源
        g.dispose();
        return image;
    }

    private static String getInitial(String name) {
        if (name == null || name.isEmpty()) {
            return "U";
        }
        // 如果是中文名字，取最后两个字
        if (isChinese(name)) {
            // 处理中文姓名，返回最后两个字
            int length = name.length();
            if (length > 1) {
                return name.substring(length - 2);  // 取最后两个字
            } else {
                return name;  // 如果只有一个字，返回该字
            }
        }
        // 如果是字母名字，取首字母
        return name.substring(0, 1).toUpperCase(); // 获取首字母并转为大写
    }

    private static boolean isChinese(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 判断字符是否是中文字符
            if (Character.toString(c).matches("[\\u4e00-\\u9fa5]+")) {
                return true;
            }
        }
        return false;
    }

}
