package com.ewing.dandelion.generation;

import com.ewing.dandelion.DaoException;
import com.ewing.dandelion.annotation.Identity;
import com.ewing.dandelion.annotation.Temporary;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 实体类型及属性信息，该类是只读的。
 */
public class EntityInfo {

    private final Class entityClass;

    private final BeanInfo beanInfo;

    private final List<Field> fields = new CopyOnWriteArrayList<>();

    private final List<Field> identityFields = new CopyOnWriteArrayList<>();

    private final List<Field> normalFields = new CopyOnWriteArrayList<>();

    private final List<PropertyDescriptor> properties = new CopyOnWriteArrayList<>();

    private final List<PropertyDescriptor> identityProperties = new CopyOnWriteArrayList<>();

    private final List<PropertyDescriptor> normalProperties = new CopyOnWriteArrayList<>();

    public EntityInfo(Class entityClass) {
        this.entityClass = entityClass;
        try {
            this.beanInfo = Introspector.getBeanInfo(entityClass);
        } catch (IntrospectionException e) {
            throw new DaoException("获取类" + entityClass.getName() + "的信息失败！", e);
        }
        PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : properties) {
            // 需要可用的属性
            if (property.getWriteMethod() == null || property.getReadMethod() == null)
                continue;
            Field field;
            try {
                field = entityClass.getDeclaredField(property.getName());
            } catch (NoSuchFieldException e) {
                throw new DaoException("获取属性" + property.getName() + "的字段失败！", e);
            }
            if (field.getAnnotation(Temporary.class) != null)
                continue;
            fields.add(field);
            this.properties.add(property);
            if (field.getAnnotation(Identity.class) != null) {
                identityFields.add(field);
                identityProperties.add(property);
            } else {
                normalFields.add(field);
                normalProperties.add(property);
            }
        }
        if (fields.size() == 0)
            throw new DaoException("类" + entityClass.getName() + "中没有可用的属性字段！");
    }

    /**
     * 获取实体类型。
     */
    public Class getEntityClass() {
        return entityClass;
    }

    /**
     * 获取实体类型的BeanInfo。
     */
    public BeanInfo getBeanInfo() {
        return beanInfo;
    }

    /**
     * 获取实体中可用的字段。
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * 获取实体中的ID字段。
     */
    public List<Field> getIdentityFields() {
        return identityFields;
    }

    /**
     * 获取实体的普通字段。
     */
    public List<Field> getNormalFields() {
        return normalFields;
    }

    /**
     * 获取实体中可用的属性。
     */
    public List<PropertyDescriptor> getProperties() {
        return properties;
    }

    /**
     * 获取实体中的ID属性。
     */
    public List<PropertyDescriptor> getIdentityProperties() {
        return identityProperties;
    }

    /**
     * 获取实体中的普通属性。
     */
    public List<PropertyDescriptor> getNormalProperties() {
        return normalProperties;
    }
}
