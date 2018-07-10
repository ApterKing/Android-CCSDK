package cc.sdkutil.controller.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 14-12-26.
 * 工具类，判断程序是否在后台运行、软件版本号、获取imei等. <br>
 */
@CCDebug
public class CCToolUtil {

    /**
     * 获取到软件的版本号
     * @param context
     * @return
     */
    public static String getSoftVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            CCLogUtil.d(CCToolUtil.class.getAnnotation(CCDebug.class).debug(),
                    CCToolUtil.class, "getSoftVersion", e.getMessage());
            return "1.0.0";
        }
    }

    /**
     * 获取设备号
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }

    /**
     * 格式化时间
     * @param date
     * @param formatter
     * @return
     */
    public static String formatDate(Date date, String formatter) {
        SimpleDateFormat format = new SimpleDateFormat(formatter, Locale.CHINA);
        return format.format(date);
    }

    /**
     * 解析时间
     * @param time
     * @param formatter
     * @return
     */
    public static Date parseDate(String time, String formatter) {
        SimpleDateFormat format = new SimpleDateFormat(formatter, Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(time);
            return date;
        } catch (ParseException e) {
            CCLogUtil.d(CCToolUtil.class.getAnnotation(CCDebug.class).debug(),
                    CCToolUtil.class, e.getMessage() + " ");
            return null;
        }
    }

    public static String castFileString(String dir, String file) {
        return dir + (dir.endsWith(File.separator) ? "" : File.separator) + file;
    }

    /**
     * 计算两点之间的距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
                + Math.abs(y1 - y2) * Math.abs(y1 - y2));
    }

    public static double pointTotoDegrees(double x, double y) {
        return Math.toDegrees(Math.atan2(x, y));
    }

    public static boolean checkInRound(float sx, float sy, float r, float x,
                                       float y) {
        return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
    }

    /**
     * 将byte[] 转换为 16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
