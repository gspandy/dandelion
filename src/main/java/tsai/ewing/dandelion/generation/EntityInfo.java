package tsai.ewing.dandelion.generation;

import tsai.ewing.dandelion.DaoException;
import tsai.ewing.dandelion.annotation.SqlName;
import tsai.ewing.dandelion.annotation.Temporary;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体类型及属性信息，该类是只读的。
 */
public class EntityInfo {

    private Class entityClass;

    private String sqlName;

    private Property[] properties;

    private Property[] identities;

    /**
     * 初始化实体信息。
     */
    public EntityInfo(Class entityClass, boolean underscore) {
        this.entityClass = entityClass;
        // 获取实体在Sql语句中名称
        SqlName sqlName = (SqlName) entityClass.getAnnotation(SqlName.class);
        if (sqlName != null) {
            this.sqlName = sqlName.value();
        } else {
            String name = entityClass.getSimpleName();
            this.sqlName = underscore ? EntityUtils.underscore(name) : name;
        }
        // 初始化实体中的属性列表
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(entityClass);
        } catch (IntrospectionException e) {
            throw new DaoException("Getting Bean information failure.", e);
        }
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        List<Property> properties = new ArrayList<>(descriptors.length);
        List<Property> identities = new ArrayList<>(3);
        for (PropertyDescriptor descriptor : descriptors) {
            // 需要可用的属性
            if (descriptor.getWriteMethod() == null || descriptor.getReadMethod() == null)
                continue;
            Field field = EntityUtils.getEntityField(descriptor.getName(), entityClass);
            // 忽略临时属性
            if (field.getAnnotation(Temporary.class) != null)
                continue;
            Property property = new Property(entityClass, descriptor, underscore);
            properties.add(property);
            if (property.isIdentity())
                identities.add(property);
        }
        if (properties.size() == 0)
            throw new DaoException("Class has no property available.");
        this.properties = properties.toArray(new Property[properties.size()]);
        this.identities = identities.toArray(new Property[identities.size()]);
    }

    /**
     * 获取实体类型。
     */
    public Class getEntityClass() {
        return entityClass;
    }

    /**
     * 获取实体在Sql中的名称。
     */
    public String getSqlName() {
        return sqlName;
    }

    /**
     * 获取实体中可用的属性。
     */
    public Property[] getProperties() {
        return properties;
    }

    /**
     * 获取实体中的ID属性。
     */
    public Property[] getIdentities() {
        return identities;
    }

}
