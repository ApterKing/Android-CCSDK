package cc.sdkutil.model.net;

/**
 * Created by wangcong on 15-1-4.
 * http请求错误反馈响应. <br>
 */
public enum  CCHttpExceptionType {
    NETERROR_EXCEPTION("网络未连接"),
    READ_EXCEPTION("获取数据出错"),
    HTTPS_EXCEPTION("https 请求错误"),
    TIMEOUT_EXCEPTION("连接超时"),
    ERROR_EXCEPTION(">=400 访问服务器出错"),
    INTRRUPT_EXCEPTION("用户取消了请求操作");

    private String value;
    CCHttpExceptionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
