package com.ly.persistence.pojo;

/***
 * 封装对应mapper的类
 */
public class MappedStatement {
    /***
     * sql id
     */
    private String id;
    /***
     * 参数类型
     */
    private String parameterType;
    /***
     * 结果类型
     */
    private String resultType;
    /***
     * sql语句
     */
    private String sql;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
