package cc.sdkutil.controller.cache;

import android.os.Bundle;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import cc.sdkutil.controller.core.CCAsyncHandler;
import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.cache.CCHttpByteWrapper;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-1-6.
 * 磁盘缓存管理工具，支持LRU算法，支持异步缓存文件，删除文件并响应相关回调. <br>
 *
 * Update by wangcong on 15-04-29.
 * 使用本框架提供的异步处理机制处理初始化LRU，写入文件，清空缓存
 * {@link CCAsyncHandler}
 */
@CCDebug
final class CCDiskLRUManager {

    //最大缓存大小，该值通过框架初始化时设置
    private long maxCacheSize;
    //当前缓存大小
    private long cacheSize = 0;

    //用于实现LRU算法
    private final LinkedHashMap<String, Long> linkedHashMap;

    /** 用于磁盘异步处理 */
    private final CCDiskAsyncHandler diskAsyncHandler;
    /** 初始化LRU msg */
    private final static int INITIAL_LRU = 0xff0001;
    /** 写入磁盘 msg */
    private final static int WRITE_DISK = 0xff0002;
    /** 清空缓存 msg */
    private final static int CLEAR_DISK = 0xff0003;

    private static CCDiskLRUManager instance = null;

    private CCDiskLRUManager() {
        linkedHashMap = new LinkedHashMap<String, Long>(16, .75f, true) {
            private static final long serialVersionUID = 1L;
            @Override
            protected boolean removeEldestEntry(
                    Entry<String, Long> eldest) {
                // TODO Auto-generated method stub
                boolean shouldRemove = cacheSize > maxCacheSize;
                if (shouldRemove) {
                    File file = new File(eldest.getKey());
                    clearCache(file, null);
                }
                return shouldRemove;
            }
        };
        diskAsyncHandler = new CCDiskAsyncHandler();
    }

    //单例
    synchronized static CCDiskLRUManager newInstance() {
        if (instance == null) {
            instance = new CCDiskLRUManager();
        }
        return instance;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    /**
     * 设置最大缓存大小
     * @param maxCacheSize
     */
    public void setMaxCacheSize(long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public long getMaxCacheSize() {
        return maxCacheSize;
    }

    /**
     * 将数据缓存至本地磁盘
     * @param path   缓存路径
     * @param value  待缓存数据
     */
    public void cacheByte(final String path, final byte[] value, final CCDiskCacheCallback cacheCallback) {
        cacheData(cacheCallback, path, value, null);
    }

    /**
     * 将与Http返回有关的缓存数据缓存到本地磁盘
     * @param path     缓存路径
     * @param value    待缓存数据
     * @param param     Http Etag等数据
     */
    public void cacheHttpByte(final String path, final byte[] value, final CCHttpByteWrapper.CCHttpByteWrapperParam param, final CCDiskCacheCallback cacheCallback) {
        cacheData(cacheCallback, path, value, param);
    }

    /**
     * 将数据缓存到磁盘中
     * @param path
     * @param value
     * @param param
     * @param cacheCallback
     */
    private void cacheData(final CCDiskCacheCallback cacheCallback, final String path, final byte[] value, final CCHttpByteWrapper.CCHttpByteWrapperParam param) {
        final Message msg = new Message();
        msg.what = WRITE_DISK;
        msg.obj = cacheCallback;

        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putByteArray("bytes", value);
        if (param != null)
            bundle.putSerializable("param", param);
        msg.setData(bundle);
        diskAsyncHandler.sendAsyncMessage(msg);
    }

    /**
     * 从磁盘缓存中获取byte[] 数据
     * @param path   在缓存中的关键字
     * @return
     */
    public byte[] getByte(final String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bts = new byte[fis.available()];
            while (fis.read(bts) != -1) {
                baos.write(bts);
                bts = new byte[fis.available()];
                if (bts.length == 0) break;
            }
            byte[] result = baos.toByteArray();
            baos.close();
            fis.close();
            return result;
        } catch (IOException e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "getByte", e.getMessage());
            return null;
        }
    }

