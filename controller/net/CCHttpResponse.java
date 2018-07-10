package cc.sdkutil.controller.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import cc.sdkutil.controller.core.CCObservable;
import cc.sdkutil.controller.core.CCObserver;
import cc.sdkutil.controller.core.CCSDKUtil;
import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.core.CCSDKConstants;
import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.net.CCHttpException;
import cc.sdkutil.model.net.CCHttpExceptionType;
import cc.sdkutil.model.net.CCHttpResponseInfo;

/**
 * @author wangcong
 * create on 12-14-22. <br>
 * http请求响应类， 支持获取头、无响应读取数据到{@link StringBuilder}、反馈读取数据;
 *  该类不能直接实例化，需通过{@link CCHttpRequest}执行获取 <br>
 */
@CCDebug
public class CCHttpResponse implements CCObserver {
	
    private volatile boolean isCancel = false;

    //请求失败相关
    private CCHttpException httpException;

    //请求成功
    private CCHttpResponseInfo responseInfo;

    private CCHttpResponseCallback responseCallback;

    public CCHttpResponse(final CCHttpResponseInfo info, Exception exception) {
        this.responseInfo = info;

        httpException = new CCHttpException();
        httpException.setStatusCode(info.getResponseCode());
        httpException.setStatusMessage(info.getResponseMessage());
        httpException.setRequestUrl(info.getConnection().getURL().toString());
        httpException.setException(exception);
    }
    
    public void cancel() {
    	isCancel = true;
        httpException.setType(CCHttpExceptionType.INTRRUPT_EXCEPTION);
        httpException.setStatusMessage(httpException.getType().name());
        CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
        responseCallback.onFail(httpException, httpException.getType().toString().getBytes());
    }
    
    /**
     * 返回数据InputStream
     */
    private InputStream getInputStream() throws IOException {
    	if (responseInfo.getResponseCode() >= 400) {
    		return getErrorStream(responseInfo.getConnection());
    	} else {
    		return getInputStream(responseInfo.getConnection());
    	}
    }

    /**
     *
     * @param conn
     * @return
     * @throws IOException
     */
	private InputStream getErrorStream(HttpURLConnection conn) throws IOException {
		final String contentEncoding = responseInfo.getContentEncoding();
		if (contentEncoding != null) {
			if (contentEncoding.contains("gzip")) {
				return new GZIPInputStream(conn.getErrorStream());
			}
			if (contentEncoding.contains("deflate")) {
				return new InflaterInputStream(conn.getErrorStream(), new Inflater(true));
			}
		}
		return conn.getErrorStream();
	}

    /**
     * 获取指定连接的输入流,支持gzip 和deflate 压缩
     * @param conn
     * @return
     * @throws IOException
     */
	private InputStream getInputStream(HttpURLConnection conn) throws IOException {
		final String contentEncoding = responseInfo.getContentEncoding();
		if (contentEncoding != null) {
			if (contentEncoding.contains("gzip")) {
				return new GZIPInputStream(conn.getInputStream());
			}
			if (contentEncoding.contains("deflate")) {
				return new InflaterInputStream(conn.getInputStream(), new Inflater(true));
			}
		}
		return conn.getInputStream();
	}

    /**
     * 直接将数据读取到StringBuilder中
     * @param buffer
     * @throws IOException
     */
    public void read(StringBuilder buffer) throws IOException {
        String enc = responseInfo.getContentCharset();
        InputStream is = getInputStream();
        final InputStreamReader reader = new InputStreamReader(is, enc);
        final char[] inBuf = new char[64];
        for (int charsRead; (charsRead = reader.read(inBuf)) != -1;) {
            buffer.append(inBuf, 0, charsRead);
        }
        reader.close();
        is.close();
    }
    
    /**
     * 带有反馈响应的读取数据
     * @param responseCallback
     */
    public void read(CCHttpResponseCallback responseCallback)  {
        if (responseCallback == null) {
            responseInfo.getConnection().disconnect();
            return;
        }
        this.responseCallback = responseCallback;

        //检测连接相关问题，如网络超时，本地数据封装错误
    	if (httpException.getException() != null) {
            if (httpException.getException() instanceof SocketTimeoutException) {
                httpException.setType(CCHttpExceptionType.TIMEOUT_EXCEPTION);
            } else if (httpException.getException() instanceof NoSuchAlgorithmException ||
                    httpException.getException() instanceof KeyManagementException) {
                httpException.setType(CCHttpExceptionType.HTTPS_EXCEPTION);
            } else {
                httpException.setType(CCHttpExceptionType.READ_EXCEPTION);
            }
            responseCallback.onFail(httpException, httpException.getType().toString().getBytes());
    		return;
    	}
        CCObservable.newInstance().registerObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
        byte[] entityData = new byte[0];
        try {
            InputStream inputStream = getInputStream();
            if (responseInfo.getContentLength() != -1) {    // 支持进度
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bt;
                int readLength = 0;
                while ((bt = inputStream.read()) != -1) {
                    if (isCancel) {   //检测是否取消
                        httpException.setType(CCHttpExceptionType.INTRRUPT_EXCEPTION);
                        httpException.setStatusMessage(httpException.getType().name());
                        inputStream.close();
                        CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
                        responseCallback.onFail(httpException, httpException.getType().toString().getBytes());
                        return;
                    }
                    readLength ++;
                    responseCallback.onProgress(responseInfo.getContentLength(), readLength);
                    baos.write(bt);
                    baos.flush();
                }
                entityData = baos.toByteArray();
                baos.close();
                inputStream.close();
            } else {
                final InputStreamReader reader = new InputStreamReader(inputStream,
                        responseInfo.getContentCharset() != null ? responseInfo.getContentCharset() : CCHttpClient.DEFAULT_CHARSET);
                final StringBuilder buffer = new StringBuilder();
                final char[] inBuf = new char[64];
                for (int charsRead; (charsRead = reader.read(inBuf)) != -1; ) {
                    buffer.append(inBuf, 0, charsRead);
                }
                reader.close();
                inputStream.close();
                entityData = buffer.toString().getBytes(responseInfo.getContentCharset() != null ? responseInfo.getContentCharset() : CCHttpClient.DEFAULT_CHARSET);
            }
        } catch (IOException e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "read--async", e.getMessage());
            CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
            httpException.setType(CCHttpExceptionType.READ_EXCEPTION);
            httpException.setStatusMessage(e.getMessage());
            httpException.setException(e);
            responseCallback.onFail(httpException, httpException.getType().toString().getBytes());
            return;
        }

        CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
        if (responseInfo.getResponseCode() >= 400) {  //获取到服务器错误响应
            httpException.setType(CCHttpExceptionType.ERROR_EXCEPTION);
            httpException.setStatusMessage(responseInfo.getResponseMessage());
        	responseCallback.onFail(httpException, entityData);
        } else {
        	responseCallback.onSuccess(responseInfo, entityData);
        }
    }

	@Override
	public void update(String category, Object... objs) {
		// TODO Auto-generated method stub
		if (!CCSDKUtil.isConnectedToNet()) {
            httpException.setType(CCHttpExceptionType.NETERROR_EXCEPTION);
            httpException.setStatusMessage(httpException.getType().name());
            responseCallback.onFail(httpException, httpException.getType().toString().getBytes());
            CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
        }
	}
    
}
