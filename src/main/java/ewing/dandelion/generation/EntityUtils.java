package ewing.dandelion.generation;

import ewing.dandelion.DaoException;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 实体类型属性处理器。
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
    public static boolean isPositive(Property property, Object entity) {
        Object value;
        try {
            value = property.getReadMethod().invoke(entity);
        } catch (ReflectiveOperationException e) {
            throw new DaoException("Failed to read entity property value.", e);
        }
        if (value == null) return false;
        Class type = property.getType();
        if (!type.isPrimitive()) return true;
            // 下面虽然对基本类型提供了支持，但不建议使用基本类型。
        else if (type == int.class) return ((int) value) > 0;
        else if (type == long.class) return ((long) value) > 0;
        else if (type == short.class) return ((short) value) > 0;
        else if (type == byte.class) return ((byte) value) > 0;
        else if (type == char.class) return ((char) value) > 0;
        else if (type == double.class) return ((double) value) > 0;
        else if (type == float.class) return ((float) value) > 0;
        else return type == boolean.class && (boolean) value;
    }

    /**
     * 根据名称查找指定类型或其父类中的字段。
     */
    public static Field getEntityField(String name, Class entityClass) {
        while (entityClass != Object.class) {
            try {
                return entityClass.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                entityClass = entityClass.getSuperclass();
            }
        }
        throw new DaoException("No such field:" + name);
    }

    /**
     * 判断实体类型是否和指定类型或其父类相同。
     */
    public static boolean isEntityOrSuper(Object entity, Class entityClass) {
        Class cls = entity.getClass();
        while (entityClass != Object.class) {
            if (entityClass == cls) {
                return true;
            } else {
                entityClass = entityClass.getSuperclass();
            }
        }
        return false;
    }

    /**
     * 从对象中获取实体ID属性的值。
     */
    public static Object[] getEntityIds(EntityInfo entityInfo, Object entity) {
        Property[] identities = entityInfo.getIdentities();
        Object[] params = new Object[identities.length];
        for (int i = 0; i < identities.length; i++) {
            try {
                params[i] = identities[i].getReadMethod().invoke(entity);
            } catch (ReflectiveOperationException e) {
                throw new DaoException("Failed to read entity property value.", e);
            }
        }
        return params;
    }

    /**
     * 批量从对象数组中获取实体ID属性的值。
     */
    public static Object[] getEntitiesIds(EntityInfo entityInfo, Object[] entities) {
        Property[] identities = entityInfo.getIdentities();
        Object[] params = new Object[entities.length * identities.length];
        int index = 0;
        for (Object entity : entities) {
            for (Property identity : identities) {
                try {
                    params[index++] = identity.getReadMethod().invoke(entity);
                } catch (ReflectiveOperationException e) {
                    throw new DaoException("Failed to read entity property value.", e);
                }
            }
        }
        return params;
    }

}
