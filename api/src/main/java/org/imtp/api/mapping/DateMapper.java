package org.imtp.api.mapping;

import org.imtp.api.config.exception.BusinessException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description
 * @Author ys
 * @Date 2025/10/16 16:41
 */
public class DateMapper {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(DEFAULT_PATTERN);

    public String asString(Date date) {
        return date == null ? null : FORMATTER.format(date);
    }

    public Date asDate(String str) {
        if (str == null) return null;
        try {
            return FORMATTER.parse(str);
        } catch (Exception e) {
            throw new BusinessException("日期格式解析失败: " + str);
        }
    }

}
