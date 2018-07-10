package cc.sdkutil.view;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-4-27.
 */
@CCDebug
public class CCBaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onDestroy");
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onConfigurationChanged");
    }

    public void onLowMemory() {
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onLowMemory");
    }

    public void onTrimMemory(int level) {
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onTrimMemory");
    }

    @Override
    public IBinder onBind(Intent intent) {
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(), getClass(), "onUnbind");
        return super.onUnbind(intent);
    }
}
