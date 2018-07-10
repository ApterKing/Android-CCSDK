package cc.sdkutil.model.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangcong on 15-5-21.
 * 用于注解View监听事件
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CCInjectEvent {

    int[] value() default {};

    /** 需要监听的方法 */
    Class<?> clazz();

    /** View中设置监听方法的名，默认为set + clazz.getSimpleName
     *  如：设置的是{@link android.view.View.OnClickListener}，设置方法默认对应为 setOnclickListener
     */
    String setterValue() default "default";

    /** 属于监听中的方法名，如果不设置此值，则自动匹配与所传参数和返回类型对应的方法，如果为查找到匹配则不做任何操作
     *  如：设置的是{@link android.widget.AdapterView.OnItemSelectedListener} 中有两个方法
     *  1：onItemSelected  2：onNothingSelected;
     *  如果方法如下
     *  void test(AdapterView<?> parent, View view, int position, long id) 此时调用的默认方法为 onItemSelected
     *  如果方法如下
     *  void test(AdapterView<?> parent) 此时调用的默认方法为 onNothingSelected
     */
    String methodValue() default "default";

}
