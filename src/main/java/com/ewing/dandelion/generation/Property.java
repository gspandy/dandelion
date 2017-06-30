package com.ewing.dandelion.generation;

import com.ewing.dandelion.DaoException;
import com.ewing.dandelion.annotation.Identity;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 对象属性描述。
 */
public class Property {

    private Class type;

    private String name;

    private String sqlName;

    private Method readMethod;

    private Method writeMethod;

    private boolean identity;

    private boolean generate;

    /**
     * 初始化属性信息。
     */
    public Property(Class entityClass, PropertyDescriptor descriptor, boolean underscore) {
        Field field;
        try {
            field = entityClass.getDeclaredField(descriptor.getName());
        } catch (NoSuchFieldException e) {
            throw new DaoException("Field not found.", e);
        }
        this.type = field.getType();
        this.name = field.getName();
        this.readMethod = descriptor.getReadMethod();
        this.writeMethod = descriptor.getWriteMethod();
        // 是否为ID以及是否生成ID值
        Identity identity = field.getAnnotation(Identity.class);
        if (identity == null) {
            this.identity = false;
            this.generate = false;
        } else {
            this.identity = true;
            this.generate = identity.generate();
        }
        // 初始化属性在Sql中的名称
        if (underscore) {
            StringBuilder result = new StringBuilder(
                    this.name.substring(0, 1).toLowerCase());
            for (int i = 1; i < this.name.length(); i++) {
                String s = this.name.substring(i, i + 1);
                String slc = s.toLowerCase();
                if (!s.equals(slc)) {
                    result.append("_").append(slc);
                } else {
                    result.append(s);
                }
            }
            this.sqlName = result.toString().toUpperCase();
        } else {
            this.sqlName = name;
        }
    }

    public Class getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSqlName() {
        return sqlName;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }

    public boolean isIdentity() {
        return identity;
    }

    public boolean isGenerate() {
        return generate;
    }

}
