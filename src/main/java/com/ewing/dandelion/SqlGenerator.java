package com.ewing.dandelion;

import com.ewing.dandelion.annotation.Identity;
import com.ewing.dandelion.annotation.Temporary;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 基于对象类型和属性生成Sql语句。
 *
 * @author Ewing
 * @since 2017-03-06
 **/
public class SqlGenerator {

    /**
     * 是否启用下划线风格。
     */
    private static final boolean UNDERSCORE = false;

    /**
     * 转换对象与属性命名规则。
     */
    protected static String convertName(String name) {
        if (UNDERSCORE) {
            StringBuilder result = new StringBuilder();
            result.append(name.substring(0, 1).toLowerCase(Locale.US));
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                String slc = s.toLowerCase(Locale.US);
                if (!s.equals(slc)) {
                    result.append("_").append(slc);
                } else {
                    result.append(s);
                }
            }
            return result.toString();
        } else {
            return name;
        }
    }

    /**
     * 根据Class获取类的信息。
     */
    protected static BeanInfo getBeanInfo(Class clazz) {
        try {
            return Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new DaoException("获取类的信息失败！", e);
        }
    }

    /**
     * 根据属性名获取类的属性字段。
     */
    protected static Field getPropertyField(Class clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new DaoException("获取类的属性字段失败！", e);
        }
    }

    /**
     * 判断该属性是否为可用的。
     */
    protected static boolean isPropertyAvailable(PropertyDescriptor pd) {
        return pd.getWriteMethod() != null && pd.getReadMethod() != null;
    }

    /**
     * 判断该属性是否为临时的。
     */
    protected static boolean isPropertyTemporary(Field field) {
        return field.getAnnotation(Temporary.class) != null;
    }

    /**
     * 判断该属性是否为ID属性。
     */
    protected static boolean isPropertyIdentity(Field field) {
        return field.getAnnotation(Identity.class) != null;
    }

    /**
     * 判断该属性是否为积极的。
     */
    protected static boolean isPropertyPositive(PropertyDescriptor pd, Object object) {
        Object propertyValue;
        try {
            propertyValue = pd.getReadMethod().invoke(object);
        } catch (ReflectiveOperationException e) {
            throw new DaoException("读取对象属性值失败！", e);
        }
        if (propertyValue == null) return false;
        Class clazz = pd.getPropertyType();
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
    protected static boolean identityResolver(Field field, PropertyDescriptor pd, Object object) {
        if (String.class.equals(field.getType())) {
            Identity identity = field.getAnnotation(Identity.class);
            if (identity != null) {
                if (identity.generate())
                    try {
                        pd.getWriteMethod().invoke(object, GlobalIdWorker.nextString());
                    } catch (ReflectiveOperationException e) {
                        throw new DaoException("对象ID属性赋值失败！", e);
                    }
                return true;
            }
        }
        return false;
    }

    /**
     * 生成与Class对应的结果列。
     */
    public static String getResultColumns(Class<?> clazz) {
        StringBuilder columns = new StringBuilder();
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd))
                continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field))
                continue;
            // 添加属性到结果列
            if (hasOne)
                columns.append(", ");
            else
                hasOne = true;
            columns.append(convertName(name));
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        return columns.toString();
    }

    /**
     * 生成与配置类的属性对应的结果列。
     */
    public static String getColumnsByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder columns = new StringBuilder();
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 添加属性到结果列
            if (isPropertyPositive(pd, config) == positive) {
                if (hasOne)
                    columns.append(", ");
                else
                    hasOne = true;
                columns.append(convertName(name));
            }
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        return columns.toString();
    }

    /**
     * 生成与配置类的积极属性对应的结果列。
     */
    public static String getResultColumnsPositive(Object config) {
        return getColumnsByConfig(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的结果列。
     */
    public static String getResultColumnsNegative(Object config) {
        return getColumnsByConfig(config, false);
    }

    /**
     * 生成与实体类对应的Insert语句。
     */
    public static String getInsertValues(Object object) {
        Class clazz = object.getClass();
        StringBuilder insert = new StringBuilder("INSERT INTO ")
                .append(convertName(object.getClass().getSimpleName())).append(" (");
        StringBuilder values = new StringBuilder(") VALUES (");
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 处理ID
            identityResolver(field, pd, object);
            // 添加属性到插入列表
            if (hasOne) {
                insert.append(", ");
                values.append(", ");
            } else {
                hasOne = true;
            }
            insert.append(convertName(pd.getName()));
            values.append(":").append(pd.getName());
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        return insert.append(values).append(")").toString();
    }

    /**
     * 生成与配置类对应的Insert语句。
     */
    public static String getInsertValuesByConfig(Object object, Object config, boolean positive) {
        if (object == null || config == null || !object.getClass().equals(config.getClass()))
            throw new DaoException("实例对象或配置对象为空或类型不匹配！");
        Class clazz = config.getClass();
        StringBuilder insert = new StringBuilder("INSERT INTO ")
                .append(convertName(clazz.getSimpleName())).append(" (");
        StringBuilder values = new StringBuilder(") VALUES (");
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 处理ID
            boolean isIdentity = identityResolver(field, pd, object);
            // 添加属性到插入列表
            if (isIdentity || isPropertyPositive(pd, config) == positive) {
                if (hasOne) {
                    insert.append(", ");
                    values.append(", ");
                } else {
                    hasOne = true;
                }
                insert.append(convertName(pd.getName()));
                values.append(":").append(pd.getName());
            }
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        return insert.append(values).append(")").toString();
    }

    /**
     * 生成与配置类积极属性对应的Insert语句。
     */
    public static String getInsertPositiveValues(Object object, Object config) {
        return getInsertValuesByConfig(object, config, true);
    }

    /**
     * 生成与配置类积极属性对应的Insert语句。
     */
    public static String getInsertNegativeValues(Object object, Object config) {
        return getInsertValuesByConfig(object, config, false);
    }

    /**
     * 生成与Class对应的Delete语句。
     */
    public static String getDeleteWhereTrue(Class<?> clazz) {
        return new StringBuilder("DELETE FROM ").append(convertName(clazz.getSimpleName()))
                .append(" WHERE 1=1").toString();
    }

    /**
     * 生成与Class对应的Delete语句带ID条件。
     */
    public static String getDeleteWhereIdEquals(Class<?> clazz, boolean isNamed) {
        StringBuilder sqlDelete = new StringBuilder("DELETE FROM ")
                .append(convertName(clazz.getSimpleName()));
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        StringBuilder idBuilder = new StringBuilder();
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 添加ID属性到查询条件
            if (isPropertyIdentity(field)) {
                if (hasOne)
                    idBuilder.append(" AND ");
                else
                    hasOne = true;
                idBuilder.append(convertName(name)).append(" = ");
                if (isNamed)
                    idBuilder.append(":").append(name);
                else
                    idBuilder.append("?");
            }
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlDelete.append(" WHERE ").append(idBuilder).toString();
    }

    /**
     * 生成与配置类的属性对应的Select语句带ID条件。
     */
    public static String getSelectBodyByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder sqlSelect = new StringBuilder("SELECT ");
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        StringBuilder idBuilder = new StringBuilder();
        boolean hasOne = false;
        boolean hasIdentity = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 添加ID属性到查询条件
            if (isPropertyIdentity(field)) {
                if (hasIdentity) {
                    idBuilder.append(" AND ");
                } else {
                    hasIdentity = true;
                }
                idBuilder.append(convertName(name)).append(" = ?");
            }
            // 添加属性到查询结果
            if (isPropertyPositive(pd, config) == positive) {
                if (hasOne)
                    sqlSelect.append(", ");
                else
                    hasOne = true;
                sqlSelect.append(convertName(name));
            }
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        sqlSelect.append(" FROM ").append(convertName(clazz.getSimpleName()));
        if (!hasIdentity)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlSelect.append(" WHERE ").append(idBuilder).toString();
    }

    /**
     * 生成与Class对应的Select主体。
     */
    public static String getSelectWhereTrue(Class<?> clazz) {
        return "SELECT " + getResultColumns(clazz) + " FROM " +
                convertName(clazz.getSimpleName()) + " WHERE 1=1";
    }

    /**
     * 生成与配置类的积极属性对应的Select主体。
     */
    public static String getSelectPositiveWhereTrue(Object config) {
        return "SELECT " + getResultColumnsPositive(config) + " FROM " +
                convertName(config.getClass().getSimpleName()) + " WHERE 1=1";
    }

    /**
     * 生成与配置类的消极属性对应的Select主体。
     */
    public static String getSelectNegativeWhereTrue(Object config) {
        return "SELECT " + getResultColumnsNegative(config) + " FROM " +
                convertName(config.getClass().getSimpleName()) + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Select语句带ID条件。
     */
    public static String getSelectWhereIdEquals(Class<?> clazz) {
        StringBuilder sqlSelect = new StringBuilder("SELECT ");
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        StringBuilder idBuilder = new StringBuilder();
        boolean hasOne = false;
        boolean hasIdentity = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 添加ID属性到查询条件
            if (isPropertyIdentity(field)) {
                if (hasIdentity) {
                    idBuilder.append(" AND ").append(convertName(name)).append(" = ?");
                } else {
                    idBuilder.append(convertName(name)).append(" = ?");
                    hasIdentity = true;
                }
            }
            // 添加属性到查询结果
            if (hasOne)
                sqlSelect.append(", ");
            else
                hasOne = true;
            sqlSelect.append(convertName(name));
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        sqlSelect.append(" FROM ").append(convertName(clazz.getSimpleName()));
        if (!hasIdentity)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlSelect.append(" WHERE ").append(idBuilder).toString();
    }

    /**
     * 生成与配置类的积极属性对应的Select语句带ID条件。
     */
    public static String getSelectPositiveWhereIdEquals(Object config) {
        return getSelectBodyByConfig(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的Select语句带ID条件。
     */
    public static String getSelectNegativeWhereIdEquals(Object config) {
        return getSelectBodyByConfig(config, false);
    }

    /**
     * 生成与配置类的属性对应的Update语句。
     */
    public static String getUpdateBodyByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder sqlUpdate = new StringBuilder("UPDATE ")
                .append(convertName(clazz.getSimpleName())).append(" SET ");
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        StringBuilder idBuilder = new StringBuilder();
        boolean hasOne = false;
        boolean hasIdentity = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 添加ID到查询条件
            if (isPropertyIdentity(field)) {
                if (hasIdentity) {
                    idBuilder.append(" AND ");
                } else {
                    hasIdentity = true;
                }
                idBuilder.append(convertName(name)).append(" = :").append(name);
                // 添加属性到更新列表
            } else if (isPropertyPositive(pd, config) == positive) {
                if (hasOne)
                    sqlUpdate.append(", ");
                else
                    hasOne = true;
                sqlUpdate.append(convertName(name)).append(" = :").append(name);
            }
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        if (!hasIdentity)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlUpdate.append(" WHERE ").append(idBuilder).toString();
    }

    /**
     * 生成与Class对应的Update语句。
     */
    public static String getUpdateWhereIdEquals(Class<?> clazz) {
        StringBuilder sqlUpdate = new StringBuilder("UPDATE ")
                .append(convertName(clazz.getSimpleName())).append(" SET ");
        PropertyDescriptor[] pds = getBeanInfo(clazz).getPropertyDescriptors();
        StringBuilder idBuilder = new StringBuilder();
        boolean hasOne = false;
        boolean hasIdentity = false;
        for (PropertyDescriptor pd : pds) {
            // 需要可用的属性
            if (!isPropertyAvailable(pd)) continue;
            String name = pd.getName();
            Field field = getPropertyField(clazz, name);
            // 排除临时的属性
            if (isPropertyTemporary(field)) continue;
            // 添加ID到查询条件
            if (isPropertyIdentity(field)) {
                if (hasIdentity) {
                    idBuilder.append(" AND ").append(convertName(name)).append(" = :").append(name);
                } else {
                    idBuilder.append(convertName(name)).append(" = :").append(name);
                    hasIdentity = true;
                }
                // 添加属性到更新列表
            } else {
                if (hasOne)
                    sqlUpdate.append(", ");
                else
                    hasOne = true;
                sqlUpdate.append(convertName(name)).append(" = :").append(name);
            }
        }
        // 检查并拼装SQL语句
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现可用的属性！");
        if (!hasIdentity)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlUpdate.append(" WHERE ").append(idBuilder).toString();
    }

    /**
     * 生成与配置类的积极属性对应的Update语句。
     */
    public static String getUpdatePositiveWhereIdEquals(Object config) {
        return getUpdateBodyByConfig(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的Update语句。
     */
    public static String getUpdateNegativeWhereIdEquals(Object config) {
        return getUpdateBodyByConfig(config, false);
    }
}
