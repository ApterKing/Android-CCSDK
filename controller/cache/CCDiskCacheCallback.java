package cc.sdkutil.controller.cache;

/**
 * Created by wangcong on 15-1-10.
 * 磁盘缓存回调（磁盘缓存采用的是异步方式)
 */
public interface CCDiskCacheCallback {

    public void onCompleted(boolean success);
}
