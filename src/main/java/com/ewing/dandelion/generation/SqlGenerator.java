package com.ewing.dandelion.generation;

import com.ewing.dandelion.DaoException;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于对象类型和属性生成Sql语句。
 *
 * @author Ewing
 * @since 2017-03-06
 **/
public class SqlGenerator {

    /**
     * 实体对象信息缓存。
     */
    private final ConcurrentHashMap<Class, EntityInfo> entityInfoCache = new ConcurrentHashMap<>();

    /**
     * 是否使用下划线命名风格。
     */
    private final boolean underscore;

    /**
     * 默认构造方法。
     */
    public SqlGenerator() {
        this.underscore = false;
    }

    /**
     * 初始化方法，可配置是否使用下划线命名风格。
     *
     * @param underscore 是否使用下划线命名风格。
     */
    public SqlGenerator(boolean underscore) {
        this.underscore = underscore;
    }

    /**
     * 获取实体对象信息。
     *
     * @param entityClass 对象类型。
     * @return 实体对象信息。
     */
    public EntityInfo getEntityInfo(Class entityClass) {
        return entityInfoCache.computeIfAbsent(entityClass,
                clazz -> new EntityInfo(clazz, underscore));
    }

    /**
     * 生成实体对象的ID。
     */
    public void generateIdentity(Object object) {
        Property[] properties = getEntityInfo(object.getClass()).getIdentities();
        for (Property property : properties) {
            // 处理ID 可能有0个或多个ID属性
            if (property.isGenerate()) {
                Class type = property.getType();
                try {
                    if (String.class == type) {
                        property.getWriteMethod().invoke(object, GlobalIdWorker.nextString());
                    } else if (BigInteger.class == type) {
                        property.getWriteMethod().invoke(object, GlobalIdWorker.nextBigInteger());
                    } else {
                        throw new DaoException("Can not generate this identity type.");
                    }
                } catch (ReflectiveOperationException e) {
                    throw new DaoException("Generate identity failed.", e);
                }
            }
        }
    }

    /**
     * 生成与Class对应的结果列，可指定表别名。
     */
    public String getResultColumns(Class clazz, String tableAlias) {
        StringBuilder columns = new StringBuilder(32);
        Property[] properties = getEntityInfo(clazz).getProperties();
        for (Property property : properties) {
            // 添加到结果列表
            if (columns.length() > 0)
                columns.append(',');
            if (tableAlias != null)
                columns.append(tableAlias).append('.');
            columns.append(property.getSqlName());
        }
        return columns.toString();
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
        StringBuilder columns = new StringBuilder(32);
        Property[] properties = getEntityInfo(config.getClass()).getProperties();
        for (Property property : properties) {
            // 添加到结果列表 ID属性必须添加
            if (property.isIdentity() || EntityUtils.isPositive(property, config) == positive) {
                if (columns.length() > 0)
                    columns.append(',');
                columns.append(property.getSqlName());
            }
        }
        if (columns.length() == 0)
            throw new DaoException("No property available.");
        return columns.toString();
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
        StringBuilder columns = new StringBuilder(32);
        StringBuilder values = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getProperties();
        for (Property property : properties) {
            // 添加属性到插入列表
            if (columns.length() > 0) {
                columns.append(',');
                values.append(',');
            }
            columns.append(property.getSqlName());
            values.append(':').append(property.getName());
        }
        if (columns.length() == 0)
            throw new DaoException("No property available.");
        return "INSERT INTO " + entityInfo.getSqlName() +
                " (" + columns + ") VALUES (" + values + ")";
    }

    /**
     * 生成与配置类对应的Insert语句。
     */
    public String getInsertByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder columns = new StringBuilder(32);
        StringBuilder values = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getProperties();
        for (Property property : properties) {
            // 添加属性到插入列表 ID属性必须插入
            if (property.isIdentity() || EntityUtils.isPositive(property, config) == positive) {
                if (columns.length() > 0) {
                    columns.append(',');
                    values.append(',');
                }
                columns.append(property.getSqlName());
                values.append(':').append(property.getName());
            }
        }
        if (columns.length() == 0)
            throw new DaoException("No property available.");
        return "INSERT INTO " + entityInfo.getSqlName() +
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
        return "DELETE FROM " + getEntityInfo(clazz).getSqlName() + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Delete语句带ID条件。
     */
    public String getDeleteIdEquals(Class clazz) {
        StringBuilder identities = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getIdentities();
        for (Property property : properties) {
            // 添加ID属性到查询条件
            if (identities.length() > 0)
                identities.append(" AND ");
            identities.append(property.getSqlName()).append("=?");
        }
        return "DELETE FROM " + entityInfo.getSqlName() + " WHERE " + identities;
    }

    /**
     * 生成与Class对应的Delete语句带命名ID条件。
     */
    public String getDeleteNamedIdEquals(Class clazz) {
        StringBuilder identities = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getIdentities();
        for (Property property : properties) {
            // 添加ID属性到查询条件
            if (identities.length() > 0)
                identities.append(" AND ");
            identities.append(property.getSqlName())
                    .append("=:").append(property.getName());
        }
        return "DELETE FROM " + entityInfo.getSqlName() + " WHERE " + identities;
    }

