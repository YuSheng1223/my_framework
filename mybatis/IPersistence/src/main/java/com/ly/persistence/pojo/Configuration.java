package com.ly.persistence.pojo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/***
 * 全局配置类
 */
public class Configuration {
    /***
     * 封装配置文件中的数据源
     */
    private DataSource dataSource;
    /***
     * key ： statementId(bnameSpace和sqlId组成的sql唯一标识) ，value ： MappedStatement 对象
     */
    private Map<String, MappedStatement> mappedStatementMap = new HashMap<>();


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, MappedStatement> getMappedStatementMap() {
        return mappedStatementMap;
    }

    public void setMappedStatementMap(Map<String, MappedStatement> mappedStatementMap) {
        this.mappedStatementMap = mappedStatementMap;
    }
}
