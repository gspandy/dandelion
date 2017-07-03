package com.ewing.dandelion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 快速方便的Sql构建工具。
 */
public class SqlBuilder {

    private StringBuilder builder;
    private List<Object> params;

    private static boolean hasValue(Object object) {
        return object != null && (!(object instanceof String)
                || ((String) object).trim().length() > 0);
    }

    public SqlBuilder() {
        this.builder = new StringBuilder(32);
        this.params = new ArrayList<>(10);
    }

    public SqlBuilder(String sql) {
        this.builder = new StringBuilder(sql);
        this.params = new ArrayList<>(10);
    }

    public SqlBuilder(String sql, Object... params) {
        this.builder = new StringBuilder(sql);
        this.params = Arrays.asList(params);
    }

    public String getSql() {
        return builder.toString();
    }

    public Object[] getParams() {
        return this.params.toArray(new Object[params.size()]);
    }

    /**
     * 追加Sql子句并添加参数到参数列表。
     */
    public void append(String sqlPart, Object param) {
        this.builder.append(sqlPart);
        this.params.add(param);
    }

    /**
     * 当参数存在时追加Sql语句并添加参数。
     */
    public void appendExists(String sqlPart, Object param) {
        if (sqlPart != null && param != null)
            this.append(sqlPart, param);
    }

    /**
     * 当参数有值时追加Sql语句并添加参数。
     */
    public void appendHasParam(String sqlPart, Object param) {
        if (sqlPart != null && hasValue(param))
            this.append(sqlPart, param);
    }

    /**
     * 当存在参数时扩展Sql并添加参数，Sql扩展为 sqlPart + (?,?,?)格式。
     */
    public SqlBuilder appendParams(String sqlPart, Object... params) {
        if (sqlPart == null) return this;
        StringBuilder list = new StringBuilder();
        for (Object param : params) {
            if (list.length() > 0)
                list.append(',');
            list.append('?');
            this.params.add(param);
        }
        if (list.length() > 0)
            this.builder.append(sqlPart).append('(')
                    .append(list).append(')');
        return this;
    }

    /**
     * 当参数存在且有值时扩展Sql并添加参数，Sql扩展为 sqlPart + (?,?,?)格式。
     */
    public SqlBuilder appendHasParams(String sqlPart, Object... params) {
        if (sqlPart == null) return this;
        StringBuilder list = new StringBuilder();
        for (Object param : params) {
            if (hasValue(param)) {
                if (list.length() > 0)
                    list.append(',');
                list.append('?');
                this.params.add(param);
            }
        }
        if (list.length() > 0)
            this.builder.append(sqlPart).append('(')
                    .append(list).append(')');
        return this;
    }

}
