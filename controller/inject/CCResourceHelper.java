package cc.sdkutil.controller.inject;

import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.lang.reflect.Field;

import cc.sdkutil.controller.util.CCLogUtil;
import cc.sdkutil.model.inject.CCDebug;
import cc.sdkutil.model.inject.CCInjectRes;

/**
 * @author wangcong
 * create on 14-12-19. <br>
 * 资源加载工具， 通过控制反转模式获取到android中定义的相关资源
 */
@CCDebug
class CCResourceHelper {

    /**
     * 设置对应资源的值
     * @param obj           注解所在对象
     * @param t             Activity 或者 View
     * @param annotation    注解
     * @param field         注解的field
     * @param <T>
     */
    public static <T> void setResourceValue(Object obj, T t, CCInjectRes annotation, Field field) {
        if (annotation.value() == -1) return;
        Context context = t instanceof Activity ? (Context) t :
                (t instanceof View ? ((View) t).getContext() : null);
        if (context == null) return;
        Object fieldValue = null;
        switch (annotation.type()) {
            case CCResView:
                fieldValue = t instanceof Activity ? ((Activity)t).findViewById(annotation.value()) :
                        ( t instanceof View ? ((View)t).findViewById(annotation.value()) : null);
                break;
            case CCResAnimation:
                fieldValue = AnimationUtils.loadAnimation(context, annotation.value());
                break;
            case CCResAnimator:
                fieldValue = AnimatorInflater.loadAnimator(context, annotation.value());
                break;
            case CCResBoolean:
                fieldValue = context.getResources().getBoolean(annotation.value());
                break;
            case CCResColor:
                fieldValue = context.getResources().getColor(annotation.value());
                break;
            case CCResStateListColor:
                fieldValue = context.getResources().getColorStateList(annotation.value());
                break;
            case CCResDimension:
                fieldValue = context.getResources().getDimension(annotation.value());
                break;
            case CCResDimensionPixelOffset:
                fieldValue = context.getResources().getDimensionPixelOffset(annotation.value());
                break;
            case CCResDimensionPixelSize:
                fieldValue = context.getResources().getDimensionPixelSize(annotation.value());
                break;
            case CCResDrawable:
                fieldValue = context.getResources().getDrawable(annotation.value());
                break;
            case CCResBitmap:
                fieldValue = BitmapFactory.decodeResource(context.getResources(), annotation.value());
                break;
            case CCResString:
                fieldValue = context.getResources().getString(annotation.value());
                break;
            case CCResStringArray:
                fieldValue = context.getResources().getStringArray(annotation.value());
                break;
            case CCResText:
                fieldValue = context.getResources().getText(annotation.value());
                break;
            case CCResTextArray:
                fieldValue = context.getResources().getTextArray(annotation.value());
                break;
            case CCResXml:
                fieldValue = context.getResources().getXml(annotation.value());
                break;

        }
        if (fieldValue == null) return; 
        try {
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            CCLogUtil.d(CCInjectUtil.class.getAnnotation(CCDebug.class).debug(),
                    CCInjectUtil.class, "setResourceValue--fail", e.getMessage());
        }
    }
	
}
