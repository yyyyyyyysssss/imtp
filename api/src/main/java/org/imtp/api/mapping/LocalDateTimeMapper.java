package org.imtp.api.mapping;

import org.imtp.api.config.exception.BusinessException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:41
 */
public class LocalDateTimeMapper {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    public String asString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(FORMATTER);
    }

    public LocalDateTime asDateTime(String str) {
        if (str == null) return null;
        try {
            return LocalDateTime.parse(str, FORMATTER);
        } catch (Exception e) {
            throw new BusinessException("日期格式解析失败: " + str);
        }
    }

}
