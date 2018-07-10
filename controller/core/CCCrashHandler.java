package cc.sdkutil.controller.core;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;

import cc.sdkutil.controller.cache.CCCacheManager;
import cc.sdkutil.controller.util.CCToolUtil;

/**
 * Created by wangcong on 15-1-8.
 * 程序崩溃处理操作. <br>
 */
public class CCCrashHandler implements Thread.UncaughtExceptionHandler {

    private static CCCrashHandler insance = null;
    private Context mContext;
    private CCCrashHandleCallback mCallback;

    private CCCrashHandler(Context context, CCCrashHandleCallback callback) {
        this.mContext = context;
        this.mCallback = callback;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public synchronized static CCCrashHandler newInstance(Context context, CCCrashHandleCallback callback) {
        if (insance == null)
            insance = new CCCrashHandler(context, callback);
        return insance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Throwable tmpThrowable = null;
        if (ex.getMessage() == null) {
            StringBuilder builder = new StringBuilder(256);
            builder.append("\n");
            for (StackTraceElement element : ex.getStackTrace()) {
                builder.append(element.toString()).append("\n");
            }
            tmpThrowable = new Throwable(builder.toString(), ex);
        }
        if (handleException(tmpThrowable != null ? tmpThrowable : ex) && mCallback != null)
            mCallback.handException(tmpThrowable != null ? tmpThrowable : ex);
    }

    /**
     * 保存崩溃文件到本地
     * @param ex
     * @return
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) return false;
        try {
            Executors.newSingleThreadExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = new File(CCCacheManager.getCrashDir(mContext),
                                "crash_" + CCToolUtil.formatDate(new Date(), "yyyyMMddHHmmss") + ".log");
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(ex.getMessage().getBytes());
                        fos.close();
                    } catch (IOException e) {

                    }
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
