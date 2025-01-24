package org.imtp.web.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2025/1/24 16:45
 */
@Slf4j
public class QrCodeUtil {

    private static final int QR_COLOR = 0xFF000000;

    private static final int QR_BG_COLOR = 0xFFFFFFFF;

    private static final int DEFAULT_WIDTH = 200;

    private static final int DEFAULT_HEIGHT = 200;

    private static final String DEFAULT_IMAGE_FORMAT_NAME = "PNG";

    private static final String LOGO_PATH = "asserts/icon.png";

    public static BufferedImage createQrCodeImage(String content) {

        return createQrCodeImage(content, DEFAULT_WIDTH, DEFAULT_HEIGHT,LOGO_PATH);
    }

    public static BufferedImage createQrCodeImage(String content, String logoPath) {
        return createQrCodeImage(content,DEFAULT_WIDTH,DEFAULT_HEIGHT,logoPath);
    }

    public static BufferedImage createQrCodeImage(String content, int w, int h,String logoPath) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        BufferedImage bufferedImage = null;
        try {
            Map<EncodeHintType, Object> hints = HashMap.newHashMap(1);
            //字符集
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //纠错等级
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
            //边框
            hints.put(EncodeHintType.MARGIN, 0);
            bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, w, h, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bufferedImage.setRGB(i, j, bitMatrix.get(i, j) ? QR_COLOR : QR_BG_COLOR);
                }
            }
            if(logoPath != null){
                ClassPathResource classPathResource = new ClassPathResource(logoPath);
                BufferedImage logo = ImageIO.read(classPathResource.getInputStream());
                return addLogo(bufferedImage, logo);
            }
        } catch (Exception exception) {
            log.error("createQrCodeImage error:", exception);
        }
        return bufferedImage;
    }

    private static BufferedImage addLogo(BufferedImage qrCodeImage, BufferedImage logoImage) {
        int qrWidth = qrCodeImage.getWidth();
        int qrHeight = qrCodeImage.getHeight();
        int logoMaxWidth = qrWidth / 5;
        int logoMaxHeight = qrHeight / 5;
        int logoWidth = Math.min(logoImage.getWidth(), logoMaxWidth);
        int logoHeight = Math.min(logoImage.getHeight(), logoMaxHeight);
        Image logo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);

        BufferedImage resultImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultImage.createGraphics();
        //绘制二维码
        g.drawImage(qrCodeImage, 0, 0, null);
        //绘制logo
        int x = (qrWidth - logoWidth) / 2;
        int y = (qrHeight - logoHeight) / 2;
        g.drawImage(logo, x, y, null);
        g.dispose();
        return resultImage;
    }

    public static void writeQrCodeImage(OutputStream outputStream, BufferedImage bufferedImage) {
        try {
            ImageIO.write(bufferedImage, DEFAULT_IMAGE_FORMAT_NAME, outputStream);
        } catch (IOException e) {
            log.error("writeQrCodeImage error:", e);
        }
    }

}
