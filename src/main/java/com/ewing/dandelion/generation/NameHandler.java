package com.ewing.dandelion.generation;

import com.ewing.dandelion.annotation.SqlName;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * 处理对象在Sql中的名称。
 */
public class NameHandler {

    /**
     * 是否启用下划线风格。
     */
    private final boolean underscore;

    /**
     * 构造方法。
     */
    public NameHandler() {
        this.underscore = false;
    }

    /**
     * 带参数的构造方法。
     */
    public NameHandler(boolean underscore) {
        this.underscore = underscore;
    }

    /**
     * 获取实体类型在Sql中的名称。
     *
     * @param clazz 实体类型。
     * @return Sql中的名称。
     */
    public String getSqlName(Class clazz) {
        SqlName sqlName = (SqlName) clazz.getAnnotation(SqlName.class);
        if (sqlName == null) {
            return convertName(clazz.getSimpleName());
        } else {
            return sqlName.value();
        }
    }

    /**
     * 获取实体字段在Sql中的名称。
     *
     * @param field 实体字段。
     * @return Sql中的名称。
     */
    public String getSqlName(Field field) {
        return convertName(field.getName());
    }

    /**
     * 转换对象与属性命名规则。
     */
    protected String convertName(String name) {
        if (underscore) {
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

}
