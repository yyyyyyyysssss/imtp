package org.imtp.api.controller;

import org.imtp.api.config.exception.BusinessException;
import org.imtp.api.domain.entity.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/19 10:15
 */
public class BaseController {

    private static final String DEFAULT_IMAGE_FORMAT_NAME = "PNG";

    protected User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("用户未登录或身份未验证");
        }
        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }
        throw new AccessDeniedException("当前用户信息获取失败");
    }

    protected <T> T getCurrentUser(Function<User, T> function) {
        User user = getCurrentUser();
        return function.apply(user);
    }

    protected void writeImage(OutputStream outputStream, BufferedImage bufferedImage){
        writeImage(outputStream, bufferedImage, DEFAULT_IMAGE_FORMAT_NAME);
    }

    protected void writeImage(OutputStream outputStream, BufferedImage bufferedImage, String formatName){
        try {
            ImageIO.write(bufferedImage, formatName, outputStream);
        } catch (IOException e) {
            throw new BusinessException("图片写出失败:" + e.getMessage());
        }
    }

    protected void write(OutputStream outputStream, InputStream inputStream){
        try (InputStream in = inputStream; OutputStream out = outputStream) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new BusinessException("流写出失败:" + e.getMessage());
        }
    }

}
