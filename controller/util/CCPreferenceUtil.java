package cc.sdkutil.controller.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by wangcong on 14-12-26.
 * {@link SharedPreferences} 功能封装. <br>
 */
public class CCPreferenceUtil {

    /** ------------- string ----------------- */
    public static String getPrefString(Context context, String key,
                                       final String defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public static String getPrefString(Context context, String name, int mode,
                                String key, final String defaultValue) {
        if (name == null || (name != null && name.equals(""))) return getPrefString(context, key, defaultValue);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        return settings.getString(key, defaultValue);
    }

    public static void setPrefString(Context context, final String key,
                                     final String value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).commit();
    }

    public static void setPrefString(Context context, String name, int mode,
                                     final String key, final String value) {
        if (name == null || (name != null && name.equals(""))) setPrefString(context, key, value);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        settings.edit().putString(key, value).commit();
    }

    /** --------------- boolean ------------- */
    public static boolean getPrefBoolean(Context context, final String key,
                                         final boolean defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getBoolean(key, defaultValue);
    }

    public static boolean getPrefBoolean(Context context, String name, int mode,
                                       String key, final Boolean defaultValue) {
        if (name == null || (name != null && name.equals(""))) getPrefBoolean(context, key, defaultValue);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        return settings.getBoolean(key, defaultValue);
    }

    public static void setPrefBoolean(Context context, final String key,
                                      final boolean value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putBoolean(key, value).commit();
    }

    public static void setPrefBoolean(Context context, String name, int mode,
                                     final String key, final boolean value) {
        if (name == null || (name != null && name.equals(""))) setPrefBoolean(context, key, value);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        settings.edit().putBoolean(key, value).commit();
    }

    /** ----------------- int ----------------- */
    public static int getPrefInt(Context context, final String key,
                                 final int defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

    public static int getPrefInt(Context context, String name, int mode,
                                         String key, final int defaultValue) {
        if (name == null || (name != null && name.equals(""))) getPrefInt(context, key, defaultValue);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        return settings.getInt(key, defaultValue);
    }

    public static void setPrefInt(Context context, final String key,
                                  final int value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).commit();
    }

    public static void setPrefInt(Context context, String name, int mode,
                                      final String key, final int value) {
        if (name == null || (name != null && name.equals(""))) setPrefInt(context, key, value);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        settings.edit().putInt(key, value).commit();
    }

    /** ---------------- float --------------- */
    public static float getPrefFloat(Context context, final String key,
                                     final float defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }

    public static float getPrefFloat(Context context, String name, int mode,
                                 String key, final float defaultValue) {
        if (name == null || (name != null && name.equals(""))) getPrefFloat(context, key, defaultValue);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        return settings.getFloat(key, defaultValue);
    }

    public static void setPrefFloat(Context context, final String key,
                                    final float value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putFloat(key, value).commit();
    }

    public static void setPrefFloat(Context context, String name, int mode,
                                      final String key, final float value) {
        if (name == null || (name != null && name.equals(""))) setPrefFloat(context, key, value);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        settings.edit().putFloat(key, value).commit();
    }

    /** ------------------- long ------------------- */
    public static long getPrefLong(Context context, final String key,
                                   final long defaultValue) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        return settings.getLong(key, defaultValue);
    }

    public static long getPrefFloat(Context context, String name, int mode,
                                     String key, final long defaultValue) {
        if (name == null || (name != null && name.equals(""))) getPrefLong(context, key, defaultValue);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        return settings.getLong(key, defaultValue);
    }

    public static void setSettingLong(Context context, final String key,
                                      final long value) {
        final SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).commit();
    }

    public static void setPrefLong(Context context, String name, int mode,
                                    final String key, final long value) {
        if (name == null || (name != null && name.equals(""))) setSettingLong(context, key, value);
        final SharedPreferences settings = context.getSharedPreferences(name, mode);
        settings.edit().putLong(key, value).commit();
    }

    /** --------------- clear -------------- */
    public static void clearPreference(Context context,
                                       final SharedPreferences p) {
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }

    public static boolean hasKey(Context context, final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(
                key);
    }
}
