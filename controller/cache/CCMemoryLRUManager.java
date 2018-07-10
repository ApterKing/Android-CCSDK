package cc.sdkutil.controller.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import cc.sdkutil.model.cache.CCByteSoftReference;
import cc.sdkutil.model.cache.CCByteWrapper;
import cc.sdkutil.model.cache.CCHttpByteSoftReference;
import cc.sdkutil.model.cache.CCHttpByteWrapper;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-1-6.
 * 内存缓存管理工具. <br>
 */
@CCDebug
final class CCMemoryLRUManager {

    //最大缓存大小，该值通过框架初始化时设置
    private long maxCacheSize;
    //当前缓存大小
    private long cacheSize = 0;

    //用于实现LRU算法
    private final LinkedHashMap<String,
            SoftReference<? extends CCByteWrapper>> linkedHashMap;

    // 引用队列，用于实现内存缓存数据是否被回收检测
    private final ReferenceQueue<CCByteWrapper> referenceQueue;

    private static CCMemoryLRUManager instance = null;

    private CCMemoryLRUManager() {
        linkedHashMap = new LinkedHashMap<String, SoftReference<? extends CCByteWrapper>>(16, .75f, true) {
            private static final long serialVersionUID = 1L;
            @Override
            protected boolean removeEldestEntry(
                    Entry<String, SoftReference<? extends CCByteWrapper>> eldest) {
                // TODO Auto-generated method stub
                boolean shouldRemove = cacheSize > maxCacheSize;
                if (shouldRemove) {
                    clearRecycledObject();
                    System.gc();
                }
                return shouldRemove;
            }
        };
        referenceQueue = new ReferenceQueue<CCByteWrapper>();
    }

    //单例
    static synchronized CCMemoryLRUManager newInstance() {
        if (instance == null) {
            instance = new CCMemoryLRUManager();
        }
        return instance;
    }

    /**
     * 设置最大内存缓存大小
     * @param maxCacheSize
     */
    public void setMaxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public long getMaxCacheSize() {
        return maxCacheSize;
    }

    /**
     * 获取当前缓存大小
     * @return
     */
    public long getCacheSize() {
        return cacheSize;
    }

    /**
     * 将数据缓存至内存
     * @param key    缓存关键字
     * @param value  待缓存的数据
     */
    public void cacheByte(String key, byte[] value) {
        CCByteWrapper wrapper = new CCByteWrapper(value);
        CCByteSoftReference softReference = new CCByteSoftReference(key, wrapper, referenceQueue);
        linkedHashMap.put(key, softReference);
        wrapper = null;
    }

    /**
     * 将与Http返回有关的缓存数据缓存到内存
     * @param key      缓存关键字
     * @param value    待缓存的数据
     * @param param     Http Etag头等
     */
    public void cacheHttpByte(String key, byte[] value, CCHttpByteWrapper.CCHttpByteWrapperParam param) {
        CCHttpByteWrapper wrapper = new CCHttpByteWrapper(value, param);
        CCHttpByteSoftReference softReference = new CCHttpByteSoftReference(key, wrapper, referenceQueue);
        linkedHashMap.put(key, softReference);
        wrapper = null;
    }

    /**
     * 从内存缓存中获取{@link CCByteWrapper}数据
     * @param key   在缓存中的关键字
     * @return
     */
    public CCByteWrapper getByteWrapper(String key) {
        @SuppressWarnings("unchecked")
        SoftReference<CCByteWrapper> softReference = (SoftReference<CCByteWrapper>) linkedHashMap.get(key);
        return softReference != null ? softReference.get() : null;
    }

    /**
     * 从内存缓存中获取byte[] 数据
     * @param key   在缓存中的关键字
     * @return
     */
    public byte[] getByte(String key) {
        CCByteWrapper wrapper = getByteWrapper(key);
        return wrapper != null ? wrapper.data : null;
    }

    /**
     * 从内存缓存中获取{@link CCHttpByteWrapper}数据
     * @param key   在缓存中的关键字
     * @return
     */
    public CCHttpByteWrapper getHttpByteWrapper(String key) {
        @SuppressWarnings("unchecked")
        SoftReference<CCHttpByteWrapper> softReference = (SoftReference<CCHttpByteWrapper>) linkedHashMap.get(key);
        return softReference != null ? softReference.get() : null;
    }

    /**
     * 从内存缓存中获取byte[] 数据
     * @param key   在缓存中的关键字
     * @return
     */
    public byte[] getHttpByte(String key) {
        CCHttpByteWrapper wrapper = getHttpByteWrapper(key);
        return wrapper != null ? wrapper.data : null;
    }

    /**
     * 清除缓存在内存中已被JVM回收掉的数据
     */
    private void clearRecycledObject() {
        SoftReference<? extends CCByteWrapper> ref = null;
        while ((ref = (CCByteSoftReference) referenceQueue.poll()) != null) {
            if (ref instanceof CCByteSoftReference) {
                cacheSize -= ((CCByteSoftReference) ref).getContentLength();
                linkedHashMap.remove(((CCByteSoftReference) ref).getKey());
            } else if (ref instanceof CCHttpByteSoftReference) {
                cacheSize -= ((CCHttpByteSoftReference) ref).getContentLength();
                linkedHashMap.remove(((CCHttpByteSoftReference) ref).getKey());
            }
        }
    }

    /**
     * 清空内存缓存
     */
    public void clearCache() {
        linkedHashMap.clear();
        cacheSize = 0;
        System.gc();
        System.runFinalization();
    }
}
