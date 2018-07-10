package cc.sdkutil.controller.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.inject.CCDebug;

/**
 *  create by wangcong on 14-12-22. <br>
 *  一个帮助类，封装提交的多数据，支持 文字、文件、及输入流、字节数组操作. <br>
 */
@CCDebug
public final class CCMultiParam {
	
	private final String PREFIX = "--";
	private final String SUBFIX = "--";
	private final String CR_LF = "\r\n";
	private final String BOUNDARY;

	private final ConcurrentHashMap<String, String> keyvalueMap;
	private final ConcurrentHashMap<String, byte[]> byteMap;

	private String BOUNDARY_LINE;
	private String BOUNDARY_END;

	public CCMultiParam() {
		this(UUID.randomUUID().toString());
	}

    public CCMultiParam(String boundary) {
        keyvalueMap = new ConcurrentHashMap<>(16);
        byteMap = new ConcurrentHashMap<>(1);

        BOUNDARY = boundary;
        BOUNDARY_LINE = PREFIX + BOUNDARY + CR_LF;
        BOUNDARY_END = PREFIX + BOUNDARY + SUBFIX + CR_LF;
    }
	
	/**
	 * 添加字段Entity
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
        if (key == null || value == null)
            throw new NullPointerException("CCMultiParam key or value must not be null [" + key +":" + value+"]");
		keyvalueMap.put(key, value);
	}
	
	/**
	 * 添加文件Entity
	 * @param key    一般采用文件名称
	 * @param file
	 */
	public void put(String key, File file) {
		try {
            FileInputStream fis = new FileInputStream(file);
            put(key, fis);
        } catch (FileNotFoundException e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "put--file", e.getMessage());
        }
	}

	/**
	 * 添加流entity
	 * @param key   输入流名称
	 * @param is
	 */
	public void put(String key, InputStream is) {
		try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] readBts = new byte[is.available()];
            while (is.read(readBts) != -1) {
                baos.write(readBts);
                if (is.available() != 0) {
                    readBts = new byte[is.available()];
                } else {
                    break;
                }
            }
            byte[] bts = baos.toByteArray();
            baos.close();
            is.close();
            put(key, bts);
        } catch (IOException e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "put--inputstream", e.getMessage());
        }
	}

    /**
     * 添加字节数组
     * @param key  byte[]数组名称
     * @param bts
     */
	public void put(String key, byte[] bts) {
		if (bts == null) return;
		byteMap.put(key, bts);
	}
	
	/**
	 * 添加参数 
	 * @param params {@link Map}
	 */
	public <T> void put(Map<String, T> params) {
		for (Entry<String, T> entry : params.entrySet()) {
			final String name = entry.getKey();
			final T obj = entry.getValue();
			if (name == null || obj == null) continue;
			if (obj instanceof File) {
				put(name, (File)obj);
			} else if (obj instanceof InputStream) {
				put(name, (InputStream) obj);
			} else if (obj instanceof String){
				put(name, (String) obj);
			} else {
				put(name, (byte[]) obj);
			}
		}
	}
	
	/**
	 * 根据上传数据类型得到Content-Type, 默认为:application/x-www-form-urlencoded
	 * @return
	 */
	public String getContentType() {
		int flag = 0;
		if (!keyvalueMap.isEmpty()) flag += 1;
		if (!byteMap.isEmpty()) flag += 1;
		return flag > 1 ? "multipart/from-data;boundary="+BOUNDARY : "application/x-www-form-urlencoded";
	}

	/**
	 * 获取需要上传的数据 
	 * @param charSet 编码方式
	 * @return
	 */
	public byte[] toBytes(String charSet) {
        try {
            if (!keyvalueMap.isEmpty()) {
                Iterator<Entry<String, String>> iterator = keyvalueMap.entrySet().iterator();
                int index = 0;
                StringBuilder builder = new StringBuilder(256);
                while (iterator.hasNext()) {
                    Entry<String, String> entry = iterator.next();
                    if (index > 0) builder.append('&');
                    builder.append(entry.getKey() + "=" + entry.getValue());
                    index ++;
                }
                put(BOUNDARY, builder.toString().getBytes(charSet));
                keyvalueMap.clear();
            }


            if (byteMap.size() > 1) {
                StringBuilder builder = new StringBuilder(512);
                Enumeration<String> keys = byteMap.keys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();
                    String value = new String(byteMap.get(key), charSet);

                    builder.append(BOUNDARY_LINE);
                    builder.append("Content-Disposition:from-data;name=\""+ key +"\""+CR_LF);
                    if (key.equals(BOUNDARY))
                        builder.append("Content-Type:text/plain"+ CR_LF);
                    builder.append(CR_LF);
                    builder.append(value);
                    builder.append(CR_LF);
                }
                builder.append(BOUNDARY_END);
                return builder.toString().getBytes(charSet);
            } else {
                return byteMap.get(byteMap.keys().nextElement());
            }
        } catch (UnsupportedEncodingException e) {
            CCLogUtil.d(getClass().getAnnotation(CCDebug.class).debug(),
                    getClass(), "toBytes", e.getMessage());
            return new byte[0];
        }
	}
}
