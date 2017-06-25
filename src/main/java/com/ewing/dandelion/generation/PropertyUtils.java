package com.ewing.dandelion.generation;

import com.ewing.dandelion.DaoException;
import com.ewing.dandelion.annotation.Identity;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象类型属性处理器。
 */
public class PropertyUtils {

    /**
     * 私有化构造方法。
     */
    private PropertyUtils() {
    }

    /**
     * 判断该属性是否为ID属性。
     */
    public static boolean isIdentity(Field field) {
        return field.getAnnotation(Identity.class) != null;
    }

    /**
     * 从对象实例中获取指定属性的值。
     *
     * @param property 指定的属性。
     * @param object   对象实例。
     * @return 属性的值。
     */
    public static Object getValue(PropertyDescriptor property, Object object) {
        try {
            return property.getReadMethod().invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new DaoException("读取对象属性值失败！", e);
        }
    }

    /**
     * 从对象实例中获取指定属性的值。
     */
    public static List<Object> getValues(List<PropertyDescriptor> properties, Object id) {
        List<Object> params = new ArrayList<>(properties.size());
        for (PropertyDescriptor property : properties)
            params.add(getValue(property, id));
        return params;
    }

    /**
     * 判断该属性是否为积极的。
     */
    public static boolean isPositive(PropertyDescriptor property, Object object) {
        Object propertyValue = getValue(property, object);
        if (propertyValue == null) return false;
        Class clazz = property.getPropertyType();
        if (!clazz.isPrimitive()) return true;
            // 下面虽然对基本类型提供了支持，但不建议使用基本类型。
        else if (clazz == int.class) return ((int) propertyValue) > 0;
        else if (clazz == long.class) return ((long) propertyValue) > 0;
        else if (clazz == short.class) return ((short) propertyValue) > 0;
        else if (clazz == byte.class) return ((byte) propertyValue) > 0;
        else if (clazz == char.class) return ((char) propertyValue) > 0;
        else if (clazz == double.class) return ((double) propertyValue) > 0;
        else if (clazz == float.class) return ((float) propertyValue) > 0;
        else return clazz == boolean.class && (boolean) propertyValue;
    }

    /**
     * 根据注解判断是否生成ID值。
     */
    public static void resolveIdentity(Field field, PropertyDescriptor property, Object object) {
        Identity identity = field.getAnnotation(Identity.class);
        if (identity.generate()) {
            Class type = field.getType();
            try {
                if (String.class.equals(type)) {
                    property.getWriteMethod().invoke(object, GlobalIdWorker.nextString());
                } else if (BigInteger.class.equals(type)) {
                    property.getWriteMethod().invoke(object, GlobalIdWorker.nextBigInteger());
                }
            } catch (ReflectiveOperationException e) {
                throw new DaoException("对象ID属性赋值失败！", e);
            }
        }
    }


}
