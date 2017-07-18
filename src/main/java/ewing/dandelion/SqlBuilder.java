package ewing.dandelion;

import java.util.ArrayList;
import java.util.List;

/**
 * 快速方便的Sql构建工具。
 */
public class SqlBuilder {

    private StringBuilder builder;
    private List<Object> params;

    public SqlBuilder() {
        this.builder = new StringBuilder(32);
        this.params = new ArrayList<>();
    }

    public SqlBuilder(String sql) {
        this.builder = new StringBuilder(sql);
        this.params = new ArrayList<>();
    }

    /**
     * 获取Sql语句。
     */
    @Override
    public String toString() {
        return this.builder.toString();
    }

    /**
     * 获取参数数组。
     */
    public Object[] getParams() {
        return this.params.toArray(new Object[params.size()]);
    }

    /**
     * 判断参数是否有值。
     */
    public static boolean hasValue(Object value) {
        return value != null && (!(value instanceof String)
                || ((String) value).length() > 0);
    }

    /**
     * 追加Sql语句。
     */
    public SqlBuilder appendSql(String sqlPart) {
        this.builder.append(sqlPart);
        return this;
    }

    /**
     * 添加参数。
     */
    public SqlBuilder appendParams(Object... params) {
        for (Object param : params)
            this.params.add(param);
        return this;
    }

    /**
     * 追加Sql语句并添加参数。
     */
    public SqlBuilder appendSqlParams(String sqlPart, Object... params) {
        this.builder.append(sqlPart);
        for (Object param : params)
            this.params.add(param);
        return this;
    }

    /**
     * 当参数有值时追加Sql语句并添加参数。
     *
     * @return 是否添加成功，参数有值则添加。
     */
    public boolean appendHasValue(String sqlPart, Object param) {
        if (sqlPart != null && hasValue(param)) {
            this.builder.append(sqlPart);
            this.params.add(param);
            return true;
        }
        return false;
    }

    /**
     * 当存在参数时扩展Sql并添加参数，sqlPart扩展为sqlPart+(?,?,?)格式。
     *
     * @return 是否添加成功，参数不为空则添加。
     */
    public boolean extendSqlParams(String sqlPart, Object... params) {
        if (sqlPart != null && params != null) {
            StringBuilder list = new StringBuilder();
            for (Object param : params) {
                if (list.length() > 0)
                    list.append(',');
                list.append('?');
                this.params.add(param);
            }
            if (list.length() > 0) {
                this.builder.append(sqlPart).append('(')
                        .append(list).append(')');
                return true;
            }
        }
        return false;
    }

    /**
     * 当参数存在且有值时扩展Sql并添加参数，sqlPart扩展为sqlPart+(?,?,?)格式。
     *
     * @return 是否添加成功，参数有值则添加。
     */
    public boolean extendHasValues(String sqlPart, Object... params) {
        if (sqlPart != null && params != null) {
            StringBuilder list = new StringBuilder();
            for (Object param : params) {
                if (hasValue(param)) {
                    if (list.length() > 0)
                        list.append(',');
                    list.append('?');
                    this.params.add(param);
                }
            }
            if (list.length() > 0) {
                this.builder.append(sqlPart).append('(')
                        .append(list).append(')');
                return true;
            }
        }
        return false;
    }

    /**
     * 当参数有值时追加Like语句并且以参数开头，即param+%。
     *
     * @return 是否添加成功，参数有值则添加。
     */
    public boolean appendStartWith(String sqlPart, String param) {
        if (sqlPart != null && param != null && param.length() > 0) {
            this.builder.append(sqlPart);
            this.params.add(param + "%");
            return true;
        }
        return false;
    }

    /**
     * 当参数有值时追加Like语句并且以参数结束，即%+param。
     *
     * @return 是否添加成功，参数有值则添加。
     */
    public boolean appendEndWith(String sqlPart, String param) {
        if (sqlPart != null && param != null && param.length() > 0) {
            this.builder.append(sqlPart);
            this.params.add("%" + param);
            return true;
        }
        return false;
    }

    /**
     * 当参数有值时追加Like语句并且包含参数，即%+param+%。
     *
     * @return 是否添加成功，参数有值则添加。
     */
    public boolean appendContains(String sqlPart, String param) {
        if (sqlPart != null && param != null && param.length() > 0) {
            this.builder.append(sqlPart);
            this.params.add("%" + param + "%");
            return true;
        }
        return false;
    }

}
