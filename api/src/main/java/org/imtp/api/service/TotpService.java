package org.imtp.api.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.utils.QrCodeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class TotpService {

    @Resource
    private GoogleAuthenticator googleAuthenticator;

    @Resource
    private GoogleAuthenticatorConfig googleAuthenticatorConfig;

    @Value("${imtp.2fa.issuer:IMTP}")
    private String issuer = "IMTP";

    public String createSecret() {
        GoogleAuthenticatorKey credentials = googleAuthenticator.createCredentials();
        return credentials.getKey();
    }

    public String generateCode(String secret){
        int code = googleAuthenticator.getTotpPassword(secret);
        return String.format("%06d", code);
    }

    public boolean verifyCode(String secret, String code){
        int totp = Integer.parseInt(code);
        return googleAuthenticator.authorize(secret, totp);
    }

    public String buildOtpAuthUrl(String accountName, String secret) {

        return buildOtpAuthUrl(accountName, issuer, secret);
    }

    public String buildOtpAuthUrl(String accountName, String issuer, String secret) {
        GoogleAuthenticatorKey googleAuthenticatorKey = new GoogleAuthenticatorKey
                .Builder(secret)
                .setConfig(googleAuthenticatorConfig)
                .build();
        return  GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(issuer, accountName, googleAuthenticatorKey);
    }

    public StreamingResponseBody streamQrCode(String accountName, String secret){

        return streamQrCode(accountName, issuer, secret);
    }

    public StreamingResponseBody streamQrCode(String accountName,String issuer, String secret){
        String otpAuthUrl = buildOtpAuthUrl(accountName,issuer, secret);
        return streamQrCode(otpAuthUrl);
    }

    public StreamingResponseBody streamQrCode(String otpAuthUrl){

        return outputStream -> {
            try {
                BufferedImage qrCodeImage = QrCodeUtils.createQrCodeImage(otpAuthUrl, 300, 300, null);
                ImageIO.write(qrCodeImage, "PNG", outputStream);
                outputStream.flush();
            } catch (IOException e) {
                log.error("Failed to write QR Code streaming output: ", e);
                throw new BusinessException("Failed to write QR Code streaming output" +  e.getMessage());
            }
        };
    }

    public byte[] bytesQrcode(String accountName, String secret){

        return bytesQrcode(accountName, issuer, secret);
    }

    public byte[] bytesQrcode(String accountName,String issuer, String secret){
        String otpAuthUrl = buildOtpAuthUrl(accountName,issuer, secret);
        return bytesQrcode(otpAuthUrl);
    }

    public byte[] bytesQrcode(String otpAuthUrl){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
            BufferedImage qrCodeImage = QrCodeUtils.createQrCodeImage(otpAuthUrl, 300, 300, null);
            ImageIO.write(qrCodeImage, "PNG", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to write QR Code streaming output: ", e);
            throw new BusinessException("Failed to write QR Code bytes output" +  e.getMessage());
        }
    }

    public BufferedImage imageQrcode(String accountName,String issuer, String secret){
        String otpAuthUrl = buildOtpAuthUrl(accountName,issuer, secret);
        return imageQrcode(otpAuthUrl);
    }

    public BufferedImage imageQrcode(String accountName, String secret){
        String otpAuthUrl = buildOtpAuthUrl(accountName,issuer, secret);
        return imageQrcode(otpAuthUrl);
    }

    public BufferedImage imageQrcode(String otpAuthUrl){

        return QrCodeUtils.createQrCodeImage(otpAuthUrl, 300, 300, null);
    }

}
