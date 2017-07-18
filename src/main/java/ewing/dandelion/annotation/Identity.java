package ewing.dandelion.annotation;

import java.lang.annotation.*;

/**
 * 标记为ID属性，一个类可以有多个ID属性。
 *
 * @author Ewing
 * @since 2017-05-22
 **/
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Identity {
    // 默认自动生成ID
    boolean generate() default true;
}
