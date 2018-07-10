package cc.sdkutil.model.cache;

import java.io.Serializable;

/**
 * Created by wangcong on 14-12-18. <br>
 * 用于封装缓存数据. <br>
 */
public class CCByteWrapper implements Serializable {

	private final static long serialVersionUID = 1L;

	/** 存放的数据  */
	public byte[] data;
	
	/* 存放数据长度 */
	protected int contentLength;
	
	public CCByteWrapper(final byte[] data) {
		this.data = data;
		contentLength = data.length;
	}
	
	public byte[] getData() {
		return data;
	}

	public int getContentLength() {
		return data == null ? 0 : data.length;
	}
}
