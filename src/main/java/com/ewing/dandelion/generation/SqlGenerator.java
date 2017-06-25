package com.ewing.dandelion.generation;

import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于对象类型和属性生成Sql语句。
 *
 * @author Ewing
 * @since 2017-03-06
 **/
@Component
public class SqlGenerator {

    /**
     * 实体对象信息缓存。
     */
    private ConcurrentHashMap<Class, EntityInfo> entityInfoCache = new ConcurrentHashMap<>();

    /**
     * Sql中的名称处理器。
     */
    private final NameHandler nameHandler;

    /**
     * 默认构造方法。
     */
    public SqlGenerator() {
        this.nameHandler = new NameHandler();
    }

    /**
     * 初始化方法，可配置Sql中的名称处理器。
     *
     * @param nameHandler Sql中的名称处理器。
     */
    public SqlGenerator(NameHandler nameHandler) {
        this.nameHandler = nameHandler;
    }

    /**
     * 获取实体对象信息。
     *
     * @param clazz 对象类型。
     * @return 实体对象信息。
     */
    public EntityInfo getEntityInfo(Class clazz) {
        EntityInfo entityInfo = entityInfoCache.get(clazz);
        if (entityInfo == null) {
            entityInfo = new EntityInfo(clazz);
            entityInfoCache.put(clazz, entityInfo);
        }
        return entityInfo;
    }

    /**
     * 生成实体对象的ID。
     */
    public void generateIdentity(Object object) {
        EntityInfo entityInfo = getEntityInfo(object.getClass());
        List<Field> fields = entityInfo.getIdentityFields();
        List<PropertyDescriptor> properties = entityInfo.getIdentityProperties();
        for (int i = 0; i < fields.size(); i++) {
            // 处理ID 可能有0个或多个ID属性
            PropertyUtils.resolveIdentity(fields.get(i), properties.get(i), object);
        }
    }

    /**
     * 生成与Class对应的结果列，可指定表别名。
     */
    public String getResultColumns(Class clazz, String tableAlias) {
        StringBuilder builder = new StringBuilder();
        List<Field> fields = getEntityInfo(clazz).getFields();
        for (Field field : fields) {
            if (builder.length() > 0)
                builder.append(",");
            if (tableAlias != null)
                builder.append(tableAlias).append(".");
            builder.append(nameHandler.convertName(field.getName()));
        }
        return builder.toString();
    }

    /**
     * 生成与Class对应的结果列。
     */
    public String getResultColumns(Class clazz) {
        return getResultColumns(clazz, null);
    }

