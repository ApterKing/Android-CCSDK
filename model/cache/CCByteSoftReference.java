package cc.sdkutil.model.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * Created by wangcong on 14-12-18. <br>
 * 缓存数据的软引用，通过key 记录在缓存中的建，方便查找到该缓存的数据是否已经被JVM清除. <br>
 */
public class CCByteSoftReference extends SoftReference<CCByteWrapper> {
	
	private String key;

    protected int contentLength;    // 保存对象在被回收之前的大小

	public CCByteSoftReference(String key, CCByteWrapper arg0) {
		this(key, arg0, null);
	}

	public CCByteSoftReference(String key, CCByteWrapper byteWrapper,
                               ReferenceQueue<? super CCByteWrapper> queue) {
		super(byteWrapper, queue);
		// TODO Auto-generated constructor stub
		this.key = key;
        this.contentLength = byteWrapper.getContentLength();
	}
	
	public String getKey() {
		return key;
	}

    public int getContentLength() {
        return contentLength;
    }
}
