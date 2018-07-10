package cc.sdkutil.controller.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 15-1-6.
 * 用于加密数据. <br>
 */
public class CCEncryptUtil {

    /**
     * 将目标字符串转换为MD5 字符串
     * @param encryptData
     * @return
     */
    public static String MD5(String encryptData) {
        String strMD5 = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(encryptData.getBytes());
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            strMD5 = new String(str).toUpperCase(Locale.CHINA);
        } catch (NoSuchAlgorithmException e) {}
        return strMD5;
    }

    /**
     * HMAC_SHA1 加密
     * @param encryptKey           加密密匙
     * @param encryptData          待加密数据
     * @param charsetName          编码
     * @return
     */
    public static byte[] HMAC_SHA1(String encryptKey, String encryptData, String charsetName) {
        try {
            byte[] keyData = encryptKey.getBytes(charsetName);
            SecretKey secretKey = new SecretKeySpec(keyData, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] text = encryptData.getBytes(charsetName);
            return mac.doFinal(text);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            CCLogUtil.d(CCToolUtil.class.getAnnotation(CCDebug.class).debug(),
                    CCToolUtil.class, "HMAC_SHA1", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Base64 + HMAC_SHA1 加密
     * @param encryptKey    加密密匙
     * @param encryptData   待加密数据
     * @param charsetName   加密编码
     * @return
     */
    public static String BASE_HMAC_SHA1(String encryptKey, String encryptData, String charsetName) {
        byte[] hamc_sha1_bytes = HMAC_SHA1(encryptKey, encryptData, charsetName);
        return Base64.encodeToString(hamc_sha1_bytes, Base64.DEFAULT);
    }
}
