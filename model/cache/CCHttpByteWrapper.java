package cc.sdkutil.model.cache;

import java.io.Serializable;

/**
 * Created by wangcong on 14-11-18. <br>
 * 用于封装Http请求需要缓存的数据，采用两种方式(Etag, expire)检测缓存数据是否还是合格的. <br>
 */
public class CCHttpByteWrapper extends CCByteWrapper {

	private static final long serialVersionUID = 1L;

    private CCHttpByteWrapperParam param;

    /**
     * 用于封装http头相关的超时信息
     */
    public static class CCHttpByteWrapperParam implements Serializable {
        public String etag;
        public String expire;
    }

	/**
	 * 与初始化一个
	 * @param data
	 */
	public CCHttpByteWrapper(byte[] data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

    /**
     *
     * @param data
     * @param param
     */
	public CCHttpByteWrapper(byte[] data, CCHttpByteWrapperParam param) {
		super(data);
		this.param = param;
	}

    public CCHttpByteWrapperParam getParam() {
        return param;
    }
}
