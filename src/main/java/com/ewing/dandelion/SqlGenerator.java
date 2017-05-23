package com.ewing.dandelion;

import com.ewing.dandelion.annotation.Temporary;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;
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
     * 判断是否空字符串。
     */
    protected static boolean isBlankString(String string) {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }

    /**
     * 判断该属性是否为积极的。
     */
    protected static boolean isPropertyPositive(BeanWrapper bw, PropertyDescriptor pd) {
        Object propertyValue = bw.getPropertyValue(pd.getName());
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
     * 判断该属性是否为可用的。
     */
    protected static boolean isPropertyAvailable(Class clazz, PropertyDescriptor pd) {
        try {
            if (clazz.getDeclaredField(pd.getName()).getAnnotation(Temporary.class) != null)
                return false;
        } catch (NoSuchFieldException e) {
            return false;
        }
        return pd.getWriteMethod() != null && pd.getReadMethod() != null;
    }

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
     * 生成与Class对应的结果列。
     */
    public static String getResultColumns(Class<?> clazz) {
        StringBuilder columns = new StringBuilder();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                if (hasOne) columns.append(", ");
                columns.append(convertName(pd.getName()));
                hasOne = true;
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        return columns.toString();
    }

    /**
     * 生成与配置类的属性对应的结果列。
     */
    public static String getColumnsByProperty(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder columns = new StringBuilder();
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(config);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd) && isPropertyPositive(beanWrapper, pd) == positive) {
                if (hasOne) columns.append(", ");
                columns.append(convertName(pd.getName()));
                hasOne = true;
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        return columns.toString();
    }

    /**
     * 生成与配置类的积极属性对应的结果列。
     */
    public static String getResultColumnsPositive(Object config) {
        return getColumnsByProperty(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的结果列。
     */
    public static String getResultColumnsNegative(Object config) {
        return getColumnsByProperty(config, false);
    }

    /**
     * 生成与实体类对应的Insert语句，如果ID为String类型的null值则生成ID。
     */
    public static String getInsertValues(Object object) {
        Class clazz = object.getClass();
        StringBuilder insert = new StringBuilder("INSERT INTO ")
                .append(convertName(object.getClass().getSimpleName())).append(" (");
        StringBuilder values = new StringBuilder(") VALUES (");
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        boolean findId = false;
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                String name = pd.getName();
                // 自动查找ID，如果ID为String类型的null值则生成ID。
                if (!findId && name.equalsIgnoreCase(clazz.getSimpleName() + "Id")) {
                    if (pd.getPropertyType() == String.class &&
                            isBlankString((String) beanWrapper.getPropertyValue(name))) {
                        beanWrapper.setPropertyValue(name, GlobalIdWorker.nextString());
                    }
                    findId = true; // 已找到ID。
                }
                if (hasOne) {
                    insert.append(", ");
                    values.append(", ");
                }
                insert.append(convertName(pd.getName()));
                values.append(":").append(pd.getName());
                hasOne = true;
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        return insert.append(values).append(")").toString();
    }

    /**
     * 生成与配置类对应的Insert语句，如果ID为String类型的null值则生成ID。
     */
    public static String getInsertValuesByPropertity(Object object, Object config, boolean positive) {
        if (object == null || config == null || !object.getClass().equals(config.getClass()))
            throw new DaoException("实例对象或配置对象为空或类型不匹配！");
        Class clazz = config.getClass();
        StringBuilder insert = new StringBuilder("INSERT INTO ")
                .append(convertName(clazz.getSimpleName())).append(" (");
        StringBuilder values = new StringBuilder(") VALUES (");
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(config);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        String idName = null;
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                String name = pd.getName();
                // 自动查找ID，如果ID为String类型的null值则生成ID。
                if (idName == null && name.equalsIgnoreCase(clazz.getSimpleName() + "Id")) {
                    BeanWrapper entityWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
                    if (pd.getPropertyType() == String.class && entityWrapper.getPropertyValue(name) == null) {
                        entityWrapper.setPropertyValue(name, GlobalIdWorker.nextString());
                    }
                    idName = name; // 已找到ID。
                }
                if (isPropertyPositive(beanWrapper, pd) == positive || name.equals(idName)) {
                    if (hasOne) {
                        insert.append(", ");
                        values.append(", ");
                    }
                    insert.append(convertName(pd.getName()));
                    values.append(":").append(pd.getName());
                    hasOne = true;
                }
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        return insert.append(values).append(")").toString();
    }

    /**
     * 生成与配置类积极属性对应的Insert语句。
     */
    public static String getInsertPositiveValues(Object object, Object config) {
        return getInsertValuesByPropertity(object, config, true);
    }

    /**
     * 生成与配置类积极属性对应的Insert语句。
     */
    public static String getInsertNegativeValues(Object object, Object config) {
        return getInsertValuesByPropertity(object, config, false);
    }

    /**
     * 生成与Class对应的Delete语句。
     */
    public static String getDeleteFromWhereTrue(Class<?> clazz) {
        return new StringBuilder("DELETE FROM ").append(convertName(clazz.getSimpleName()))
                .append(" WHERE 1=1").toString();
    }

    /**
     * 生成与Class对应的Delete语句带ID条件。
     */
    public static String getDeleteFromWhereIdEquals(Class<?> clazz, boolean isNamed) {
        StringBuilder sqlDelete = new StringBuilder("DELETE FROM ")
                .append(convertName(clazz.getSimpleName()));
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        String autoIdName = null;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                String name = pd.getName();
                // 自动查找ID。
                if (name.equalsIgnoreCase(clazz.getSimpleName() + "Id")) {
                    autoIdName = name;
                    break;
                }
            }
        }
        // 拼装要查询的ID条件。
        if (autoIdName == null)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlDelete.append(" WHERE ").append(convertName(autoIdName))
                .append(" = ").append(isNamed ? ":" + autoIdName : "?").toString();
    }

    /**
     * 生成与配置类的属性对应的Select语句带ID条件。
     */
    public static String getSelectBodyByProperty(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder sqlSelect = new StringBuilder("SELECT ");
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(config);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        String autoIdName = null;
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                String name = pd.getName();
                // 自动查找ID。
                if (autoIdName == null && name.equalsIgnoreCase(clazz.getSimpleName() + "Id")) {
                    autoIdName = name;
                }
                if (isPropertyPositive(beanWrapper, pd) == positive) {
                    if (hasOne) sqlSelect.append(", ");
                    sqlSelect.append(convertName(name));
                    hasOne = true;
                }
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        sqlSelect.append(" FROM ").append(convertName(clazz.getSimpleName()));
        // 拼装要查询的ID条件。
        if (autoIdName == null)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlSelect.append(" WHERE ").append(convertName(autoIdName)).append(" = ?").toString();
    }

    /**
     * 生成与Class对应的Select主体。
     */
    public static String getSelectFromWhereTrue(Class<?> clazz) {
        return "SELECT " + getResultColumns(clazz) + " FROM " +
                convertName(clazz.getSimpleName()) + " WHERE 1=1";
    }

    /**
     * 生成与配置类的积极属性对应的Select主体。
     */
    public static String getSelectPositiveFromWhereTrue(Object config) {
        return "SELECT " + getResultColumnsPositive(config) + " FROM " +
                convertName(config.getClass().getSimpleName()) + " WHERE 1=1";
    }

    /**
     * 生成与配置类的消极属性对应的Select主体。
     */
    public static String getSelectNegativeFromWhereTrue(Object config) {
        return "SELECT " + getResultColumnsNegative(config) + " FROM " +
                convertName(config.getClass().getSimpleName()) + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Select语句带ID条件。
     */
    public static String getSelectFromWhereIdEquals(Class<?> clazz) {
        StringBuilder sqlSelect = new StringBuilder("SELECT ");
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        String autoIdName = null;
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                String name = pd.getName();
                // 自动查找ID。
                if (autoIdName == null && name.equalsIgnoreCase(clazz.getSimpleName() + "Id")) {
                    autoIdName = name;
                }
                if (hasOne) sqlSelect.append(", ");
                sqlSelect.append(convertName(name));
                hasOne = true;
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        sqlSelect.append(" FROM ").append(convertName(clazz.getSimpleName()));
        // 拼装要查询的ID条件。
        if (autoIdName == null)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlSelect.append(" WHERE ").append(convertName(autoIdName)).append(" = ?").toString();
    }

    /**
     * 生成与配置类的积极属性对应的Select语句带ID条件。
     */
    public static String getSelectPositiveFromWhereIdEquals(Object config) {
        return getSelectBodyByProperty(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的Select语句带ID条件。
     */
    public static String getSelectNegativeFromWhereIdEquals(Object config) {
        return getSelectBodyByProperty(config, false);
    }

    /**
     * 生成与配置类的属性对应的Update语句。
     */
    public static String getUpdateBodyByProperty(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder sqlUpdate = new StringBuilder("UPDATE ")
                .append(convertName(clazz.getSimpleName())).append(" SET ");
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(config);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        String autoIdName = null;
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                String name = pd.getName();
                // 自动查找ID，并且不做为要更新的属性。
                if (autoIdName == null && name.equalsIgnoreCase(clazz.getSimpleName() + "Id")) {
                    autoIdName = name;
                    continue;
                }
                if (isPropertyPositive(beanWrapper, pd) == positive) {
                    if (hasOne) sqlUpdate.append(", ");
                    sqlUpdate.append(convertName(name)).append(" = :").append(name);
                    hasOne = true;
                }
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        // 拼装要更新的条件。
        if (autoIdName == null)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlUpdate.append(" WHERE ").append(convertName(autoIdName)).append(" = :").append(autoIdName).toString();
    }

    /**
     * 生成与Class对应的Update语句。
     */
    public static String getUpdateSetWhereIdEquals(Class<?> clazz) {
        StringBuilder sqlUpdate = new StringBuilder("UPDATE ")
                .append(convertName(clazz.getSimpleName())).append(" SET ");
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(clazz);
        String autoIdName = null;
        boolean hasOne = false;
        for (PropertyDescriptor pd : pds) {
            if (isPropertyAvailable(clazz, pd)) {
                String name = pd.getName();
                // 自动查找ID，并且不做为要更新的属性。
                if (autoIdName == null && name.equalsIgnoreCase(clazz.getSimpleName() + "Id")) {
                    autoIdName = name;
                } else {
                    if (hasOne) sqlUpdate.append(", ");
                    sqlUpdate.append(convertName(name)).append(" = :").append(name);
                    hasOne = true;
                }
            }
        }
        if (!hasOne)
            throw new DaoException("类" + clazz.getName() + "中没有发现任何有效的属性！");
        // 拼装要更新的条件。
        if (autoIdName == null)
            throw new DaoException("未找到类" + clazz.getName() + "的ID属性！");
        return sqlUpdate.append(" WHERE ").append(convertName(autoIdName))
                .append(" = :").append(autoIdName).toString();
    }

    /**
     * 生成与配置类的积极属性对应的Update语句。
     */
    public static String getUpdatePositiveSetWhereIdEquals(Object config) {
        return getUpdateBodyByProperty(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的Update语句。
     */
    public static String getUpdateNegativeSetWhereIdEquals(Object config) {
        return getUpdateBodyByProperty(config, false);
    }
}
