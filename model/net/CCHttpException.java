package cc.sdkutil.model.net;

/**
 * Created by wangcong on 15-1-4.
 * http请求错误exception. <br>
 */
public class CCHttpException extends Exception {

	private static final long serialVersionUID = 1L;

	private final static String TAG = "CCHttpException";

    //发生Exception错误类型
    private CCHttpExceptionType type;
    //响应状态码
    private int statusCode;
    //响应消息
    private String statusMessage;
    //请求url
    private String requestUrl;
    //exception
    private Exception exception;

    public CCHttpException() {
        super();
    }

    public static String getTag() {
        return TAG;
    }

    public CCHttpExceptionType getType() {
        return type;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public Exception getException() {
        return exception;
    }

    public void setType(CCHttpExceptionType type) {
        this.type = type;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String getMessage() {
        return TAG + " -> [ type: " + type.toString() +  "   statusCode: "+statusCode + "   message: " + statusMessage + "\n   requestUrl: "
                + requestUrl + "\n   exception: " + ( exception == null ? "" : exception.getMessage()) + "]";
    }

    @Override
    public String getLocalizedMessage() {
        return TAG + " -> [ type: " + type.toString() +  "   statusCode: "+statusCode + "   message: " + statusMessage + "\n   requestUrl: "
                + requestUrl + "\n   exception: " + (exception == null ? "" : exception.getMessage()) + "]";
    }

}
