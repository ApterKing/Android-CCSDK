package cc.sdkutil.model.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangcong on 15-5-21.
 * 用于注解Android res下的资源，如：xml，view，drawable，bitmap等
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CCInjectRes {

    /** 资源id */
    int value() default -1;

    /** 默认注解类型为view */
    CCInjectResType type() default CCInjectResType.CCResView;
}
