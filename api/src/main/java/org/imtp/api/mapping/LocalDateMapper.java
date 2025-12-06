package org.imtp.api.mapping;


import org.imtp.api.config.exception.BusinessException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:41
 */
public class LocalDateMapper {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    public String asString(LocalDate date) {
        return date == null ? null : date.format(FORMATTER);
    }

    public LocalDate asDate(String str) {
        if (str == null) return null;
        try {
            return LocalDate.parse(str, FORMATTER);
        } catch (Exception e) {
            throw new BusinessException("日期格式解析失败: " + str);
        }
    }

}
