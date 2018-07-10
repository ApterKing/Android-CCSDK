package cc.sdkutil.model.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * Created by wangcong on 14-12-18. <br>
 * 缓存数据的软引用，通过key 记录在缓存中的建，方便查找到该缓存的数据是否已经被JVM清除, 主要用于将HTTP缓存数据
 * 封装成软引用. <br>
 * @see {@link CCHttpByteWrapper}
 */
public class CCHttpByteSoftReference extends SoftReference<CCHttpByteWrapper> {

	private String key;

    private int contentLength;   // 数据初始化之前的长度

	public CCHttpByteSoftReference(String key, CCHttpByteWrapper httpByteWrapper) {
		super(httpByteWrapper);
		this.key = key;
	}
	
	/**
	 * @param key 在缓存中的键
	 * @param httpByteWrapper 缓存数据 {@link CCHttpByteWrapper}
	 * @param queue
	 */
	public CCHttpByteSoftReference(String key, CCHttpByteWrapper httpByteWrapper,
                                   ReferenceQueue<? super CCHttpByteWrapper> queue) {
		super(httpByteWrapper, queue);
		this.key = key;
        this.contentLength = httpByteWrapper.getContentLength();
	}
	
	public String getKey() {
		return key;
	}

    public int getContentLength() {
        return contentLength;
    }
}
