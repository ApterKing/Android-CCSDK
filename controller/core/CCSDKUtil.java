package cc.sdkutil.controller.core;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.controller.util.CCNetUtil;

/**
 * Created by wangcong on 14-12-18. <br>
 * 框架初始化类，初始化应用程序必须调用此方法才能保证整个框架中的一些功能能够正常使用. <br>
 */
public class CCSDKUtil {

    private static Context mContext;
    private final static Handler mHandler = new Handler();

    /**
     * 初始化整个框架
     * @param context
     */
	public static void initialize(Context context) {
        mContext = context;
        Intent intent = new Intent(context, CCSDKService.class);
        context.startService(intent);
	}

    public static  void stop(Context context) {
        Intent intent = new Intent(context, CCSDKService.class);
        context.stopService(intent);
    }

    /**
     * 获取网络类型
     * @return
     */
    public static int getNetworkType() {
        return CCNetUtil.netType(mContext);
    }

    /**
     * 是否连接到网络
     * @return
     */
    public static boolean isConnectedToNet() {
        return CCNetUtil.isConnectToNet(mContext);
    }

    /**
     * 获取框架初始化Application Context
     * @return
     */
    public static Context getContext() {
        return mContext.getApplicationContext();
    }

    /**
     * 获取UI 操作Handler
     * @return
     */
    public static Handler getHandler() {
        return mHandler;
    }

    /**
     * 程序崩溃回调
     * @param callback
     */
    public static void setCrashHandlerCallback(CCCrashHandleCallback callback) {
        CCCrashHandler.newInstance(getContext(), callback);
    }

    /**
     * 设置是否可以调试，如果在发布版本时请将调试关闭，默认调试打开
     * @param debug
     */
    public static void setDebugable(boolean debug) {
        CCLogUtil.isDebug = debug;
    }
}
