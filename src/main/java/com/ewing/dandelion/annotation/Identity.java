package com.ewing.dandelion.annotation;

import java.lang.annotation.*;

/**
 * 解除属性与数据库列的关联，使其成为临时属性。
 *
 * @author Ewing
 * @since 2017-05-22
 **/
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Identity {
    boolean generate() default false;
}
