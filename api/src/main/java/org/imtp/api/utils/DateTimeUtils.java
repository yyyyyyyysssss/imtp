package org.imtp.api.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

public class DateTimeUtils {

    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    private DateTimeUtils() {

    }

    // 获取当前时间
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    // 获取当前日期
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 使用默认格式（yyyy-MM-dd HH:mm:ss）格式化 Date
     */
    public static String format(Date date) {
        return format(date, DEFAULT_DATE_TIME_PATTERN);
    }

    /**
     * 使用自定义格式格式化 Date
     * @param date Date 对象
     * @param pattern 格式化 pattern，如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) return null;
        LocalDateTime dateTime = toLocalDateTime(date); // 将 Date 转 LocalDateTime
        return format(dateTime, pattern);
    }

    /** 使用默认格式格式化 LocalDateTime */
    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_DATE_TIME_PATTERN);
    }

    /**
     * 格式化 LocalDateTime 为字符串
     * @param dateTime LocalDateTime 对象
     * @param pattern 格式化 pattern，如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (Objects.isNull(dateTime)) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDate date) {
        return format(date, DEFAULT_DATE_PATTERN);
    }

    /** 格式化 LocalDate 为字符串 */
    public static String format(LocalDate date, String pattern) {
        if (Objects.isNull(date)) return null;
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 使用默认格式（yyyy-MM-dd HH:mm:ss）解析字符串为 Date
     */
    public static Date parseToDate(String dateTimeStr) {
        return parseToDate(dateTimeStr, DEFAULT_DATE_TIME_PATTERN);
    }

    /**
     * 使用自定义格式解析字符串为 Date
     * @param dateTimeStr 时间字符串
     * @param pattern 对应 pattern
     * @return Date 对象
     */
    public static Date parseToDate(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        LocalDateTime dateTime = parse(dateTimeStr, pattern); // 解析为 LocalDateTime
        return toDate(dateTime); // 转为 Date
    }

    /** 使用默认格式解析字符串为 LocalDateTime */
    public static LocalDateTime parse(String dateTimeStr) {
        return parse(dateTimeStr, DEFAULT_DATE_TIME_PATTERN);
    }

    /**
     * 将字符串解析为 LocalDateTime
     * @param dateTimeStr 时间字符串
     * @param pattern 对应 pattern
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parse(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_PATTERN);
    }

    /** 将字符串解析为 LocalDate */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    /** Date 转 LocalDateTime */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /** LocalDateTime 转 Date */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** Date 转 LocalDate（只保留年月日） */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /** LocalDate 转 Date，取当天结束时间 23:59:59 */
    public static Date toDateEndOfDay(LocalDate localDate) {
        if (localDate == null) return null;
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** LocalDate 转 Date，取当天结束时间 00:00:00 */
    public static Date toDateStartOfDay(LocalDate localDate) {
        if (localDate == null) return null;
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MIN);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** 增加秒数 */
    public static LocalDateTime plusSeconds(LocalDateTime dateTime, long seconds) {
        if (dateTime == null) return null;
        return dateTime.plusSeconds(seconds);
    }

    /** 增加分钟 */
    public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
        if (dateTime == null) return null;
        return dateTime.plusMinutes(minutes);
    }

    /** 增加小时 */
    public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) return null;
        return dateTime.plusHours(hours);
    }

    /** 增加天数 */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) return null;
        return dateTime.plusDays(days);
    }

    /** 减少秒数 */
    public static LocalDateTime minusSeconds(LocalDateTime dateTime, long seconds) {
        if (dateTime == null) return null;
        return dateTime.minusSeconds(seconds);
    }

    /** 减少分钟 */
    public static LocalDateTime minusMinutes(LocalDateTime dateTime, long minutes) {
        if (dateTime == null) return null;
        return dateTime.minusMinutes(minutes);
    }

    /** 减少小时 */
    public static LocalDateTime minusHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) return null;
        return dateTime.minusHours(hours);
    }

    /** 减少天数 */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) return null;
        return dateTime.minusDays(days);
    }

    public static LocalDate plusDays(LocalDate date, long days) {
        if (date == null) return null;
        return date.plusDays(days);
    }

    public static LocalDate plusMonths(LocalDate date, long months) {
        if (date == null) return null;
        return date.plusMonths(months);
    }

    public static LocalDate plusYears(LocalDate date, long years) {
        if (date == null) return null;
        return date.plusYears(years);
    }

    public static LocalDate minusDays(LocalDate date, long days) {
        if (date == null) return null;
        return date.minusDays(days);
    }

    public static LocalDate minusMonths(LocalDate date, long months) {
        if (date == null) return null;
        return date.minusMonths(months);
    }

    public static LocalDate minusYears(LocalDate date, long years) {
        if (date == null) return null;
        return date.minusYears(years);
    }

    /** 获取某天的开始时间 */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) return null;
        return date.atStartOfDay();
    }

    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime startOfDay(Date dateTime) {
        if (dateTime == null) return null;
        return toLocalDateTime(dateTime).toLocalDate().atStartOfDay();
    }

    /** 获取某天的结束时间 */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) return null;
        return date.atTime(LocalTime.MAX);
    }

    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    public static LocalDateTime endOfDay(Date dateTime) {
        if (dateTime == null) return null;
        return toLocalDateTime(dateTime).toLocalDate().atTime(LocalTime.MAX);
    }

    /** 获取某月的开始时间 */
    public static LocalDateTime startOfMonth(LocalDate date) {
        if (date == null) return null;
        return date.withDayOfMonth(1).atStartOfDay();
    }

    public static LocalDateTime startOfMonth(LocalDateTime date) {
        if (date == null) return null;
        return date.toLocalDate().withDayOfMonth(1).atStartOfDay();
    }

    public static LocalDateTime startOfMonth(Date date) {
        if (date == null) return null;
        return toLocalDateTime(date).toLocalDate().withDayOfMonth(1).atStartOfDay();
    }

    /** 获取某月的结束时间 */
    public static LocalDateTime endOfMonth(LocalDate date) {
        if (date == null) return null;
        return date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
    }

    public static LocalDateTime endOfMonth(LocalDateTime date) {
        if (date == null) return null;
        return date.toLocalDate().withDayOfMonth(date.toLocalDate().lengthOfMonth()).atTime(LocalTime.MAX);
    }

    public static LocalDateTime endOfMonth(Date date) {
        if (date == null) return null;
        LocalDateTime localDateTime = toLocalDateTime(date);
        return localDateTime.toLocalDate().withDayOfMonth(localDateTime.toLocalDate().lengthOfMonth()).atTime(LocalTime.MAX);
    }


    /** LocalDateTime 转时间戳（毫秒） */
    public static long toTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) return 0L;
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /** 时间戳（毫秒） 转 LocalDateTime */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * 计算两个 LocalDate 之间相差的天数
     * 例如：start = 2025-11-30, end = 2025-12-02 → 返回 2
     */
    public static long betweenDays(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0;
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个 LocalDateTime 之间相差的天数
     */
    public static long betweenDays(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个 LocalDateTime 之间相差的小时数
     */
    public static long betweenHours(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 计算两个 LocalDateTime 之间相差的分钟数
     */
    public static long betweenMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * 计算两个 LocalDateTime 之间相差的秒数
     */
    public static long betweenSeconds(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * 计算两个 Date 之间相差的天数
     */
    public static long betweenDays(Date start, Date end) {
        if (start == null || end == null) return 0;
        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();
        return ChronoUnit.DAYS.between(startInstant, endInstant);
    }

    /**
     * 计算两个 Date 之间相差的小时数
     */
    public static long betweenHours(Date start, Date end) {
        if (start == null || end == null) return 0;
        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();
        return ChronoUnit.HOURS.between(startInstant, endInstant);
    }

    /**
     * 计算两个 Date 之间相差的分钟数
     */
    public static long betweenMinutes(Date start, Date end) {
        if (start == null || end == null) return 0;
        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();
        return ChronoUnit.MINUTES.between(startInstant, endInstant);
    }

    /**
     * 计算两个 Date 之间相差的秒数
     */
    public static long betweenSeconds(Date start, Date end) {
        if (start == null || end == null) return 0;
        Instant startInstant = start.toInstant();
        Instant endInstant = end.toInstant();
        return ChronoUnit.SECONDS.between(startInstant, endInstant);
    }

    /**
     * 判断 LocalDate 是否在指定区间内（包含开始和结束）
     */
    public static boolean isBetween(LocalDate date, LocalDate startInclusive, LocalDate endInclusive) {
        if (date == null || startInclusive == null || endInclusive == null) return false;
        return !date.isBefore(startInclusive) && !date.isAfter(endInclusive);
    }

    /**
     * 判断 LocalDateTime 是否在指定区间内（包含开始和结束）
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime startInclusive, LocalDateTime endInclusive) {
        if (dateTime == null || startInclusive == null || endInclusive == null) return false;
        return !dateTime.isBefore(startInclusive) && !dateTime.isAfter(endInclusive);
    }

    /**
     * 判断 Date 是否在指定区间内（包含开始和结束）
     */
    public static boolean isBetween(Date date, Date startInclusive, Date endInclusive) {
        if (date == null || startInclusive == null || endInclusive == null) return false;
        return !date.before(startInclusive) && !date.after(endInclusive);
    }

    /** 判断 LocalDateTime 是否为今天 */
    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) return false;
        return LocalDate.now().equals(dateTime.toLocalDate());
    }

    /** 判断 LocalDate 是否为今天 */
    public static boolean isToday(LocalDate date) {
        if (date == null) return false;
        return LocalDate.now().equals(date);
    }

    /** 判断 Date 是否为今天 */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        LocalDate localDate = toLocalDate(date);
        return isToday(localDate);
    }

    /** 判断 LocalDateTime 是否为本月 */
    public static boolean isCurrentMonth(LocalDateTime dateTime) {
        if (dateTime == null) return false;
        LocalDate now = LocalDate.now();
        LocalDate target = dateTime.toLocalDate();
        return now.getYear() == target.getYear() && now.getMonth() == target.getMonth();
    }

    /** 判断 LocalDate 是否为本月 */
    public static boolean isCurrentMonth(LocalDate date) {
        if (date == null) return false;
        LocalDate now = LocalDate.now();
        return now.getYear() == date.getYear() && now.getMonth() == date.getMonth();
    }

    /** 判断 Date 是否为本月 */
    public static boolean isCurrentMonth(Date date) {
        if (date == null) return false;
        return isCurrentMonth(toLocalDate(date));
    }

    /** 判断 LocalDate 是否在另一个 LocalDate 之前 */
    public static boolean isBefore(LocalDate date, LocalDate other) {
        if (date == null || other == null) return false;
        return date.isBefore(other);
    }

    /** 判断 LocalDate 是否在另一个 LocalDate 之后 */
    public static boolean isAfter(LocalDate date, LocalDate other) {
        if (date == null || other == null) return false;
        return date.isAfter(other);
    }

    /** 判断 LocalDateTime 是否在另一个 LocalDateTime 之前 */
    public static boolean isBefore(LocalDateTime dateTime, LocalDateTime other) {
        if (dateTime == null || other == null) return false;
        return dateTime.isBefore(other);
    }

    /** 判断 LocalDateTime 是否在另一个 LocalDateTime 之后 */
    public static boolean isAfter(LocalDateTime dateTime, LocalDateTime other) {
        if (dateTime == null || other == null) return false;
        return dateTime.isAfter(other);
    }

    /** 判断 Date 是否在另一个 Date 之前 */
    public static boolean isBefore(Date date, Date other) {
        if (date == null || other == null) return false;
        return date.before(other);
    }

    /** 判断 Date 是否在另一个 Date 之后 */
    public static boolean isAfter(Date date, Date other) {
        if (date == null || other == null) return false;
        return date.after(other);
    }

}
