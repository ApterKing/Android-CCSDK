package cc.sdkutil.controller.util;


import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import cc.sdkutil.controller.net.CCHttpClientAsync;
import cc.sdkutil.controller.net.CCHttpResponseCallback;
import cc.sdkutil.controller.net.CCMultiParam;
import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.net.CCHttpException;
import cc.sdkutil.model.net.CCHttpExceptionType;
import cc.sdkutil.model.net.CCHttpResponseInfo;

/**
 * Created by wangcong on 15-1-4. <br>
 * 用于客户端106发送短信验证码. <br>
 */
@CCDebug
public class CCSMS109Util {

    //109 短信验证
    private final static String APP_ID = "367829780000038640";                //appid
    private final static String APP_SECRET = "42f495e351a66145e5ea4ba17e766a38";  //appsecret
    private final static String SMS_GRANT_TYPE = "client_credentials";
    private final static String SMS_REQ_ACCESSTOKEN = "https://oauth.api.189.cn/emp/oauth2/v3/access_token";  //获取access_tooken
    private final static String SMS_REQ_TOKEN = "http://api.189.cn/v2/dm/randcode/token?";                     //获取信任码
    private final static String SMS_REQ_SEND  = "http://api.189.cn/v2/dm/randcode/sendSms?";                    //发送短信

    public static interface CCSMS109Callback {

        /**
         * 短信请求发送成功之后将返回随机码
         * @param randcode
         */
        void onSuccess(String randcode);

        /**
         * 发送短信失败返回失败理由
         * @param reason
         */
        void onFail(CCHttpException e, String reason);

    }

    /**
     * 发送短信
     * @param callback
     */
    public static void sendSMS(final String phone, final CCSMS109Callback callback) {
        if (callback == null)
            throw new NullPointerException("callback can not be null");
        requestAccessToken(phone, callback);
    }

