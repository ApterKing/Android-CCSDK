package cc.sdkutil.view;

import android.app.Application;
import android.os.Build;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 14-12-24.
 * 基类Application，用于打印Application生命周期日志. <br>
 */
@CCDebug
public class CCBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CCLogUtil.d(CCBaseApplication.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onCreate");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        CCLogUtil.d(CCBaseApplication.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        CCLogUtil.d(CCBaseApplication.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onTerminate");
    }

    @Override
    public void onTrimMemory(int level) {
        if (Build.VERSION.SDK_INT >= 14)
            super.onTrimMemory(level);
        CCLogUtil.d(CCBaseApplication.class.getAnnotation(CCDebug.class).debug(),
                getClass(), "onTrimMemory");
    }
}
