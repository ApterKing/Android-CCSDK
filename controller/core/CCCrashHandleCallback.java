package cc.sdkutil.controller.core;

/**
 * Created by wangcong on 15-1-8.
 * 处理程序崩溃回调. <br>
 */
public interface CCCrashHandleCallback {
    public boolean handException(Throwable ex);
}
