package cc.sdkutil.model.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-1-5.
 * http请求响应类. <br>
 */
@CCDebug
public class CCHttpResponseInfo {

    private int responseCode;

    private String responseMessage;

    private String contentType;

    private String contentEncoding;

    private int contentLength;

    private URL url;

    private Map<String, List<String>> headerFields;

    private long ifModifiedSince;

    private long lastModified;

    private HttpURLConnection connection;

    public CCHttpResponseInfo(HttpURLConnection connection) {
        try {
            this.connection = connection;
            responseCode = connection.getResponseCode();
            responseMessage = connection.getResponseMessage();
            contentType = connection.getContentType();
            contentEncoding = connection.getContentEncoding();
            contentLength = connection.getContentLength();
            url = connection.getURL();
            headerFields = connection.getHeaderFields();
            ifModifiedSince = connection.getIfModifiedSince();
            lastModified = connection.getLastModified();
        } catch (IOException e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "CCHttpResponseInfo", e.getMessage());
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public int getContentLength() {
        if (contentLength == -1) {
            List<String> fields = getHeaderFields().get("Content-Length");
            contentLength = fields == null ? -1 : Integer.parseInt(fields.get(0));
        }
        return contentLength;
    }

    public URL getUrl() {
        return url;
    }

    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    public long getIfModifiedSince() {
        return ifModifiedSince;
    }

    public long getLastModified() {
        return lastModified;
    }

    /**
     * 获取编码方式
     * @return
     */
    public String getContentCharset() {
        final String contentType = getContentType();
        if (contentType == null) {
            return null;
        }
        final int i = contentType.indexOf('=');
        return i == -1 ? "UTF-8" : contentType.substring(i + 1).trim();
    }

    public HttpURLConnection getConnection() {
        return connection;
    }
}
