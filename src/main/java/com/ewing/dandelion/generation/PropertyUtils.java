package com.ewing.dandelion.generation;

import com.ewing.dandelion.DaoException;

import java.util.Locale;

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
     * 属性名或实体名转换成下划线风格。
     */
    public static String underscore(String name) {
        StringBuilder result = new StringBuilder();
        result.append(name.substring(0, 1).toLowerCase(Locale.US));
        for (int i = 1; i < name.length(); ++i) {
            String s = name.substring(i, i + 1);
            String slc = s.toLowerCase(Locale.US);
            if (!s.equals(slc)) {
                result.append('_').append(slc);
            } else {
                result.append(s);
            }
        }
        return result.toString().toUpperCase(Locale.US);
    }

    /**
     * 判断该属性是否为积极的。
     */
    public static boolean isPositive(Property property, Object object) {
        Object propertyValue;
        try {
            propertyValue = property.getReadMethod().invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new DaoException("Failed to read object property value.", e);
        }
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

    /**
     * 从对象中获取实体ID属性的值。
     */
    public static Object[] getEntityIds(EntityInfo entityInfo, Object object) {
        Property[] identities = entityInfo.getIdentities();
        Object[] params = new Object[identities.length];
        for (int i = 0; i < identities.length; i++) {
            try {
                params[i] = identities[i].getReadMethod().invoke(object);
            } catch (ReflectiveOperationException e) {
                throw new DaoException("Failed to read object property value.", e);
            }
        }
        return params;
    }

    /**
     * 批量从对象数组中获取实体ID属性的值。
     */
    public static Object[] getEntitiesIds(EntityInfo entityInfo, Object[] objects) {
        Property[] identities = entityInfo.getIdentities();
        Object[] params = new Object[objects.length * identities.length];
        int index = 0;
        for (Object object : objects) {
            for (Property identity : identities) {
                try {
                    params[index++] = identity.getReadMethod().invoke(object);
                } catch (ReflectiveOperationException e) {
                    throw new DaoException("Failed to read object property value.", e);
                }
            }
        }
        return params;
    }

}
