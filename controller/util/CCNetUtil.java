package cc.sdkutil.controller.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import cc.sdkutil.model.core.CCSDKConstants;

/**
 * Created by wangcong on 14-12-26.
 * 网络状态检测类. <br>
 */
public class CCNetUtil {

    /**
	 * 获取到当前网络连接类型 (NET_NONE/NET_WWAN/NET_WIFI)
	 * @param context
	 * @return
	 */
	public static final int netType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if (netInfo == null || (netInfo != null && !netInfo.isAvailable())) {
            return CCSDKConstants.NET_NONE;
        } else if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return CCSDKConstants.NET_WIFI;
        } else {
            return CCSDKConstants.NET_WWAN;
        }
	}

	/**
	 * 判断当前是否连接到网络
	 * @param context
	 * @return
	 */
	public static final boolean isConnectToNet(Context context) {
		return netType(context) != CCSDKConstants.NET_NONE ? true : false;
	}
}
