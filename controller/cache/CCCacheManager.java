package cc.sdkutil.controller.cache;

import android.content.Context;

import java.io.File;

import cc.sdkutil.controller.util.CCSdcardUtil;
import cc.sdkutil.model.cache.CCByteWrapper;
import cc.sdkutil.model.cache.CCHttpByteWrapper;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 14-12-18. <br>
 * 实现内存与磁盘LRU缓存算法，支持分别设置缓存大小，缓存和获取数据，支持异步清空缓存并相应回调. <br>
 */
@CCDebug
public final class CCCacheManager {

    /** 默认最大内存缓存数据 */
    private final static long DEFAULT_MAX_MEM_SIZE = 4 * 1024 * 1024;

    /** 默认最大磁盘缓存数据 */
    private final static long DEFAULT_MAX_DISK_SIZE = 16 * 1024 * 1024;

	private static CCCacheManager instance = null;

    static {
        CCDiskLRUManager.newInstance().setMaxCacheSize(DEFAULT_MAX_DISK_SIZE);
        CCMemoryLRUManager.newInstance().setMaxCacheSize(DEFAULT_MAX_MEM_SIZE);
    }

	private CCCacheManager() {

    }

    //单例
	public static synchronized CCCacheManager newInstance(){
		if (instance == null) {
			instance = new CCCacheManager();
		}
		return instance;
	}

    /**
     * 初始化设置，该方法用于初始化读取磁盘当前缓存大小等工作，框架必须在自定义的{@link android.app.Application}
     * 中调用后才能够使用到该功能的后续操作，如：当前磁盘缓存数据大小,这个关系到磁盘LRU及清除缓存设置的相关动作
     * @param context
     */
    public static void initialize(final Context context) {
        File file = new File(CCSdcardUtil.getCacheDir(context));
        if (file.exists()) CCDiskLRUManager.newInstance().initializeLRU(file);
    }

