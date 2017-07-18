package tsai.ewing.dandelion.generation;

import tsai.ewing.dandelion.DaoException;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 对象类型属性处理器。
 */
public class EntityUtils {

    /**
     * 私有化构造方法。
     */
    private EntityUtils() {
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
        Object value;
        try {
            value = property.getReadMethod().invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new DaoException("Failed to read object property value.", e);
        }
        if (value == null) return false;
        Class clazz = property.getType();
        if (!clazz.isPrimitive()) return true;
            // 下面虽然对基本类型提供了支持，但不建议使用基本类型。
        else if (clazz == int.class) return ((int) value) > 0;
        else if (clazz == long.class) return ((long) value) > 0;
        else if (clazz == short.class) return ((short) value) > 0;
        else if (clazz == byte.class) return ((byte) value) > 0;
        else if (clazz == char.class) return ((char) value) > 0;
        else if (clazz == double.class) return ((double) value) > 0;
        else if (clazz == float.class) return ((float) value) > 0;
        else return clazz == boolean.class && (boolean) value;
    }

    /**
     * 根据名称查找指定类型或其父类中的字段。
     */
    public static Field fieldInClassOrSuper(String name, Class clazz) {
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new DaoException("No such field:" + name);
    }

    /**
     * 判断对象类型是否和指定类型或其父类相同。
     */
    public static boolean isClassOrSuper(Object object, Class clazz) {
        Class cls = object.getClass();
        while (clazz != Object.class) {
            if (clazz == cls) {
                return true;
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        return false;
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