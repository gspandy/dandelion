package com.ewing.dandelion.annotation;

import java.lang.annotation.*;

/**
 * 配置对象类型在Sql中的名称。
 *
 * @author Ewing
 * @since 2017-05-22
 **/
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlName {
    // 对象类型在Sql中的名称
    String value();
}
