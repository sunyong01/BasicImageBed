package web.sy.base.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtils {
    public static String dateTimeToHumanDate(Date date) {
        // 将Date转换为Instant
        Instant instant = date.toInstant();
        // 获取当前时间的Instant
        Instant now = Instant.now();
        // 计算时间差
        long diff = now.toEpochMilli() - instant.toEpochMilli();

        // 计算时间差的秒数
        long seconds = diff / 1000;
        // 计算时间差的分钟数
        long minutes = seconds / 60;
        // 计算时间差的小时数
        long hours = minutes / 60;
        // 计算时间差的天数
        long days = hours / 24;
        // 计算时间差的月份数
        long months = days / 30;
        // 计算时间差的年数
        long years = months / 12;

        // 根据时间差返回适当的人性化时间格式
        if (seconds < 60) {
            return seconds + "秒前";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (days < 30) {
            return days + "天前";
        } else if (months < 12) {
            return months + "个月前";
        } else if (years < 3) {
            return years + "年前";
        } else {
            // 格式化为标准日期时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return formatter.format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
        }
    }
}