    /**
     * 获取图片缓存路径
     * @param context
     * @return
     */
    public static String getImageCacheDir(Context context) {
        File file = new File(CCSdcardUtil.getCacheDir(context), "ccsdkimage");
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

    /**
     * 获取http自动缓存目录
     * @param context
     * @return
     */
    public static String getHttpCacheDir(Context context) {
        File file = new File(CCSdcardUtil.getCacheDir(context), "ccsdkhttp");
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

    /**
     * 获取到应用程序崩溃缓存目录
     * @param context
     * @return
     */
    public static String getCrashDir(Context context) {
        File file = new File(CCSdcardUtil.getCacheDir(context), "ccsdkcrash");
        if (!file.exists()) file.mkdirs();
        return file.getPath();
    }

    /**
     * 设置最大内存缓存大小
     * @param cacheSize
     */
    public void setMaxMemoryCacheSize(int cacheSize) {
        CCMemoryLRUManager.newInstance().setMaxCacheSize(cacheSize);
    }

    /**
     * 设置最大磁盘缓存大小
     * @param cacheSize
     */
	public void setMaxDiskCacheSize(int cacheSize) {
        CCDiskLRUManager.newInstance().setMaxCacheSize(cacheSize);
	}
	
	/**
	 * 获取当前内存缓存大小
	 * @return
	 */
	public long getCacheSize() {
		return CCMemoryLRUManager.newInstance().getCacheSize() +
                CCDiskLRUManager.newInstance().getCacheSize();
	}
	
	/**
	 * 将数据缓存至内存 
	 * @param key    缓存关键字
	 * @param value  待缓存的数据
	 */
	public void cacheByteInMemory(String key, byte[] value) {
		CCMemoryLRUManager.newInstance().cacheByte(key, value);
	}
	
	/**
	 * 将与Http返回有关的缓存数据缓存到内存
	 * @param key      缓存关键字
	 * @param value    待缓存的数据
	 * @param param     Http Etag头等
	 */
	public void cacheHttpByteInMemory(String key, byte[] value, CCHttpByteWrapper.CCHttpByteWrapperParam param) {
		CCMemoryLRUManager.newInstance().cacheHttpByte(key, value, param);
	}
	
	/**
	 * 将数据缓存至本地磁盘
	 * @param path   缓存路径
	 * @param value  待缓存数据
	 */
	public void cacheByteInDisk(final String path, final byte[] value) {
		CCDiskLRUManager.newInstance().cacheByte(path, value, null);
	}

    /**
     * 将数据缓存至本地磁盘
     * @param path   缓存路径
     * @param value  待缓存数据
     * @param cacheCallback  缓存完成后回调
     */
    public void cacheByteInDisk(final String path, final byte[] value, CCDiskCacheCallback cacheCallback) {
        CCDiskLRUManager.newInstance().cacheByte(path, value, cacheCallback);
    }
	
	/**
	 * 将与Http返回有关的缓存数据缓存到本地磁盘
	 * @param path     缓存路径
	 * @param value    待缓存数据
	 * @param param     Http Etag头等
	 */
	public void cacheHttpByteInDisk(final String path, final byte[] value, final CCHttpByteWrapper.CCHttpByteWrapperParam param) {
		CCDiskLRUManager.newInstance().cacheHttpByte(path, value, param, null);
	}

    /**
     * 将与Http返回有关的缓存数据缓存到本地磁盘
     * @param path     缓存路径
     * @param value    待缓存数据
     * @param param     Http Etag头等
     * @param cacheCallback 缓存完成后回调
     */
    public void cacheHttpByteInDisk(final String path, final byte[] value, final CCHttpByteWrapper.CCHttpByteWrapperParam param, CCDiskCacheCallback cacheCallback) {
        CCDiskLRUManager.newInstance().cacheHttpByte(path, value, param, cacheCallback);
    }
	
	/**
	 * 从内存缓存中获取{@link CCByteWrapper}数据
	 * @param key   在缓存中的关键字
	 * @return
	 */
	public CCByteWrapper byteWrapperFromMemory(String key) {
		return CCMemoryLRUManager.newInstance().getByteWrapper(key);
	}
	
	/**
	 * 从内存缓存中获取byte[] 数据
	 * @param key   在缓存中的关键字
	 * @return
	 */
	public byte[] byteFromMemory(String key) {
		return CCMemoryLRUManager.newInstance().getByte(key);
	}
	
	/**
	 * 从内存缓存中获取{@link CCHttpByteWrapper}数据
	 * @param key   在缓存中的关键字
	 * @return
	 */
	public CCHttpByteWrapper httpByteWrapperFromMemory(String key) {
		return CCMemoryLRUManager.newInstance().getHttpByteWrapper(key);
	}
	
	/**
	 * 从内存缓存中获取byte[] 数据
	 * @param key   在缓存中的关键字
	 * @return
	 */
	public byte[] httpByteFromMemory(String key) {
		return CCMemoryLRUManager.newInstance().getHttpByte(key);
	}
	
	/**
	 * 从磁盘缓存中获取byte[] 数据
	 * @param path   在缓存中的关键字
	 * @return
	 */
	public byte[] byteFromDisk(final String path) {
		return CCDiskLRUManager.newInstance().getByte(path);
	}
	
	/**
	 * 从磁盘缓存中获取{@link CCByteWrapper}数据
	 * @param path   在缓存中的关键字
	 * @return
	 */
	public CCHttpByteWrapper httpByteWrapperFromDisk(final String path) {
		return CCDiskLRUManager.newInstance().getHttpByteWrapper(path);
	}
	
	/**
	 * 从磁盘缓存中获取 http byte[] 数据
	 * @param path   在缓存中的关键字
	 * @return
	 */
	public byte[] httpByteFromDisk(final String path) {
		return CCDiskLRUManager.newInstance().getHttpByte(path);
	}

    /**
     * 异步清空缓存
     * @param context
     * @param callback
     */
    public void clearCache(final Context context, final CCDiskClearCallback callback) {
        CCMemoryLRUManager.newInstance().clearCache();
        if (context == null) {
            throw new NullPointerException("context should not be null");
        }
        File dir = new File(CCSdcardUtil.getCacheDir(context));
        clearCache(dir, callback);
    }

    /**
     * 异步清空指定文件夹中的缓存
     * @param dir
     * @param callback
     */
    public void clearCache(final File dir, final CCDiskClearCallback callback) {
        CCMemoryLRUManager.newInstance().clearCache();
        if (dir == null) {
            throw new NullPointerException("dir should not be null");
        }
        if (dir.exists())
            CCDiskLRUManager.newInstance().clearCache(dir, callback);
    }
}
