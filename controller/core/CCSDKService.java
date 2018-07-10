package cc.sdkutil.controller.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import cc.sdkutil.controller.cache.CCCacheManager;
import cc.sdkutil.model.core.CCSDKConstants;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-1-15.
 * 框架服务，用于连接服务器获取许可码操作. <br>
 */
@CCDebug
public final class CCSDKService extends Service {

    private final static String SERVICE_RESTART = "cc.sdkutil.core.SERVICE_RESTART";

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(SERVICE_RESTART);
        registerReceiver(mBroadcastReceiver, filter);
        CCCacheManager.initialize(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void onDestroy() {
        Intent intent = new Intent(SERVICE_RESTART);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //框架广播，用于监听网络状态改变等
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                CCObservable.newInstance().notifyObserver(CCSDKConstants.NET_STATUS_CHANGED);
            } else if (intent.getAction().equals(SERVICE_RESTART)) {
                context.startService(new Intent(context, CCSDKService.class));
            }
        }
    };

}