    /**
     * 生成与配置类的属性对应的结果列。
     */
    public String getColumnsByConfig(Object config, boolean positive) {
        StringBuilder builder = new StringBuilder();
        EntityInfo entityInfo = getEntityInfo(config.getClass());
        List<Field> fields = entityInfo.getFields();
        List<PropertyDescriptor> properties = entityInfo.getProperties();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (PropertyUtils.isPositive(properties.get(i), config) == positive) {
                if (builder.length() > 0)
                    builder.append(",");
                builder.append(nameHandler.convertName(field.getName()));
            }
        }
        return builder.toString();
    }

    /**
     * 生成与配置类的积极属性对应的结果列。
     */
    public String getPositiveColumns(Object config) {
        return getColumnsByConfig(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的结果列。
     */
    public String getNegativeColumns(Object config) {
        return getColumnsByConfig(config, false);
    }

    /**
     * 生成与实例对应的Insert语句。
     */
    public String getInsertValues(Class clazz) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Field> fields = getEntityInfo(clazz).getFields();
        for (Field field : fields) {
            // 添加属性到插入列表
            if (columns.length() > 0) {
                columns.append(",");
                values.append(",");
            }
            columns.append(nameHandler.getSqlName(field));
            values.append(":").append(field.getName());
        }
        return "INSERT INTO " + nameHandler.getSqlName(clazz) +
                " (" + columns + ") VALUES (" + values + ")";
    }

    /**
     * 生成与配置类对应的Insert语句。
     */
    public String getInsertByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        EntityInfo entityInfo = getEntityInfo(clazz);
        List<Field> fields = entityInfo.getFields();
        List<PropertyDescriptor> properties = entityInfo.getProperties();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            // 添加属性到插入列表
            if (PropertyUtils.isIdentity(field) || PropertyUtils.isPositive(properties.get(i), config) == positive) {
                if (columns.length() > 0) {
                    columns.append(",");
                    values.append(",");
                }
                columns.append(nameHandler.getSqlName(field));
                values.append(":").append(field.getName());
            }
        }
        return "INSERT INTO " + nameHandler.getSqlName(clazz) +
                " (" + columns + ") VALUES (" + values + ")";
    }

    /**
     * 生成与配置类积极属性对应的Insert语句。
     */
    public String getInsertPositive(Object config) {
        return getInsertByConfig(config, true);
    }

    /**
     * 生成与配置类积极属性对应的Insert语句。
     */
    public String getInsertNegative(Object config) {
        return getInsertByConfig(config, false);
    }

    /**
     * 生成与Class对应的Delete语句。
     */
    public String getDeleteWhereTrue(Class clazz) {
        return "DELETE FROM " + nameHandler.getSqlName(clazz) + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Delete语句带ID条件。
     */
    public String getDeleteIdEquals(Class clazz) {
        List<Field> fields = getEntityInfo(clazz).getIdentityFields();
        StringBuilder idBuilder = new StringBuilder();
        for (Field field : fields) {
            // 添加ID属性到查询条件
            if (idBuilder.length() > 0)
                idBuilder.append(" AND ");
            idBuilder.append(nameHandler.getSqlName(field)).append("=?");
        }
        return "DELETE FROM " + nameHandler.getSqlName(clazz) + " WHERE " + idBuilder;
    }

    /**
     * 生成与Class对应的Delete语句带命名ID条件。
     */
    public String getDeleteNamedIdEquals(Class clazz) {
        List<Field> fields = getEntityInfo(clazz).getIdentityFields();
        StringBuilder idBuilder = new StringBuilder();
        for (Field field : fields) {
            // 添加ID属性到查询条件
            if (idBuilder.length() > 0)
                idBuilder.append(" AND ");
            idBuilder.append(nameHandler.getSqlName(field)).append("=");
            idBuilder.append(":").append(field.getName());
        }
        return "DELETE FROM " + nameHandler.getSqlName(clazz) + " WHERE " + idBuilder;
    }

    /**
     * 生成与配置类的属性对应的Select语句带ID条件。
     */
    public String getCountWhereTrue(Class clazz) {
        return "SELECT COUNT(*) FROM " + nameHandler.getSqlName(clazz) + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Select主体。
     */
    public String getSelectWhereTrue(Class clazz) {
        return "SELECT " + getResultColumns(clazz) + " FROM " +
                nameHandler.getSqlName(clazz) + " WHERE 1=1";
    }

    /**
     * 生成与配置类的积极属性对应的Select主体。
     */
    public String getSelectPositiveWhereTrue(Object config) {
        return "SELECT " + getPositiveColumns(config) + " FROM " +
                nameHandler.getSqlName(config.getClass()) + " WHERE 1=1";
    }

    /**
     * 生成与配置类的消极属性对应的Select主体。
     */
    public String getSelectNegativeWhereTrue(Object config) {
        return "SELECT " + getNegativeColumns(config) + " FROM " +
                nameHandler.getSqlName(config.getClass()) + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Select语句带ID条件。
     */
    public String getSelectWhereIdEquals(Class clazz) {
        StringBuilder columns = new StringBuilder();
        StringBuilder identities = new StringBuilder();
        EntityInfo entityInfo = getEntityInfo(clazz);
        List<Field> fields = entityInfo.getFields();
        for (Field field : fields) {
            // 添加属性到查询结果
            if (columns.length() > 0)
                columns.append(",");
            columns.append(nameHandler.getSqlName(field));
        }
        List<Field> identityFields = entityInfo.getIdentityFields();
        for (Field field : identityFields) {
            // 添加到ID查询条件
            if (identities.length() > 0)
                identities.append(" AND ");
            identities.append(nameHandler.getSqlName(field)).append("=?");
        }
        return "SELECT " + columns + " FROM " + nameHandler.getSqlName(clazz) + " WHERE " + identities;
    }

    /**
     * 生成与Class对应的Select语句带批量ID条件。
     */
    public String getSelectWhereBatchIds(Class clazz, int length) {
        StringBuilder identities = new StringBuilder();
        List<Field> fields = getEntityInfo(clazz).getIdentityFields();
        if (fields.size() == 1) {
            for (int i = 0; i < length; i++) {
                if (identities.length() > 0) {
                    identities.append(",");
                } else {
                    identities.append(nameHandler.getSqlName(fields.get(0))).append(" IN (");
                }
                identities.append("?");
            }
            identities.append(")");
        } else {
            for (int i = 0; i < length; i++) {
                if (identities.length() > 0)
                    identities.append(" OR ");
                identities.append("(");
                StringBuilder identity = new StringBuilder();
                for (Field field : fields) {
                    if (identity.length() > 0)
                        identity.append(" AND ");
                    identity.append(nameHandler.getSqlName(field)).append("=?");
                }
                identities.append(identity).append(")");
            }
        }
        return "SELECT " + getResultColumns(clazz) + " FROM "
                + nameHandler.getSqlName(clazz) + " WHERE " + identities;
    }

    /**
     * 生成与配置类的属性对应的Select语句带ID条件。
     */
    public String getSelectByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder columns = new StringBuilder();
        StringBuilder identities = new StringBuilder();
        EntityInfo entityInfo = getEntityInfo(clazz);
        List<Field> fields = entityInfo.getFields();
        List<PropertyDescriptor> properties = entityInfo.getProperties();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (PropertyUtils.isIdentity(field)) {
                if (identities.length() > 0)
                    identities.append(" AND ");
                identities.append(nameHandler.getSqlName(field)).append("=?");
            }
            // 添加属性到查询结果
            if (PropertyUtils.isPositive(properties.get(i), config) == positive) {
                if (columns.length() > 0)
                    columns.append(",");
                columns.append(nameHandler.getSqlName(field));
            }
        }
        return "SELECT " + columns + " FROM " + nameHandler.getSqlName(clazz) + " WHERE " + identities;
    }

    /**
     * 生成与配置类的积极属性对应的Select语句带ID条件。
     */
    public String getSelectPositiveWhereIdEquals(Object config) {
        return getSelectByConfig(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的Select语句带ID条件。
     */
    public String getSelectNegativeWhereIdEquals(Object config) {
        return getSelectByConfig(config, false);
    }

    /**
     * 生成与配置类的属性对应的Update语句。
     */
    public String getUpdateByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder updates = new StringBuilder();
        StringBuilder identities = new StringBuilder();
        EntityInfo entityInfo = getEntityInfo(clazz);
        List<Field> fields = entityInfo.getNormalFields();
        List<PropertyDescriptor> properties = entityInfo.getNormalProperties();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (PropertyUtils.isPositive(properties.get(i), config) == positive) {
                if (updates.length() > 0)
                    updates.append(",");
                updates.append(nameHandler.convertName(field.getName())).append("=:").append(field.getName());
            }
        }
        List<Field> identityFields = entityInfo.getIdentityFields();
        for (Field field : identityFields) {
            if (identities.length() > 0)
                identities.append(" AND ");
            identities.append(nameHandler.convertName(field.getName())).append("=:").append(field.getName());
        }
        return "UPDATE " + nameHandler.getSqlName(clazz) + " SET " + updates + " WHERE " + identities;
    }

    /**
     * 生成与Class对应的Update语句。
     */
    public String getUpdateWhereIdEquals(Class clazz) {
        StringBuilder updates = new StringBuilder();
        StringBuilder identities = new StringBuilder();
        EntityInfo entityInfo = getEntityInfo(clazz);
        List<Field> fields = entityInfo.getNormalFields();
        for (Field field : fields) {
            if (updates.length() > 0)
                updates.append(",");
            updates.append(nameHandler.convertName(field.getName())).append("=:").append(field.getName());
        }
        List<Field> identityFields = entityInfo.getIdentityFields();
        for (Field field : identityFields) {
            if (identities.length() > 0)
                identities.append(" AND ");
            identities.append(nameHandler.convertName(field.getName())).append("=:").append(field.getName());
        }
        return "UPDATE " + nameHandler.getSqlName(clazz) + " SET " + updates + " WHERE " + identities;
    }

    /**
     * 生成与配置类的积极属性对应的Update语句。
     */
    public String getUpdatePositiveWhereIdEquals(Object config) {
        return getUpdateByConfig(config, true);
    }

    /**
     * 生成与配置类的消极属性对应的Update语句。
     */
    public String getUpdateNegativeWhereIdEquals(Object config) {
        return getUpdateByConfig(config, false);
    }

}
