package cc.sdkutil.controller.util;


import android.util.Log;

/**
 * Created by wangcong on 14-12-26.
 * 在控制台打印Log,发布版本时在Application中设置答应Log 为false
 */
public class CCLogUtil {

	public static boolean isDebug = true;
	private static final String TAG = "LogUtil";

	public static void i(String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}

	public static void i(Class<?> _class, String msg) {
		if (isDebug)
			Log.i(_class.getName(), msg);
	}

	public static void d(Class<?> _class, String msg) {
		if (isDebug)
			Log.d(_class.getName(), msg);
	}

	public static void e(Class<?> _class, String msg) {
		if (isDebug)
			Log.e(_class.getName(), msg);
	}

	public static void v(Class<?> _class, String msg) {
		if (isDebug)
			Log.v(_class.getName(), msg);
	}

	public static void i(String tag, String msg) {
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebug)
			Log.d(tag, msg);
	}
	
	public static void d(Class<?> _class, String methodName, String msg) {
		if (isDebug && (_class  != null || methodName != null) && msg != null)
			Log.d(_class.getName() + "--" + methodName, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebug)
			Log.e(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (isDebug)
			Log.v(tag, msg);
	}

    /**
     * 此方法用于框架内部调试
     * @param debug
     * @param clazz
     * @param method
     * @param msg
     */
    public static void  d(boolean debug, Class<?> clazz, String method, String msg) {
        if (!isDebug) return;
        if (debug && msg != null)
            Log.d(clazz.getName() + " -- " + method, msg);
    }

    /**
     *
     * @param debug
     * @param clazz
     * @param msg
     */
    public static  void  d(boolean debug, Class<?> clazz, String msg) {
        if (!isDebug) return;
        if (debug && msg != null)
            Log.d(clazz.getName(), msg);
    }
}
