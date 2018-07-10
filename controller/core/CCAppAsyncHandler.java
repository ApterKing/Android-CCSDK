package cc.sdkutil.controller.core;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.controller.view.CCAppBackgroundListener;
import cc.sdkutil.controller.view.CCAppForegroundListener;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-4-28.
 * 用于异步处理框架中需要处理的消息，如：监听程序进入前后台等
 */
@CCDebug
public class CCAppAsyncHandler extends CCAsyncHandler {

    public final static String TAG = "CCAsyncHandler";

    //  程序进入前后台监听
    public final static int APP_RUNNINGINFO_CHECK_ADD = 0xffff00;       // 新增前后台监听
    public final static int APP_RUNNINGINFO_CHECK_REMOVE = 0xffff01;   // 移除前后台监听

    private final static int APP_RUNNINGINFO_MSG = 0xff0000;     // running info 改变
    public Set<CCAppBackgroundListener> mAppBackgroundListeners;
    public Set<CCAppForegroundListener> mAppForegroundListenters;
    private boolean needPostRunningInfo = true;

    private int importance;


    private static CCAppAsyncHandler instance = null;

    private CCAppAsyncHandler() {
        super();
    }

    public synchronized static CCAppAsyncHandler newInstance() {
        if (instance == null) {
            instance = new CCAppAsyncHandler();
        }
        return instance;
    }

    @Override
    protected void onAsyncDeal(Message asyncMsg) {
        switch (asyncMsg.what) {
            case APP_RUNNINGINFO_MSG:
                asyncMsg.arg1 = getCurrentAppRunningInfo() == null ? ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE : getCurrentAppRunningInfo().importance;
                break;
        }
    }

    @Override
    protected void onPostComplete(Message msg) {
        switch (msg.what) {
            case APP_RUNNINGINFO_MSG:
                if (importance != msg.arg1) {
                    importance = msg.arg1;
                    if (importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        if (mAppBackgroundListeners == null) break;
                        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                                getClass(), "onAsyncComplete", TAG + "---app exit to background");
                        for (CCAppBackgroundListener backgroundListener : mAppBackgroundListeners) {
                            backgroundListener.onExitBackgound();
                        }
                    } else {
                        if (mAppForegroundListenters == null) break;
                        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                                getClass(), "onAsyncComplete", TAG + "---app enter foreground");
                        for (CCAppForegroundListener foregroundListener : mAppForegroundListenters) {
                            foregroundListener.onEnterForeground();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Object obj = msg.obj;
        switch (msg.what) {
            case APP_RUNNINGINFO_CHECK_ADD:
                if (obj instanceof CCAppBackgroundListener) {
                    if (mAppBackgroundListeners == null) mAppBackgroundListeners = new HashSet<>();
                    CCAppBackgroundListener backgroundListener = (CCAppBackgroundListener) obj;
                    mAppBackgroundListeners.add(backgroundListener);

                    if (needPostRunningInfo) {
                        needPostRunningInfo = false;
                        final Message asyncMsg = new Message();
                        asyncMsg.what = APP_RUNNINGINFO_MSG;
                        asyncMsg.arg1 = ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE;
                        sendScheduleAsyncMessage(asyncMsg, 0, 2 * 1000);
                    }
                }

                if (obj instanceof CCAppForegroundListener) {
                    if (mAppForegroundListenters == null) mAppForegroundListenters = new HashSet<>();
                    CCAppForegroundListener foregroundListener = (CCAppForegroundListener) obj;
                    mAppForegroundListenters.add(foregroundListener);
                    if (needPostRunningInfo) {
                        needPostRunningInfo = false;
                        final Message asyncMsg = new Message();
                        asyncMsg.what = APP_RUNNINGINFO_MSG;
                        asyncMsg.arg1 = ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE;
                        sendScheduleAsyncMessage(asyncMsg, 0, 2 * 1000);
                    }

                }
                break;
            case APP_RUNNINGINFO_CHECK_REMOVE:
                if (obj instanceof CCAppBackgroundListener) {
                    if (mAppBackgroundListeners == null) mAppBackgroundListeners = new HashSet<>();
                    CCAppBackgroundListener backgroundListener = (CCAppBackgroundListener) obj;
                    mAppBackgroundListeners.remove(backgroundListener);
                } else if (obj instanceof CCAppForegroundListener) {
                    if (mAppForegroundListenters == null) mAppForegroundListenters = new HashSet<>();
                    CCAppForegroundListener foregroundListener = (CCAppForegroundListener) obj;
                    mAppForegroundListenters.remove(foregroundListener);
                }

                // 如果前后台监听都不需要了，则移除循环消息处理
                if ((mAppForegroundListenters == null || (mAppForegroundListenters != null && mAppForegroundListenters.size() == 0))
                        && (mAppBackgroundListeners == null || (mAppBackgroundListeners != null && mAppBackgroundListeners.size() == 0))) {
                    cancelOperation(APP_RUNNINGINFO_MSG);
                    needPostRunningInfo = true;
                }
                break;
        }
    }

    /**
     * 获取当前运行程序的信息
     * @return
     */
    public ActivityManager.RunningAppProcessInfo getCurrentAppRunningInfo() {
        ActivityManager activityManager = (ActivityManager) CCSDKUtil.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : appProcessInfos) {
            if (info.processName.equals(CCSDKUtil.getContext().getPackageName())) {
                return info;
            }
        }
        return null;
    }
}
