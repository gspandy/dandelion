package ewing.dandelion.annotation;

import java.lang.annotation.*;

/**
 * 该注解标记的属性在生成Sql语句时被忽略。
 *
 * @author Ewing
 * @since 2017-05-22
 **/
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Temporary {
}