    /**
     * 生成与配置类的属性对应的Select语句带ID条件。
     */
    public String getCountWhereTrue(Class clazz) {
        return "SELECT COUNT(*) FROM " + getEntityInfo(clazz).getSqlName() + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Select主体。
     */
    public String getSelectWhereTrue(Class clazz) {
        return "SELECT " + getResultColumns(clazz) + " FROM " +
                getEntityInfo(clazz).getSqlName() + " WHERE 1=1";
    }

    /**
     * 生成与配置类的积极属性对应的Select主体。
     */
    public String getSelectPositiveWhereTrue(Object config) {
        return "SELECT " + getPositiveColumns(config) + " FROM " +
                getEntityInfo(config.getClass()).getSqlName() + " WHERE 1=1";
    }

    /**
     * 生成与配置类的消极属性对应的Select主体。
     */
    public String getSelectNegativeWhereTrue(Object config) {
        return "SELECT " + getNegativeColumns(config) + " FROM " +
                getEntityInfo(config.getClass()).getSqlName() + " WHERE 1=1";
    }

    /**
     * 生成与Class对应的Select语句带ID条件。
     */
    public String getSelectWhereIdEquals(Class clazz) {
        StringBuilder columns = new StringBuilder(32);
        StringBuilder identities = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getProperties();
        for (Property property : properties) {
            // 添加属性到查询结果
            if (columns.length() > 0)
                columns.append(',');
            columns.append(property.getSqlName());
            // 添加到ID查询条件
            if (property.isIdentity()) {
                if (identities.length() > 0)
                    identities.append(" AND ");
                identities.append(property.getSqlName()).append("=?");
            }
        }
        return "SELECT " + columns + " FROM "
                + entityInfo.getSqlName() + " WHERE " + identities;
    }

    /**
     * 生成与Class对应的Select语句带批量ID条件。
     */
    public String getSelectWhereBatchIds(Class clazz, int length) {
        StringBuilder identities = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getIdentities();
        // 当只有一个ID属性时用IN查询
        if (properties.length == 1) {
            while (length-- > 0) {
                if (identities.length() > 0) {
                    identities.append(',');
                } else {
                    identities.append(properties[0].getSqlName()).append(" IN (");
                }
                identities.append('?');
            }
            identities.append(')');
        } else { // 当有多个ID属性时用OR查询
            while (length-- > 0) {
                if (identities.length() > 0)
                    identities.append(" OR ");
                identities.append('(');
                StringBuilder identity = new StringBuilder(32);
                for (Property property : properties) {
                    if (identity.length() > 0)
                        identity.append(" AND ");
                    identity.append(property.getSqlName()).append("=?");
                }
                identities.append(identity).append(')');
            }
        }
        return "SELECT " + getResultColumns(clazz) + " FROM "
                + entityInfo.getSqlName() + " WHERE " + identities;
    }

    /**
     * 生成与配置类的属性对应的Select语句带ID条件。
     */
    public String getSelectByConfig(Object config, boolean positive) {
        Class clazz = config.getClass();
        StringBuilder columns = new StringBuilder(32);
        StringBuilder identities = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getProperties();
        for (Property property : properties) {
            // 添加属性到查询结果
            if (property.isIdentity() || EntityUtils.isPositive(property, config) == positive) {
                if (columns.length() > 0)
                    columns.append(',');
                columns.append(property.getSqlName());
            }
            // 添加到ID查询条件
            if (property.isIdentity()) {
                if (identities.length() > 0)
                    identities.append(" AND ");
                identities.append(property.getSqlName()).append("=?");
            }
        }
        return "SELECT " + columns + " FROM " + entityInfo.getSqlName() + " WHERE " + identities;
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
        StringBuilder updates = new StringBuilder(32);
        StringBuilder identities = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getProperties();
        for (Property property : properties) {
            if (property.isIdentity()) {
                // ID添加到更新条件
                if (identities.length() > 0)
                    identities.append(" AND ");
                identities.append(property.getSqlName())
                        .append("=:").append(property.getName());
            } else if (EntityUtils.isPositive(property, config) == positive) {
                // 添加到要更新的字段
                if (updates.length() > 0)
                    updates.append(',');
                updates.append(property.getSqlName())
                        .append("=:").append(property.getName());
            }
        }
        if (updates.length() == 0)
            throw new DaoException("No property need to update.");
        return "UPDATE " + entityInfo.getSqlName() + " SET " + updates + " WHERE " + identities;
    }

    /**
     * 生成与Class对应的Update语句。
     */
    public String getUpdateWhereIdEquals(Class clazz) {
        StringBuilder updates = new StringBuilder(32);
        StringBuilder identities = new StringBuilder(32);
        EntityInfo entityInfo = getEntityInfo(clazz);
        Property[] properties = entityInfo.getProperties();
        for (Property property : properties) {
            if (property.isIdentity()) {
                // ID添加到更新条件
                if (identities.length() > 0)
                    identities.append(" AND ");
                identities.append(property.getSqlName())
                        .append("=:").append(property.getName());
            } else {
                // 添加到要更新的字段
                if (updates.length() > 0)
                    updates.append(',');
                updates.append(property.getSqlName())
                        .append("=:").append(property.getName());
            }
        }
        if (updates.length() == 0)
            throw new DaoException("No property need to update.");
        return "UPDATE " + entityInfo.getSqlName() + " SET " + updates + " WHERE " + identities;
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
