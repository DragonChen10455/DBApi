package db;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    /**
     * 将标准时间转为Unix秒
     * @param date String格式时间
     * @return long
     * @author none
     * @date 2020-11-05 00:00
     */
    public static long dateStringToUTCSeconds(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (long) (format.parse(date).getTime() / 1000);
    }
    /**
     * 将标准时间转为Unix毫秒
     * @param date String格式时间
     * @return long
     * @author none
     * @date 2020-11-05 00:00
     */
    public static long dateStringToUTCMilliSeconds(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(date).getTime();
    }


    /**
     * 将Unix秒转为标准时间
     * @param utcSeconds long格式当前unix时间秒数
     * @return String
     * @author none
     * @date 2020-11-05 00:00
     */
    public static String uTCSecondsToDateString(long utcSeconds) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(utcSeconds * 1000));
    }

   /**
     * 将Unix秒转为标准时间
     * @param utcMilliSeconds long格式当前unix时间毫秒数
     * @return String
     * @author none
     * @date 2020-11-05 00:00
     */
    public static String uTCMilliSecondsToDateString(long utcMilliSeconds) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(utcMilliSeconds));
    }

   /**
     * 将Unix秒转为标准时间
     * @param utcMilliSeconds long格式当前unix时间毫秒数
     * @return String
     * @author none
     * @date 2020-11-05 00:00
     */
    public static String uTCMilliSecondsToDateStringWithMs(long utcMilliSeconds) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        return format.format(new Date(utcMilliSeconds));
    }

    public static String uTCMSToDateString(String utcSeconds) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long second= Long.parseLong(utcSeconds.substring(0,10));
        return (format.format(new Date(second*1000)))+":"+utcSeconds.substring(10);
    }

    /**
     * 将Unix秒转为标准时间
     * @param utcSeconds long格式当前unix时间秒数
     * @return String
     * @author none
     * @date 2020-11-05 00:00
     */
    public static String uTCSecondsToDateStringYear(long utcSeconds) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(new Date(utcSeconds * 1000));
    }

    /**
     * 将Unix秒转为标准时间
     * @param utcSeconds long格式当前unix时间秒数
     * @return String
     * @author none
     * @date 2020-11-05 00:00
     */
    public static String uTCSecondsToDateStringMonth(long utcSeconds) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("MM");
        return format.format(new Date(utcSeconds * 1000));
    }

    /**
     * 返回当前时间的Unix值，单位为毫秒
     * @return long
     * @author none
     * @date 2020-11-05 00:00
     */
    public static long currentUTCSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 返回当前时间的Unix值，单位为毫秒
     * @return long
     * @author none
     * @date 2020-11-05 00:00
     */
    public static long currentUTCMilliSeconds() {
        return System.currentTimeMillis();
    }


    /**
     * 返回human-readable的数据尺寸大小
     * @param size
     * @return String
     * @author none
     * @date 2020-11-05 00:00
     */
    public static String getNetFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

}
