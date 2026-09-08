package cn.sdpjw.export.utils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: liuyuebai
 * @date: 2026/7/20 17:02
 * @description:
 */
public class DateUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
