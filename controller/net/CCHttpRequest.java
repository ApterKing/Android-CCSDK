package cc.sdkutil.controller.net;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cc.sdkutil.controller.core.CCObservable;
import cc.sdkutil.controller.core.CCObserver;
import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.core.CCSDKConstants;
import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.net.CCHttpException;
import cc.sdkutil.model.net.CCHttpExceptionType;
import cc.sdkutil.model.net.CCHttpResponseInfo;

/**
 * @author wangcong
 * 连接请求类，无需直接调用通过 {@link CCHttpClient} 构造. <br>
 */
@CCDebug
public class CCHttpRequest implements CCObserver {

	public volatile boolean isCancel = false;

	/** 连接请求 */
	private CCHttpClient mClient;

	private CCHttpRequestCallback requestCallback;
    private CCHttpException httpException;

	public CCHttpRequest(CCHttpClient client) {
		this.mClient = client;

        httpException = new CCHttpException();
	}

    /**
     * 获取请求连接
     * @return
     */
    public String getRequestUrl() {
        return mClient.requestUrl;
    }

	public void cancel() {
		isCancel = true;
	}

    /**
     * 带有反馈的执行HTTP访问
     * @param requestCallback
     * @return
     */
	@SuppressLint("TrulyRandom")
	public CCHttpResponse execute(CCHttpRequestCallback requestCallback) {
		HttpURLConnection conn = null;
        CCHttpResponseInfo info = null;
		try {
            CCObservable.newInstance().registerObserver(CCSDKConstants.NET_STATUS_CHANGED, this);

			//用于拼接 get/head 连接
			final StringBuilder builder = new StringBuilder(mClient.requestUrl);
			//用于封装 post/put/delete 上传数据
			byte[] content = new byte[0];
			if (mClient.param != null) {  // GET/HEAD请求
				if (!mClient.requestUrl.endsWith("?")) builder.append('?');
				int paramIndex = 0;
				for (Entry<String, String> entry : mClient.param.entrySet()) {
					if (paramIndex != 0) builder.append('&');
					builder.append(URLEncoder.encode(entry.getKey(), mClient.getCharset()))
						.append('=').append(URLEncoder.encode(entry.getValue(), mClient.getCharset()));
					paramIndex ++;
				}
			} else if (mClient.multiParam != null) {  // POST/PUT/DELETE 请求
				content = mClient.multiParam.toBytes(mClient.getCharset());
			}
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "url", builder.toString() + "\n content: " + new String(content));
            CCLogUtil.e(getClass(), "url : " + builder.toString() + "\n content: " + new String(content));

            //android 2.2 版本以前使用HttpUrlConnection的一个bug
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
                System.setProperty("http.keepAlive", "false");
            }
            //忽略https访问认证
            if (builder.toString().startsWith("https")) {
                TrustManager[] trustManagers = new TrustManager[] {new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }};
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                SSLContext sslc = SSLContext.getInstance("TLS");
                sslc.init(null, trustManagers, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
            }

			// 创建http 连接
			conn = (HttpURLConnection) new URL(builder.toString()).openConnection();
			conn.setConnectTimeout(mClient.getConnectionTimeOut());
			conn.setReadTimeout(mClient.getSoTimeOut());
			conn.setRequestMethod(mClient.requestMethod);

            //如果存在post/put/delete 提交的数据则先设置Content-Length 和Content-Type
            if (mClient.multiParam != null && content != null && (CCHttpClient.HTTP_POST.equals(mClient.requestMethod)
                    || CCHttpClient.HTTP_PUT.equals(mClient.requestMethod)
                    || CCHttpClient.HTTP_DELETE.equals(mClient.requestMethod))) {
                mClient.setHeader("Content-Length", String.valueOf(content.length));
                if (!mClient.getHeaders().containsKey("Content-Type")) {
                    if (mClient.multiParam.getContentType().equals("application/x-www-form-urlencoded"))
                        mClient.setHeader("Content-Type", mClient.multiParam.getContentType()+";charset="+mClient.getCharset());
                    else mClient.setHeader("Content-Type", mClient.multiParam.getContentType());
                }
            }

            //设置在请求之前需要添加的头
            for (final Entry<String, List<String>> entry : mClient.getHeaders().entrySet()) {
                final String name = entry.getKey();
                final List<String> values = entry.getValue();
                if (values != null) {
                    for (final String value : values) {
                        conn.addRequestProperty(name, value);
                    }
                }
            }

			if (mClient.multiParam != null && content != null && (CCHttpClient.HTTP_POST.equals(mClient.requestMethod)
					|| CCHttpClient.HTTP_PUT.equals(mClient.requestMethod)
					|| CCHttpClient.HTTP_DELETE.equals(mClient.requestMethod))) {
                conn.setDoOutput(true);
				final OutputStream os = conn.getOutputStream();
				ByteArrayInputStream bais = new ByteArrayInputStream(content);
				byte[] bts = new byte[1024 < bais.available() ?  1024 : bais.available()];
				int length = 0;
				int totoalLength = content.length, currentLength = 0;
				while ((length = bais.read(bts)) != -1) {
					if (isCancel) {
                        httpException.setType(CCHttpExceptionType.INTRRUPT_EXCEPTION);
                        httpException.setStatusMessage(httpException.getType().name());
		        		bais.close();
                        if (requestCallback != null) requestCallback.onFail(httpException);
		        		return null;
		        	}
		        	currentLength += length;
		        	if (requestCallback != null) requestCallback.onProgress(totoalLength, currentLength);
		        	os.write(bts);
		        	bts = new byte[1024 < bais.available() ? 1024 : bais.available()];
				}
				os.flush();
				bais.close();
			}

            CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
            info = new CCHttpResponseInfo(conn);
			return new CCHttpResponse(info, null);
		} catch (NoSuchAlgorithmException | IOException | KeyManagementException e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "execute", e.getMessage());
            info = new CCHttpResponseInfo(conn);
            CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
            if (e instanceof SocketTimeoutException) {
                httpException.setType(CCHttpExceptionType.TIMEOUT_EXCEPTION);
            } else if (e instanceof NoSuchAlgorithmException || e instanceof KeyManagementException) {
                httpException.setType(CCHttpExceptionType.HTTPS_EXCEPTION);
            } else {
                httpException.setType(CCHttpExceptionType.READ_EXCEPTION);
            }
            httpException.setStatusCode(info.getResponseCode());
            httpException.setStatusMessage(info.getResponseMessage());
            httpException.setRequestUrl(getRequestUrl());
            httpException.setException(e);
            if (requestCallback != null) requestCallback.onFail(httpException);
			return new CCHttpResponse(info, e);
		}
	}

	@Override
	public void update(String category, Object... objs) {
		// TODO Auto-generated method stub
        if (Integer.parseInt(objs[0].toString()) == CCSDKConstants.NET_NONE) {
            httpException.setType(CCHttpExceptionType.NETERROR_EXCEPTION);
            httpException.setStatusMessage(httpException.getType().name());
            requestCallback.onFail(httpException);
        }
		CCObservable.newInstance().unRegisterObserver(CCSDKConstants.NET_STATUS_CHANGED, this);
	}

}
