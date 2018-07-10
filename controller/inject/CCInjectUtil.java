package cc.sdkutil.controller.inject;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.inject.CCInjectEvent;
import cc.sdkutil.model.inject.CCInjectRes;

/**
 * @author wangcong
 * create on 14-12-19. <br>
 * 注入工具，通过该工具获取到android中定义的资源，及对控件设置监听.  <br>
 */
@CCDebug
public class CCInjectUtil {

	private CCInjectUtil() {}

    /**
     * 在Activity进行注入
     * @param activity
     */
	public static void inject(Activity activity) {
        injectResEvent(activity, activity);
	}

    /**
     * 在View进行注入
     * @param view
     */
	public static void inject(View view) {
        injectResEvent(view, view);
	}

    /**
     * 在制定对象 obj 中通过activity进行注入
     * @param obj
     * @param activity
     */
	public static void inject(Object obj, Activity activity) {
        injectResEvent(obj, activity);
	}

    /**
     * 在制定obj 中通过view进行注入
     * @param obj
     * @param view
     */
	public static void inject(Object obj, View view) {
        injectResEvent(obj, view);
	}

    /**
     * 注解资源及view监听
     * @param obj
     * @param t
     * @param <T>
     */
    private static <T> void injectResEvent(Object obj, T t) {
        injectField(obj, t);
        injectEvent(obj, t);
    }

    /**
     * 对某个对象使用 {@link CCResourceHelper} 进行注入处理
     * @param obj
     * @param t
     * @param <T>
     */
	private static <T> void injectField(Object obj, T t) {

		//通过反射机制获取到定义的属性
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
            //处理资源及监听
            if (field.isAnnotationPresent(CCInjectRes.class)) {
                CCInjectRes annRes = field.getAnnotation(CCInjectRes.class);
                CCResourceHelper.setResourceValue(obj, t, annRes, field);
            }
		}
	}

    /**
     * 注解view中的监听方法
     * @param obj
     * @param t
     * @param <T>
     */
    private static <T> void injectEvent(Object obj, T t) {
        CCEventHelper tmpEventHelper = new CCEventHelper();
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(CCInjectEvent.class)) continue;
            CCInjectEvent annEvent = method.getAnnotation(CCInjectEvent.class);
            tmpEventHelper.bindEvent(obj, t, annEvent, method);
        }
    }

}