    /**
     * 从磁盘缓存中获取{@link cc.sdkutil.model.cache.CCByteWrapper}数据
     * @param path   在缓存中的关键字
     * @return
     */
    public CCHttpByteWrapper getHttpByteWrapper(final String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;

            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            CCHttpByteWrapper wrapper = (CCHttpByteWrapper)ois.readObject();
            fis.close();
            ois.close();
            return wrapper;
        } catch (Exception e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "getHttpByteWrapper", e.getMessage());
            return null;
        }
    }

    /**
     * 从磁盘缓存中获取 http byte[] 数据
     * @param path   在缓存中的关键字
     * @return
     */
    public byte[] getHttpByte(final String path) {
        CCHttpByteWrapper wrapper = getHttpByteWrapper(path);
        return wrapper != null ? wrapper.data : null;
    }

    /**
     * 供 {@link CCCacheManager} 初始化时调用，实现LRU初始化
     * @param cacheFile
     */
    public void initializeLRU(File cacheFile) {
        final Message asyncMsg = new Message();
        asyncMsg.what = INITIAL_LRU;
        asyncMsg.obj = cacheFile;
        diskAsyncHandler.sendAsyncMessage(asyncMsg);
    }

    /**
     * 递归读取缓存，并按缓存时间对文件排序
     * @param cacheFile
     */
    private TreeSet<File> readCache(File cacheFile) {
        final TreeSet<File> treeSet = new TreeSet<File>(new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return (int)(lhs.lastModified() - rhs.lastModified());
            }
        });
        if (cacheFile.isDirectory()) {
            File[] subFiles = cacheFile.listFiles();
            for (File subFile : subFiles) {
                readCache(subFile);
            }
        } else {
            treeSet.add(cacheFile);
        }
        return treeSet;
    }

    /**
     * 清空磁盘缓存
     */
    public void clearCache(final File dir, final CCDiskClearCallback callback) {
        final Message msg = new Message();
        msg.what = CLEAR_DISK;
        msg.obj = callback;

        Bundle bundle = new Bundle();
        bundle.putSerializable("dir", dir);
        msg.setData(bundle);
        diskAsyncHandler.sendAsyncMessage(msg);
    }

    /**
     * 删除文件
     * @param file
     */
    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                deleteFile(subFile);
            }
        } else {
            cacheSize -= file.length();
            file.delete();
        }
    }

    /**
     * 用于异步处理磁盘信息(读取所有信息道内存，写入数据，删除文件)
     */
    private class CCDiskAsyncHandler extends CCAsyncHandler {

        @Override
        protected void onAsyncDeal(Message asyncMsg) {
            super.onAsyncDeal(asyncMsg);
            switch (asyncMsg.what) {
                case INITIAL_LRU:
                    File cacheFile = (File) asyncMsg.obj;
                    TreeSet<File> treeSet = readCache(cacheFile);
                    Iterator<File> iterator = treeSet.iterator();
                    while (iterator.hasNext()) {
                        File file = iterator.next();
                        linkedHashMap.put(file.getPath(), file.lastModified());
                        cacheSize += file.length();
                    }
                    break;
                case WRITE_DISK:
                    try {
                        Bundle bundle = asyncMsg.getData();
                        String path = bundle.getString("path");
                        byte[] value = bundle.getByteArray("bytes");
                        CCHttpByteWrapper.CCHttpByteWrapperParam param = (CCHttpByteWrapper.CCHttpByteWrapperParam) bundle.getSerializable("param");

                        File file = new File(path);
                        if (!file.exists()) file.createNewFile();

                        FileOutputStream fos = new FileOutputStream(file);
                        if (param == null) {  //缓存字节数组
                            fos.write(value);
                            fos.close();
                        } else {  //缓存http字节数组
                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                            oos.writeObject(new CCHttpByteWrapper(value, param));
                            oos.close();
                            fos.close();
                        }
                        //缓存成功后将信息放入缓存中
                        cacheSize += value.length;
                        linkedHashMap.put(file.getPath(), Long.valueOf(file.lastModified()));
                    } catch (IOException e) {
                        CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                                getClass(), "cacheData", e.getMessage());
                    }
                    break;
                case CLEAR_DISK:
                    File dir = (File) asyncMsg.getData().getSerializable("dir");
                    deleteFile(dir);
                    break;
            }
        }

        @Override
        protected void onPostComplete(Message msg) {
            super.onPostComplete(msg);
            switch (msg.what) {
                case INITIAL_LRU:
                    // 无需告诉外部回调，这里不用作任何处理
                    break;
                case WRITE_DISK:
                    CCDiskCacheCallback cacheCallback = (CCDiskCacheCallback) msg.obj;
                    if (cacheCallback != null)
                        cacheCallback.onCompleted(true);
                    break;
                case CLEAR_DISK:
                    final CCDiskClearCallback clearCallback = (CCDiskClearCallback) msg.obj;
                    if (clearCallback != null)
                        clearCallback.onCompleted(true);
                    break;
            }
        }
    }
}
