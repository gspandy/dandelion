package com.ewing.dandelion.generation;

import com.ewing.dandelion.DaoException;

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
     * 从对象实例中获取指定属性的值。
     *
     * @param property 指定的属性。
     * @param object   对象实例。
     * @return 属性的值。
     */
    public static Object getValue(Property property, Object object) {
        try {
            return property.getReadMethod().invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new DaoException("Failed to read object property value.", e);
        }
    }

    /**
     * 从对象实例中获取指定属性的值。
     */
    public static List<Object> getValues(List<Property> properties, Object id) {
        List<Object> params = new ArrayList<>(properties.size());
        for (Property property : properties)
            params.add(getValue(property, id));
        return params;
    }

    /**
     * 判断该属性是否为积极的。
     */
    public static boolean isPositive(Property property, Object object) {
        Object propertyValue = getValue(property, object);
        if (propertyValue == null) return false;
        Class clazz = property.getType();
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

}
