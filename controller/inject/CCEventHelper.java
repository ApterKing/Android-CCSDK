package cc.sdkutil.controller.inject;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.inject.CCInjectEvent;
import cc.sdkutil.model.inject.CCInjectEventListener;

/**
 * Created by wangcong on 15-5-22.
 * 处理监听与方法之间的匹配关系
 */
@CCDebug
public class CCEventHelper {

    private CCEventHandler mHandler;

    public CCEventHelper() {
        mHandler = new CCEventHandler();
    }

    /**
     * 将方法与对应的监听事件匹配
     * @param obj           方法所属的对象
     * @param t             必须是Activity或者View用于findView
     * @param annotation    {@link CCInjectEvent}
     * @param method
     * @param <T>
     */
    public <T> void bindEvent(Object obj, T t, CCInjectEvent annotation, Method method) {
        int[] viewIds = annotation.value();
        String setterValue = annotation.setterValue().equals("default") ?
                "set" + annotation.clazz().getSimpleName() : annotation.setterValue();
        String methodValue = annotation.methodValue();
        /** 如果未设置所调用的方法，自动查找匹配参数的方法 */
        if (methodValue.equals("default")) {
            Method[] iMethods = annotation.clazz().getDeclaredMethods();
            for (Method iMethod : iMethods) {
                if (checkMethodRPTypes(iMethod, method)) {
                    methodValue = iMethod.getName();
                    break;
                }
            }
        }
        if (methodValue.equals("default")) return;
        for (int viewId : viewIds) {
            View view = t instanceof Activity ? ((Activity)t).findViewById(viewId) :
                    ( t instanceof View ? ((View)t).findViewById(viewId) : null);
            /** 通过动态代理绑定所需执行的方法 */
            CCInjectEventListener listener = (CCInjectEventListener) mHandler.bindMethod(obj, methodValue, method);
            if (view != null) {
                try {
                    Method setterMethod = view.getClass().getMethod(setterValue,
                            annotation.clazz());
                    setterMethod.setAccessible(true);
                    setterMethod.invoke(view, listener);
                } catch (NoSuchMethodException e) {
                    CCLogUtil.d(CCEventHelper.class.getAnnotation(CCDebug.class).debug(),
                            CCEventHelper.class, "bind---fail---" + e.getMessage());
                } catch (InvocationTargetException e) {
                    CCLogUtil.d(CCEventHelper.class.getAnnotation(CCDebug.class).debug(),
                            CCEventHelper.class, "bind---fail---" + e.getMessage());
                } catch (IllegalAccessException e) {
                    CCLogUtil.d(CCEventHelper.class.getAnnotation(CCDebug.class).debug(),
                            CCEventHelper.class, "bind---fail---" + e.getMessage());
                }
            }
        }
    }

    /**
     * 检测两个方法中所带的参数和返回类型是否一致
     * @param iMethod     方法1
     * @param method      方法2
     * @return
     */
    private boolean checkMethodRPTypes(Method iMethod, Method method) {
        Class<?> iMethodReturnType = iMethod.getReturnType();
        Class<?> methodReturnType = method.getReturnType();
        if (!iMethodReturnType.getName().equals(methodReturnType.getName())) {
            return false;
        }
        Class<?>[] iMethodParameters = iMethod.getParameterTypes();
        Class<?>[] methodParameters = method.getParameterTypes();
        if (iMethodParameters.length != methodParameters.length) return false;
        for (int i = 0; i < iMethodParameters.length; i++) {
            if (!iMethodParameters[i].getName().equals(methodParameters[i].getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 动态代理类
     */
    @CCDebug
    private class CCEventHandler implements InvocationHandler {

        private WeakReference<Object> refTarget;
        private WeakReference<Object> refProxy;
        private ArrayMap<String, Method> methodMap = new ArrayMap<>();

        /**
         * 绑定方法，并且得到代理对象
         * @param target
         * @param methodName
         * @param method
         * @return
         */
        public Object bindMethod(Object target, String methodName, Method method) {
            if (refTarget == null || (refTarget != null) && refTarget.get() != target) {
                refTarget = new WeakReference<Object>(target);
            }
            methodMap.put(methodName, method);
            if (refProxy == null || (refProxy != null && refProxy.get() == null)) {
                Object proxy = Proxy.newProxyInstance(target.getClass().getClassLoader(),
                        new Class[]{CCInjectEventListener.class}, this);
                refProxy = new WeakReference<Object>(proxy);
            }
            return refProxy.get();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            CCLogUtil.d(CCEventHelper.class.getAnnotation(CCDebug.class).debug(),
                    CCEventHandler.class, "invoke---start");
            String methodName = method.getName();
            Method tMethod = methodMap.get(methodName);
            if (tMethod == null) {
                CCLogUtil.d(CCEventHelper.class.getAnnotation(CCDebug.class).debug(),
                        CCEventHandler.class, "invoke---fail---not found target Method");
                return null;
            }
            tMethod.setAccessible(true);
            Object result = tMethod.invoke(refTarget.get(), args);
            CCLogUtil.d(CCEventHelper.class.getAnnotation(CCDebug.class).debug(),
                    CCEventHandler.class, "invoke---end");
            return result;
        }
    }
}
