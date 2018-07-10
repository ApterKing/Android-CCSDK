package cc.sdkutil.controller.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangcong on 14-12-22. <br>
 * HTTP访问客户端，初始化客户端发起http请求; 编码方式、cookie、userAgent等
 * , 支持（POST/GET/PUT/HEAD/DELETE）方式访问服务器端，默认支持gzip， deflate压缩，
 * 支持多文件上传，支持取消操作，支持下载进度，支持断点续传功能
 */
public class CCHttpClient {

	/** 默认请求时长 */
	public final static int DEFAULT_CON_TIMEOUT = 10 * 1000;
	/** 默认获取数据时长 */
	public final static int DEFAULT_SO_TIMEOUT = 20 * 1000;
	/** 默认编码类型 */
	public final static String DEFAULT_CHARSET = "UTF-8";

	public final static String HTTP_POST = "POST";
	public final static String HTTP_PUT = "PUT";
	public final static String HTTP_DELETE = "DELETE";
	public final static String HTTP_GET = "GET";
	public final static String HTTP_HEAD = "HEAD";

	private short port;
	
	private int connectionTimeOut;
	
	private int soTimeOut;

	/** 相关头 */
	private final Map<String, List<String>> headerMap;  

	/** =======  请求相关 ======== */

	/** 请求的url */
	String requestUrl;

	/** POST/PUT/DELETE 请求方式参数 */
	CCMultiParam multiParam;

	/** GET/HEAD 请求方式参数 */
	Map<String, String> param;

	/** 请求方法 */
	String requestMethod;

	protected CCHttpClient() {
		port = 80;
		connectionTimeOut = DEFAULT_CON_TIMEOUT;
		soTimeOut = DEFAULT_SO_TIMEOUT;

		headerMap = new HashMap<String, List<String>>(6);
		this.setUserAgent("goncgnaw_yb_etaerc_krowemarf_kdscc");
		this.setCharset(DEFAULT_CHARSET);
		this.setHeader("Connection", "keep-alive");
	}

	/**
	 * 设置编码方式，默认采用UTF-8
	 * @param charset
	 */
	public void setCharset(String charset) {
		this.setHeader("Accept-Charset", charset);
	}

	/**
	 * 返回编码方式，默认为UTF-8
	 * @return
	 */
	public String getCharset() {
		return headerMap.get("Accept-Charset").get(0);
	}

	/**
	 * 设置User-Agent
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent) {
		this.setHeader("User-Agent", userAgent);
	}

	/**
	 * 设置Host
	 * @param host
	 */
	public void setHost(String host) {
		this.setHeader("Host", host);
	}

	/**
	 * 设置端口号
	 * @param port
	 */
	public void setPort(short port) {
		this.port = port;
	}

	/**
	 * 获取到端口号
	 * @return
	 */
	public short getPort() {
		return this.port;
	}

	/**
	 * 设置连接时长
	 * @param connectionTimeOut
	 */
	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	/**
	 * 获取到连接时长
	 * @return
	 */
	public int getConnectionTimeOut() {
		return this.connectionTimeOut;
	}

	/**
	 * 设置连读取数据时长
	 * @param soTimeOut
	 */
	public void setSoTimeOut(int soTimeOut) {
		this.soTimeOut = soTimeOut;
	}

	/**
	 * 获取到读取数据时长
	 * @return
	 */
	public int getSoTimeOut() {
		return this.soTimeOut;
	}

	/**
	 * 格式如
	 * <br>从某个位置开始到结尾: bytes=1024-
	 * <br>从某个位置到某个位置: bytes=1024-2048
	 * <br>同时指定几个range: bytes=512-1024,2048-4096
	 * @param range
	 */
	public void setRange(String range) {
		this.setHeader("Range", range);
	}

	/**
	 * 设置数据响应编码 gzip,deflate
	 * @param encoding
	 */
	public void setContentEncoding(String encoding) {
		this.setHeader("Content-Encoding", encoding);
	}

    /**
     * 设置头域
     * @param name
     * @param value
     */
	public void setHeader(String name, String value) {
		List<String> values = headerMap.get(name);
		if (values == null) {
			values = new ArrayList<String>();
			headerMap.put(name, values);
		} else {
            values.clear();
        }
        values.add(value);
	}

    /**
     * 新增头域
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        List<String> values = headerMap.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            headerMap.put(name, values);
        }
        values.add(value);
    }

	/**
	 * 获取到设置的请求头
	 * @return
	 */
	public Map<String, List<String>> getHeaders() {
		return headerMap;
	}

	/**
	 * Post 方式访问
	 * @param uri 访问地址
	 * @param params {@link CCMultiParam}
	 * @return {@link CCHttpRequest}
	 */
	public CCHttpRequest post(String uri, CCMultiParam params) {
		return request(uri, HTTP_POST, params, null);
	}

	/**
	 * Put 方式访问
	 * @param uri 访问地址
	 * @param params {@link CCMultiParam}
	 * @return {@link CCHttpRequest}
	 */
	public CCHttpRequest put(String uri, CCMultiParam params) {
		return request(uri, HTTP_PUT, params, null);
	}

	/**
	 * Delete 方式访问
	 * @param uri 访问地址
	 * @param params {@link CCMultiParam}
	 * @return {@link CCHttpRequest}
	 */
	public CCHttpRequest delete(String uri, CCMultiParam params) {
		return request(uri, HTTP_DELETE, params, null);
	}

	/**
	 * get 访问方式
	 * @param uri
	 * @param params
	 * @return
	 */
	public CCHttpRequest get(String uri, Map<String, String> params) {
		return request(uri, HTTP_GET, null, params);
	}

	/**
	 * get 访问方式
	 * @param uri url请求头，不包含请求参数
	 * @param names 请求参数名称
	 * @param values 请求参数值
	 * @return
	 */
	public CCHttpRequest get(String uri, String[] names, String[] values) {
		if (names == null || values == null)
			throw new NullPointerException("键值对不能为空");
		int length = names.length > values.length ? values.length : names.length;
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < length; i++) {
			map.put(names[i], values[i]);
		}
		return get(uri, map);
	}

	/**
	 * head 访问方式
	 * @param uri url请求头，不包含请求参数
	 * @param params
	 * @return
	 */
	public CCHttpRequest head(String uri, Map<String, String> params) {
		return request(uri, HTTP_HEAD, null, params);
	}

	/**
	 * head 访问方式
	 * @param uri url请求头，不包含请求参数
	 * @param names 请求参数名称
	 * @param values 请求参数值
	 * @return
	 */
	public CCHttpRequest head(String uri, String[] names, String[] values) {
		if (names == null || values == null)
			throw new NullPointerException("键值对不能为空");
		int length = names.length > values.length ? values.length : names.length;
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < length; i++) {
			map.put(names[i], values[i]);
		}
		return head(uri, map);
	}

	/**
	 * 获取一个 {@link CCHttpRequest}
	 * @param uri
	 * @param method
	 * @param multiParam
	 * @param param
	 * @return
	 */
	private CCHttpRequest request(String uri, String method, CCMultiParam multiParam, Map<String, String> param) {
		this.requestUrl = uri;
		this.requestMethod = method;
		this.multiParam = multiParam;
		this.param = param;
		return new CCHttpRequest(this);
	}
}
