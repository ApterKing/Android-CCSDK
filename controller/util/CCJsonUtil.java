package cc.sdkutil.controller.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cc.sdkutil.model.inject.CCDebug;

/**
 * Created by wangcong on 14-12-26.
 * Json字符串封装与解析. <br> 
 */
@CCDebug
public class CCJsonUtil {

	/**
	 * 解析多层json数据  json + 递归, 如果解析的是JSonObject字符串则返回 {@link Map}，
     * 如果为JSonArray字符串则返回{@link List}, 其他返回本身
	 * @param source
	 * @return
	 */
	public static Object jsonParse(Object source) {
		try {
			if (source instanceof String) {
                String jsonString = (String) source;
                JSONObject jsonObject = new JSONObject(jsonString);
                final Map<String, Object> map = new HashMap<>();
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    map.put(key, jsonParse(jsonObject.opt(key)));
                }
                return map;
			} else if (source instanceof JSONArray) {
				final List<Object> list = new ArrayList<>();
                JSONArray jsonArray = (JSONArray) source;
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonParse(jsonArray.opt(i)));
                }
				return list;
			} else if (source instanceof JSONObject) {
                return jsonParse(source.toString());
            } else {
                return source;
            }
		} catch (JSONException e) {
            return source;
		}
	}

    /**
     * 将对象封装为Json对象，并返回字符串，否则返回封装错误exception描述
     * @param objs
     * @return
     */
    public static String jsonEnclose(Object... objs) {
        Object jsonObject = jsonWrap(objs);
        return jsonObject.toString();
    }

	/**
	 * 将对象封装为Json对象，如果不能封装为Json对象则将原参数返回
	 * @param objs
     * @see JSONArray
     * @see JSONObject
     * @see JSONException
	 * @return
	 */
	private static Object jsonWrap(Object... objs) {
		if (objs == null) return new JSONException("objs should not be null");
        try {
            int length = objs.length;
            if (length == 1) {
                Object obj = objs[0];
                if (obj instanceof Map) {                                      //map
                    return new JSONObject((Map<?, ?>) obj);
                } else if (obj instanceof Collection) {                        //Collection
                    return  new JSONArray((Collection<?>) obj);
                } else if (obj instanceof JSONTokener) {                       //JsonTokener
                    try {
                        return new JSONObject((JSONTokener) obj);
                    } catch (JSONException eo) {
                        try {
                            return new JSONArray((JSONTokener) obj);
                        } catch (JSONException ea) {
                            throw new JSONException(eo.getMessage() + "--" + ea.getMessage());
                        }
                    }
                } else if (obj instanceof String) {                            //String
                    try {
                        return new JSONObject((String) obj);
                    } catch (JSONException eo) {
                        try {
                            return new JSONArray((String) obj);
                        } catch (JSONException ea) {
                            throw new JSONException(eo.getMessage() + "--" + ea.getMessage());
                        }
                    }
                } else {
                    throw new JSONException("objs can not be enclose as JsonObject");
                }
            } else {
                Object obj = objs[0];
                if (!(obj instanceof JSONObject) && !(objs[1] instanceof String)) return objs;
                String[] subObjs = new String[objs.length - 1];
                System.arraycopy(objs, 1, subObjs, 0, objs.length - 1);
                try {
                    return new JSONObject((JSONObject) obj, subObjs);
                } catch (JSONException e) {
                    throw new JSONException(e.getMessage());
                }
            }
        } catch (JSONException e) {
            CCLogUtil.d(CCJsonUtil.class.getAnnotation(CCDebug.class).debug(),
                    CCJsonUtil.class, "jsonWrap -- inner", e.getMessage());
            return e;
        }
	}
	
}
