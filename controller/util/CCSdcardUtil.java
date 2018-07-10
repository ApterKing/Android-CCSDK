package cc.sdkutil.controller.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by wangcong on 14-12-26.
 * 检测sd卡是否可用，获取应用缓存路径. <br>
 */
public class CCSdcardUtil {
	
	/**
	 * 检测sd卡是否可用
	 * @param context
	 * @return
	 */
	public static final boolean isAccessExternal(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
			&& context.getExternalCacheDir() != null)
			return true;
		return false;
	}
	
	/**
	 * 获取应用缓存目录
	 * @param context
	 * @return
	 */
	public static final String getCacheDir(Context context) {
        File cacheDir = new File(isAccessExternal(context) ? context.getExternalCacheDir().getPath()
                : context.getCacheDir().getPath());
        if (!cacheDir.exists()) cacheDir.mkdirs();
		return cacheDir.getPath();
	}

    /**
     * 在应用缓存目录中创建文件夹
     * @param context
     * @param dirPath
     * @return
     */
    public static final String mkDirIfNotExists(Context context, String dirPath) {
        String cacheDir = getCacheDir(context);
        File file = new File(cacheDir, dirPath);
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

}