    /**
     * 获取access_token
     * @param callback
     */
    private static void requestAccessToken(final String phone, final CCSMS109Callback callback) {
        CCMultiParam param = new CCMultiParam();
        param.put("grant_type", SMS_GRANT_TYPE);
        param.put("app_id", APP_ID);
        param.put("app_secret", APP_SECRET);
        new CCHttpClientAsync().post(SMS_REQ_ACCESSTOKEN, param, new CCHttpResponseCallback() {

            @Override
            public void onSuccess(CCHttpResponseInfo info, byte[] bts) {
                CCLogUtil.d(CCSMS109Util.class.getAnnotation(CCDebug.class).debug(),
                        CCSMS109Util.class, "requestAccessToken_onSuccess", new String(bts));
                @SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) CCJsonUtil.jsonParse(new String(bts));
                if (Integer.valueOf(map.get("res_code")+"") == 0) {
                    requestToken(phone, map.get("access_token")+"", callback);
                } else {
                    CCHttpException httpException = new CCHttpException();
                    httpException.setType(CCHttpExceptionType.ERROR_EXCEPTION);
                    httpException.setStatusCode(Integer.valueOf(map.get("res_code")+""));
                    httpException.setStatusMessage(map.get("res_message")+"");
                    callback.onFail(httpException, "发送短息失败");
                }
            }

            @Override
            public void onFail(CCHttpException e, byte[] bts) {
                CCLogUtil.d(CCSMS109Util.class.getAnnotation(CCDebug.class).debug(),
                        CCSMS109Util.class, "requestAccessToken_onFail", new String(bts));
                callback.onFail(e, "发送短息失败");
            }
        });
    }

    /**
     * 获取token
     * @param callback
     */
    private static void requestToken(final String phone, final String access_token, final CCSMS109Callback callback) {
        //将请求参数加密
        final String timestamp = CCToolUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        TreeMap<String, String> param = new TreeMap<>();
        param.put("app_id", APP_ID);
        param.put("access_token", access_token);
        param.put("timestamp", timestamp);
        Iterator<Map.Entry<String, String>> iterator = param.entrySet().iterator();
        StringBuilder builder = new StringBuilder(256);
        int index = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (index > 0) builder.append("&");
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            index ++;
        }
        String sign = CCEncryptUtil.BASE_HMAC_SHA1(APP_SECRET, builder.toString(), "UTF-8");
        builder.append("&").append("sign").append("=").append(sign);
        new CCHttpClientAsync().get(SMS_REQ_TOKEN + builder.toString().replace(" ", "%20"), null, new CCHttpResponseCallback() {

            @Override
            public void onSuccess(CCHttpResponseInfo info, byte[] bts) {
                CCLogUtil.d(CCSMS109Util.class.getAnnotation(CCDebug.class).debug(),
                        CCSMS109Util.class, "requestToken_onSuccess", new String(bts));
                @SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) CCJsonUtil.jsonParse(new String(bts));
                if (Integer.parseInt(map.get("res_code")+"") == 0) {
                    requestSendSMS(phone, access_token, map.get("token") + "", callback);
                } else {
                    CCHttpException httpException = new CCHttpException();
                    httpException.setType(CCHttpExceptionType.ERROR_EXCEPTION);
                    httpException.setStatusCode(Integer.valueOf(map.get("res_code")+""));
                    httpException.setStatusMessage(map.get("res_message")+"");
                    callback.onFail(httpException, "发送短息失败");
                }
            }

            @Override
            public void onFail(CCHttpException e, byte[] bts) {
                CCLogUtil.d(CCSMS109Util.class.getAnnotation(CCDebug.class).debug(),
                        CCSMS109Util.class, "requestToken_onFail", new String(bts));
                callback.onFail(e, "发送短息失败");
            }
        });
    }

    /**
     * 发送验证码
     * @param access_token
     * @param token
     * @param phone
     */
    private static void requestSendSMS(final String phone, String access_token, String token, final CCSMS109Callback callback) {
        //将请求参数加密
        final String timestamp = CCToolUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("app_id", APP_ID);
        treeMap.put("access_token", access_token);
        treeMap.put("token", token);
        treeMap.put("phone", phone);
        final String randcode = "" + (new Random().nextInt() % 100000 + 100000);
        treeMap.put("randcode", "" + randcode);
        treeMap.put("timestamp", timestamp);
        Iterator<Map.Entry<String, String>> iterator = treeMap.entrySet().iterator();
        StringBuilder builder = new StringBuilder(256);
        int index = 0;
        while (iterator.hasNext()) {
            if (index > 0) builder.append("&");
            Map.Entry<String, String> entry = iterator.next();
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            index ++;
        }
        String sign = CCEncryptUtil.BASE_HMAC_SHA1(APP_SECRET, builder.toString(), "UTF-8");
        builder.append("&").append("sign").append("=").append(sign);
        new CCHttpClientAsync().get(SMS_REQ_SEND + builder.toString().replace(" ", "%20"), null, new CCHttpResponseCallback() {

            @Override
            public void onSuccess(CCHttpResponseInfo info, byte[] bts) {
                CCLogUtil.d(CCSMS109Util.class.getAnnotation(CCDebug.class).debug(),
                        CCSMS109Util.class, "requestSendSMS_onSuccess", new String(bts));
                @SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) CCJsonUtil.jsonParse(new String(bts));
                if (Integer.valueOf(map.get("res_code")+"") == 0) {
                    callback.onSuccess(randcode);
                } else {
                    CCHttpException httpException = new CCHttpException();
                    httpException.setType(CCHttpExceptionType.ERROR_EXCEPTION);
                    httpException.setStatusCode(Integer.valueOf(map.get("res_code")+""));
                    httpException.setStatusMessage(map.get("res_message")+"");
                    callback.onFail(httpException, "发送短息失败");
                }
            }

            @Override
            public void onFail(CCHttpException e, byte[] bts) {
                CCLogUtil.d(CCSMS109Util.class.getAnnotation(CCDebug.class).debug(),
                        CCSMS109Util.class, "requestSendSMS_onFail", new String(bts));
                callback.onFail(e, "发送短信失败");
            }
        });
    }
}
